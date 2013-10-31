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

public class HttpProcessor implements Runnable{
	private Socket mSocket;
	private BufferedReader in;
	private DataOutputStream out;
	private static LocalImageDB mDB;
	private static int m;
	
	public HttpProcessor(Socket s,int c){
		mSocket = s;
		m=c;
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new DataOutputStream(s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mDB = new LocalImageDB();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			P("error");
			e1.printStackTrace();
		}
		
	}
	
	@Override
	public void run(){
		// Read HTTP request (empty line signal end of request)
		String request ="";
		String filename = "";
		StringTokenizer st = null;
		try {
			request = in.readLine();
			if(request == null){
				closeConnection(mSocket);
				P("null req");
				return;
			}
			System.out.println("request: " + request);				
			st = new StringTokenizer(request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// if its a get request
		if (st.nextToken().equals("GET"))
		{
			// This is a GET request.
			String command = st.nextToken().substring(1);
			//default direct to index page
			if (command.equals("")) 
			{
				P("index page");
				filename = "html/index.html";	
				sendFile(filename,out);
		    }
			//this is to get other page
			else if(command.indexOf('?') == -1){
				P("page: "+command);
				filename = "html/" + command;	
				sendFile(filename,out);
			}
			else{
				//this is to call search method
				long startTime = System.currentTimeMillis();
				command = command.substring(command.indexOf('?')+1);
				//this is to remove unused string
				if(command.indexOf('&') != -1){
					command = command.substring(0,command.indexOf('&'));
				}		
				P(command);		
				List<String> keys = Arrays.asList(command.split("\\+"));
				SearchResult searchResult = new SearchResult();
				List<Image> localImages = mDB.search(keys.get(0), null);
				String response = "";
				if(localImages.size()!=0){
					P("search finished");	
					long endTime = System.currentTimeMillis();
					double timeUsed = endTime - startTime;
					searchResult.setTime(timeUsed/1000);
					searchResult.addImageList(localImages);
					response = searchResult.toString();
					P(response);
				}
				else{
					List<String> urls = Arrays.asList("http://www.bbc.co.uk", "http://www.nytimes.com");
					Crawler crawler = new Crawler(urls);
					crawler.setSearchLimit(3);
					List<Image> onlineImages = crawler.search(keys.get(0));
					P("search finished");		
					long endTime = System.currentTimeMillis();
					double timeUsed = endTime - startTime;
					searchResult.setTime(timeUsed/1000);
					searchResult.addImageList(onlineImages);
					response = searchResult.toString();
					P(response);
					mDB.insert(onlineImages, keys.get(0));
				}				
				writeResponse(out,response);
				closeConnection(mSocket);
			}
		}
	}
	
	public static void writeResponse(DataOutputStream out,String s){
		try {
			out.writeBytes("HTTP/1.1 200 Okie \r\n");
			out.writeBytes("Content-type: text/html\r\n");
			out.writeBytes("\r\n");
			out.writeBytes(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void sendFile(String filename,DataOutputStream out){
		try{		
			P("send file: "+filename);
			// Open and read the file into buffer
			File f = new File(filename);
		      
			if (f.canRead())
			{
				int size = (int)f.length();
				
				//Create a File InputStrem to read the File
				FileInputStream fis = new FileInputStream(filename);
				byte[] buffer = new byte[size];
				fis.read(buffer);
				fis.close();
			
				// Now, write buffer to client
				// (but, send HTTP response header first)
				
				out.writeBytes("HTTP/1.1 200 Okie \r\n");
				out.writeBytes("Content-type: text/html\r\n");
				out.writeBytes("\r\n");
				out.write(buffer,  0, size);
			}
			else
			{
				// File cannot be read.  Reply with 404 error.
				out.writeBytes("HTTP/1.0 404 Not Found\r\n");
				out.writeBytes("\r\n");
				out.writeBytes("Cannot find " + filename + " on server");
			}
		}
		catch (Exception ex){
		}
	}
	
	public static void closeConnection(Socket s){
    	try {
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public static void P(String s){
    	System.out.println("thread"+m + ":" +s);
    }

}
