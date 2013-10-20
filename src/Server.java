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
import java.util.StringTokenizer;


public class Server {

	private static ServerSocket mServerSocket;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			mServerSocket = new ServerSocket(80);
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
			    }
				//this is to get page
				else if(command.indexOf('?') == -1){
					filename = "html/" + command;		
				}
				else{
					//this is to call some method
					command = command.substring(command.indexOf('?')+1);
					//this is to remove unused string
					if(command.indexOf('&') != -1){
						command = command.substring(0,command.indexOf('&'));
					}		
					P(command);
					//get a new word
					/*if(command.equals("getword")){	
						try {
							P("Please enter a word:");
							String word = readString();
							out.writeBytes("HTTP/1.1 200 Okie \r\n");
							out.writeBytes("Content-type: text/html\r\n");
							out.writeBytes("\r\n");							
							out.writeBytes(maskWord(word));
							//store the word sent
							recordWord(s,word);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//check ans; format is check+"guess"
					else if(command.startsWith("check")){
						String response;
						String guess = command.substring(command.indexOf("+")+1);
						response = checkAns(s, guess);
						//response to client
						try {
							out.writeBytes("HTTP/1.1 200 Okie \r\n");
							out.writeBytes("Content-type: text/html\r\n");
							out.writeBytes("\r\n");
							out.writeBytes(response);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}*/
					//if only call some method, no need to send file
					closeConnection(s);
					continue;
				}
			}
			
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

			// Close connection 
			closeConnection(s);
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
