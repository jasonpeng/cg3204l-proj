package cg3204l;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CGHTTPClient {

	public static final int DEFAULT_PORT_NUMBER = 80;

	protected URI mURI;
	protected String mHost;
	protected int mPort;
	protected String mScheme;
	protected String mAuthority;
	
	protected String mRaw;
	protected String mResponse;

	public CGHTTPClient(String url) {
		try {
			// Parse the given url
			mURI = new URI(url);
			mHost = mURI.getHost();
			mPort = mURI.getPort();
			mScheme = mURI.getScheme();
			mAuthority = mURI.getAuthority();

			if (mPort < 0) {
				mPort = DEFAULT_PORT_NUMBER;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void get() {
		try {
			// Create new socket to the server
			Socket socket = new Socket(mHost, mPort);
			String path = mURI.getPath();
			if (path.length() == 0) {
				path = "/";
			}
			
			System.out.println("Requesting " + path + " from " + mHost);

			// Send HTTP request to server
			PrintWriter pw = new PrintWriter(socket.getOutputStream());
			pw.println("GET " + path + " HTTP/1.1");
			pw.println("Host: " + mHost);
			pw.println();
			pw.flush();

			// Receive HTTP response
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()), 1024 * 4);
			int contentLength = 0;
			mRaw = "";
			mResponse = "";
			String line;
			boolean chunkedFlag = false;
			
			while ((line = br.readLine()) != null) {
				// Record the raw response
				mRaw = mRaw + line + "\n";

				// Content-Length:
				Pattern contentLengthPattern = Pattern
						.compile("Content\\-Length: (\\d+)");
				Matcher contentLengthMatcher = contentLengthPattern
						.matcher(line);
				if (contentLengthMatcher.find()) {
					contentLength = new Integer(contentLengthMatcher.group(1));
					System.out.println("Content-Length: " + contentLength);
				}
				
				// Transfer-Encoding: chunked
				Pattern chunkedPattern = Pattern
						.compile("Transfer\\-Encoding: chunked");
				Matcher chunkedMatcher = chunkedPattern
						.matcher(line);
				if (chunkedMatcher.find()) {
					chunkedFlag = true;
					System.out.println("Transfer-Encoding: chunked");
				}
				
				// End of HTTP Header
				if (line.equals("")) {
					break;
				}
			}
			
			// Receive contents
			if (contentLength > 0) {
				char[] buffer = new char[contentLength];
				int bytes = br.read(buffer);
				mResponse = mResponse + new String(buffer);
				while (bytes < contentLength) {
					char[] missingBuffer = new char[contentLength - bytes];
					bytes = bytes + br.read(missingBuffer);
					mResponse = mResponse + new String(missingBuffer);
				}
			} else if (chunkedFlag == true) {
				int chunkLength = Integer.parseInt(br.readLine(), 16);
				while(chunkLength > 0) {
					char[] buffer = new char[chunkLength];
					int bytes = br.read(buffer);
					mResponse = mResponse + new String(buffer);
					while (bytes < chunkLength) {
						char[] missingBuffer = new char[chunkLength - bytes];
						bytes = bytes + br.read(missingBuffer);
						mResponse = mResponse + new String(missingBuffer);
					}
					
					br.readLine();
					
					chunkLength = Integer.parseInt(br.readLine(), 16);
				}
			}
			
			br.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getRaw() {
		return mRaw;
	}

	public String getResponse() {
		return mResponse;
	}
	
	public String getScheme() {
		return mScheme;
	}
	
	public String getAuthority() {
		return mAuthority;
	}

}
