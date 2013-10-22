package cg3204l;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.nodes.Document;

import cg3204l.model.Image;
import cg3204l.model.Link;


/**
 * 
 * Crawler class for crawling web pages
 * 
 */
public class Crawler {

	protected static final long DEFALT_SEARCH_LIMIT = 10;
	protected static final int MAX_THREADS = 10;
	protected static final int BATCH_SIZE = 5;

	protected List<String> mInitURL;
	protected long mSearchLimit;
	protected Parser mParser;
	protected Analyzer mAnalyzer;

	/**
	 * Crawler constructor initializes with a list of urls and sets default
	 * search limit
	 * 
	 * @param initURL
	 *            a List of URL String to start crawling
	 */
	public Crawler(List<String> initURL) {
		this.mInitURL = initURL;
		this.mSearchLimit = Crawler.DEFALT_SEARCH_LIMIT;

		this.mParser = new Parser();
		this.mAnalyzer = new Analyzer();
	}
	
	public void setInitURL(List<String> initURL) {
		this.mInitURL = initURL;
	}

	/**
	 * Search images with a keyword
	 * 
	 * @param keyword
	 *            a String keyword to search
	 * @return List of Image
	 */
	public List<Image> search(String keyword) {
		Set<Image> resultImageSet = new TreeSet<Image>();
		Set<String> uniqueURL = new TreeSet<String>(mInitURL);
		List<String> URLQueue = new ArrayList<String>(uniqueURL);
		int queueIndex = 0; 

		// recursively crawl links starting with mInitURL
		// until number of images reaches mSearchLimit
		while (resultImageSet.size() < this.mSearchLimit) {
			if (queueIndex >= URLQueue.size()) {
				// no more url to fetch, exit
				break;
			}
			
			List<Future<Document>> futureList = new ArrayList<Future<Document>>();
			ExecutorService executor = Executors
					.newFixedThreadPool(Crawler.MAX_THREADS);
			
			for (int i = 0; i < Crawler.BATCH_SIZE; i++) {
				// get the head element from the queue
				String url = URLQueue.get(queueIndex);
				
				// send url to callable
				Callable<Document> worker = new FetcherCallable(url);
				Future<Document> future = executor.submit(worker);
				futureList.add(future);
				
				queueIndex++;
				if (queueIndex >= URLQueue.size()) {
					break;
				}
			}

			for (Future<Document> future : futureList) {
				try {
					Document doc = future.get();
					
					if (doc != null) {				
						// parse the HTML content
						mParser.parse(doc);
						List<Image> pageImages = mParser.getImages();
						List<Link> pageLinks = mParser.getLinks();
						
						// analyze content relevance
						analyzeRelevance(pageImages, pageLinks, keyword);
	
						// add related images to result
						resultImageSet.addAll(pageImages);
						
						// add related links to queue
						List<String> linkURLs = new ArrayList<String>();
						for (Link link : pageLinks) {
							linkURLs.add(link.getHref());
						}
						
						for (String url : linkURLs) {
							// add url to queue only if it's new
							if (uniqueURL.add(url)) {
								URLQueue.add(url);
							}
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			
			executor.shutdown();
		}

		List<Image> resultImageList = new ArrayList<Image>(resultImageSet);
		return resultImageList;
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

	/**
	 * SearchLimit field setter
	 * 
	 * @param searchLimit
	 */
	public void setSearchLimit(long searchLimit) {
		this.mSearchLimit = searchLimit;
	}

	/**
	 * SearchLimit field getter
	 * 
	 * @return
	 */
	public long getSearchLimit() {
		return this.mSearchLimit;
	}
}
