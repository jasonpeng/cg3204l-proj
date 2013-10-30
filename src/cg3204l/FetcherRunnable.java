package cg3204l;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cg3204l.model.Image;
import cg3204l.model.Link;

public class FetcherRunnable implements Runnable {
	
	protected String mURL;
	protected Crawler mCrawler;
	protected Parser mParser;
	protected Analyzer mAnalyzer;
	protected String mKeyword;
	
	public FetcherRunnable (String url, Crawler crawler, String keyword) {
		mURL = url;
		mCrawler = crawler;
		mParser = new Parser();
		mAnalyzer = new Analyzer();
		mKeyword = keyword;
	}

	@Override
	public void run() {
		CGHTTPClient client = new CGHTTPClient(mURL);
		client.get();
		String html = client.getResponse();
		Document doc = null;
		if (html != null) {
			doc = Jsoup.parse(html);
			String baseUri = client.getScheme() + "://" + client.getAuthority();
			doc.setBaseUri(baseUri);
		}
		
		if (doc != null) {				
			// parse the HTML content
			mParser.parse(doc);
			List<Image> pageImages = mParser.getImages();
			List<Link> pageLinks = mParser.getLinks();
			
			// analyze content relevance
			analyzeRelevance(pageImages, pageLinks, mKeyword);

			// add related images to result
			mCrawler.addToImageSet(pageImages);
			
			// add related links to queue
			List<String> linkURLs = new ArrayList<String>();
			for (Link link : pageLinks) {
				linkURLs.add(link.getHref());
			}
			mCrawler.addToURLSet(linkURLs);
		}
	}
	
	private void analyzeRelevance(List<Image> images, List<Link> links, String keyword) {
		Iterator<Image> imageIterator = images.iterator();
		while (imageIterator.hasNext()) {
			Image image = imageIterator.next();
			if (!mAnalyzer.checkRelevance(image, keyword)) {
				imageIterator.remove();
			}
		}
		
		Iterator<Link> linkIterator = links.iterator();
		while (linkIterator.hasNext()) {
			Link link = linkIterator.next();
			if (!mAnalyzer.checkRelevance(link, keyword)) {
				linkIterator.remove();
			}
		}
	}

}
