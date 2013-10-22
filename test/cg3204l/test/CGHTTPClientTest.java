package cg3204l.test;

import cg3204l.CGHTTPClient;

public class CGHTTPClientTest {

	public static void main(String[] args) {
		CGHTTPClient client = new CGHTTPClient("http://www.nus.edu.sg/cors/images/menubar/home-selected.jpg");
		System.out.println(client.getResponse());
	}

}
