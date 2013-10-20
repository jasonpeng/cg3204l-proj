import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.nodes.Document;

import model.Image;
import model.Link;

/**
 * 
 * Crawler class for crawling web pages
 * 
 */
public class Crawler {

	protected static final long DEFALT_SEARCH_LIMIT = 10;
	protected static final int MAX_THREADS = 10;

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

	/**
	 * Search images with a keyword
	 * 
	 * @param keyword
	 *            a String keyword to search
	 * @return List of Image
	 */
	public List<Image> search(String keyword) {
		List<Image> resultImages = new ArrayList<Image>();
		List<String> URLQueue = new ArrayList<String>(mInitURL);

		// recursively crawl links starting with mInitURL
		// until number of images reaches mSearchLimit
		while (resultImages.size() < this.mSearchLimit) {
			// get the first url from the queue
			String url = URLQueue.get(0);
			URLQueue.remove(0);

			List<Future<Document>> futureList = new ArrayList<Future<Document>>();
			ExecutorService executor = Executors
					.newFixedThreadPool(Crawler.MAX_THREADS);
			for (int i = 0; i < 50; i++) {
				Callable<Document> worker = new FetcherCallable(url);
				Future<Document> future = executor.submit(worker);
				futureList.add(future);
			}

			for (Future<Document> future : futureList) {
				try {
					Document doc = future.get();
					// parse the HTML content
					mParser.parse(doc, url);
					List<Image> pageImages = mParser.getImages();
					List<Link> pageLinks = mParser.getLinks();
					
					// analyze content relsevance

					// add related images to result
					resultImages.addAll(pageImages);
					// add related links to queue
					List<String> linkURLs = new ArrayList<String>();
					for (Link link : pageLinks) {
						linkURLs.add(link.getHref());
					}
					URLQueue.addAll(linkURLs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			
			executor.shutdown();
		}

		return resultImages;
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
