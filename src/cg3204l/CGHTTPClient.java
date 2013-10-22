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

	protected String mRaw;
	protected String mResponse;

	public CGHTTPClient(String url) {
		try {
			// Parse the given url
			URI uri = new URI(url);
			String host = uri.getHost();
			int port = uri.getPort();

			if (port < 0) {
				port = DEFAULT_PORT_NUMBER;
			}

			try {
				// Create new socket to the server
				Socket socket = new Socket(host, port);
				String path = uri.getPath();
				if (path.length() == 0) {
					path = "/";
				}

				// Send HTTP request to server
				PrintWriter pw = new PrintWriter(socket.getOutputStream());
				pw.println("GET " + path + " HTTP/1.1");
				pw.println("Host: " + host);
				pw.println();
				pw.flush();

				// Receive HTTP response
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
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
					}
					
					// Transfer-Encoding: chunked
					Pattern chunkedPattern = Pattern
							.compile("Transfer\\-Encoding: chunked");
					Matcher chunkedMatcher = chunkedPattern
							.matcher(line);
					if (chunkedMatcher.find()) {
						chunkedFlag = true;
					}
					
					// End of HTTP Header
					if (line.equals("")) {
						break;
					}
				}
				
				// Receive contents
				if (contentLength > 0) {
					char[] buffer = new char[contentLength];
					br.read(buffer);
					mResponse = new String(buffer);
				} else if (chunkedFlag == true) {
					int chunkLength = Integer.parseInt(br.readLine(), 16);
					while(chunkLength > 0) {
						char[] buffer = new char[chunkLength];
						br.read(buffer);
						br.readLine();
						mResponse = mResponse + new String(buffer);
						
						chunkLength = Integer.parseInt(br.readLine(), 16);
					}
				}
				
				br.close();
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}
		} catch (URISyntaxException e) {

		}
	}

	public String getRaw() {
		return mRaw;
	}

	public String getResponse() {
		return mResponse;
	}

}
