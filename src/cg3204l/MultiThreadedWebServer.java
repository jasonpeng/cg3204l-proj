package cg3204l;


import java.net.*;

public class MultiThreadedWebServer {

	public static void main(String[] args) throws Exception {
		ServerSocket socket = new ServerSocket(8000);
		System.out.println("MULTITHREADED WEBSERVER IS WAITING FOR HTTP REQUEST AT " + getHostAddr() + "...");
		
		while (true) {
			Socket connectionSocket = socket.accept();
			HTTPRequest request = new HTTPRequest(connectionSocket);
			Thread thread = new Thread(request);
			thread.start();
		}
	}
	
    private static String getHostAddr(){
        String hostAddr = "";
        
        try {
                hostAddr = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
                hostAddr = "127.0.0.1";
        }
        
        return hostAddr;
}

} 
