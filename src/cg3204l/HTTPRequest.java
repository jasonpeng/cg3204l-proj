package cg3204l;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import cg3204l.model.Image;
import cg3204l.model.SearchResult;

public class HTTPRequest implements Runnable {

	private Socket mSocket;
	private LocalImageDB mDB;
	private static final List<String> travel = Arrays.asList(
			"http://www.tripadvisor.com",
			"http://travel.yahoo.com",
			"http://sg.hotels.com",
			"http://www.hotwire.com/sg",
			"http://www.lonelyplanet.com");
	private static final List<String> fashion = Arrays.asList(
			"http://www.asos.com",
			"http://www.other-shop.com",
			"http://www.styloko.com",
			"http://www.cavan.com",
			"http://cropp.com");
	private static final List<String> it = Arrays.asList(
			"http://www.engadget.com",
			"http://www.techeblog.com",
			"http://gizmodo.com",
			"http://boygeniusreport.com",
			"http://www.wired.com");
	private static final List<String> food = Arrays.asList(
			"http://www.bigoven.com",
			"http://www.food.com",
			"http://www.chocablog.com",
			"http://candccoffee.com",
			"http://www.mealeo.com");
	private static final List<String> news = Arrays.asList(
			"http://edition.cnn.com",
			"http://www.foxnews.com",
			"http://www.nytimes.com",
			"http://www.latimes.com",
			"http://www.bbc.com");
	public HTTPRequest(Socket connectionSocket) throws ClassNotFoundException {
		mSocket = connectionSocket;
		mDB = new LocalImageDB();
	}

	@Override
	public void run() {
		System.out.println("Connection established from "
				+ mSocket.getInetAddress());
		System.out.println("Connection Definition " + mSocket.toString());

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					mSocket.getInputStream()));
			DataOutputStream os = new DataOutputStream(
					mSocket.getOutputStream());

			String request = "";
			String filename = "";
			StringTokenizer st = null;

			request = br.readLine();
			if (request == null) {
				mSocket.close();
				return;
			}
			
			System.out.println("request: " + request);
			st = new StringTokenizer(request);

			if (st.nextToken().equals("GET")) {
				String command = st.nextToken().substring(1);
				if (command.equals("")) {
					filename = "html/index.html";
					sendFile(filename, os);
				} else if (command.indexOf('?') == -1) {
					filename = "html/" + command;
					sendFile(filename, os);
				} else {
					// this is to call search method
					long startTime = System.currentTimeMillis();
					command = command.substring(command.indexOf('?') + 1);
					// this is to remove unused string
					if (command.indexOf('&') != -1) {
						command = command.substring(0, command.indexOf('&'));
					}
					// get image limit
					int imageNum = Integer.parseInt(command.substring(command
							.indexOf(':') + 1));
					command = command.substring(0, command.indexOf(':'));

					String  tag = command.substring(command.indexOf('.') + 1);
					command = command.substring(0, command.indexOf('.'));
					List<String> keys = Arrays.asList(command.split("\\+"));
					SearchResult searchResult = new SearchResult();
					String response = "";
					// first search in local db
					List<Image> localImages = mDB.search(keys.get(0),
							System.currentTimeMillis() - 3600 * 24 * 1000);
					searchResult.addImageList(localImages);
					// if local images is enough
					if (localImages.size() >= imageNum) {
						long endTime = System.currentTimeMillis();
						double timeUsed = endTime - startTime;
						searchResult.setTime(timeUsed / 1000);
						response = searchResult.toString();
						System.out.println(response);
					}
					// else need online searching
					else {
						int limit = imageNum;
						List<String> urls;
						System.out.println("tag: " + tag);
						if(tag.equals("news")){
							urls = news;
						}
						else if(tag.equals("food")){
							urls = food;
						}
						else if(tag.equals("it")){
							urls = it;
						}
						else if(tag.equals("fashion")){
							urls = fashion;
						}
						else if(tag.equals("travel")){
							urls = travel;
						}
						else{
							urls = Arrays.asList(
									"http://www.bbc.co.uk",
									"http://www.nytimes.com",
									"http://www.engadget.com");
						}
						
						Crawler crawler = new Crawler(urls);
						crawler.setSearchLimit(limit);
						List<Image> onlineImages = crawler.search(keys.get(0));
						long endTime = System.currentTimeMillis();
						double timeUsed = endTime - startTime;
						SearchResult sr = new SearchResult();
						sr.setTime(timeUsed / 1000);
						sr.addImageList(onlineImages);
						response = sr.toString();
						System.out.println(response);
						mDB.insert(onlineImages, keys.get(0));
					}
					writeResponse(os, response);
					mSocket.close();
				}
			}
			br.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendFile(String filename, DataOutputStream out) {
		try {
			// Open and read the file into buffer
			File f = new File(filename);

			if (f.canRead()) {
				int size = (int) f.length();

				// Create a File InputStrem to read the File
				FileInputStream fis = new FileInputStream(filename);
				byte[] buffer = new byte[size];
				fis.read(buffer);
				fis.close();

				// Now, write buffer to client
				// (but, send HTTP response header first)

				out.writeBytes("HTTP/1.1 200 OK\r\n");
				out.writeBytes("Content-type: text/html\r\n");
				out.writeBytes("\r\n");
				out.write(buffer, 0, size);
			} else {
				// File cannot be read. Reply with 404 error.
				out.writeBytes("HTTP/1.1 404 Not Found\r\n");
				out.writeBytes("\r\n");
				out.writeBytes("Cannot find " + filename + " on server");
			}
		} catch (Exception ex) {
		}
	}
	
    public void writeResponse(DataOutputStream out,String s){
        try {
                out.writeBytes("HTTP/1.1 200 OK\r\n");
                out.writeBytes("Content-Type: text/html\r\n");
                out.writeBytes("\r\n");
                out.writeBytes(s);
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        
}

}
