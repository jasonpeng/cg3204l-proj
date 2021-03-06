package cg3204l;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import cg3204l.model.Image;
import cg3204l.model.SearchResult;



public class Server {

        private static ServerSocket mServerSocket;
        private static LocalImageDB mDB;
        public static void main(String[] args) {                
                try {
                        mDB = new LocalImageDB();
                } catch (ClassNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                }
                // TODO Auto-generated method stub
                try {
                        mServerSocket = new ServerSocket(8000);
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                System.out.println("SERVER IS WAITING FOR HTTP REQUEST AT " + getHostAddr() + "...");
                
                while (true) 
                {
                        //Listen & Accept Connection and Create new CONNECTION SOCKET
                        Socket s = null;
                        BufferedReader in = null;
                        DataOutputStream out = null;
                        try {
                                s = mServerSocket.accept();
                                System.out.println("connection established from " + s.getInetAddress());
                                System.out.println("Connection Definition " + s.toString());
                                
                                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                                out = new DataOutputStream(s.getOutputStream());
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }                
                                                
                        // Read HTTP request (empty line signal end of request)
                        String request ="";
                        String filename = "";
                        StringTokenizer st = null;
                        try {
                                request = in.readLine();
                                if(request == null){
                                        closeConnection(s);
                                        continue;
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
                                //
                                if (command.equals("")) 
                                {
                                        filename = "html/index.html";        
                                        sendFile(filename,out);
                            }
                                //this is to get page
                                else if(command.indexOf('?') == -1){
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
                                        //get image limit
                                        int imageNum = Integer.parseInt(command.substring(command.indexOf(':')+1));
                                        command = command.substring(0,command.indexOf(':'));
                                        P(Integer.toString(imageNum));
                                        
                                        List<String> keys = Arrays.asList(command.split("\\+"));
                                        SearchResult searchResult = new SearchResult();
                                        String response = "";
                                        //first search in local db
                                        List<Image> localImages = mDB.search(keys.get(0), System.currentTimeMillis() - 3600 * 24 * 1000);
                                        searchResult.addImageList(localImages);
                                        //if local images is enough
                                        if(localImages.size() >= imageNum){
                                                P("search finished");        
                                                long endTime = System.currentTimeMillis();
                                                double timeUsed = endTime - startTime;
                                                searchResult.setTime(timeUsed/1000);                   
                                                response = searchResult.toString();
                                                P(response);
                                        }
                                        //else need online searching
                                        else{
                                        		int limit = imageNum;
                                                List<String> urls = Arrays.asList("http://www.bbc.co.uk", "http://www.nytimes.com");
                                                Crawler crawler = new Crawler(urls);
                                                crawler.setSearchLimit(limit);
                                                List<Image> onlineImages = crawler.search(keys.get(0));
                                                P("search finished");                
                                                long endTime = System.currentTimeMillis();
                                                double timeUsed = endTime - startTime;
                                                SearchResult sr = new SearchResult();
                                                sr.setTime(timeUsed/1000);
                                                sr.addImageList(onlineImages);
                                                response = sr.toString();
                                                P(response);
                                                mDB.insert(onlineImages, keys.get(0));
                                        }                                
                                        writeResponse(out,response);
                                        closeConnection(s);
                                        continue;
                                }
                        }
                        // Close connection 
                        closeConnection(s);
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
    
    public static String getHostAddr(){
                String hostAddr = "";
                try {
                        hostAddr = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                        hostAddr = "127.0.0.1";
                }
                return hostAddr;
    }
    
    
    public static void P(String s){
            System.out.println(s);
    }
}