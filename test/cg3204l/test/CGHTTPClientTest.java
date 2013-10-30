package cg3204l.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cg3204l.CGHTTPClient;

public class CGHTTPClientTest {

	public static void main(String[] args) {
		CGHTTPClient client = new CGHTTPClient("http://www.bbc.co.uk/");
		client.get();
		String html = client.getResponse();
		System.out.println(client.getResponse());
		Document doc = Jsoup.parse(html);
	}

}
