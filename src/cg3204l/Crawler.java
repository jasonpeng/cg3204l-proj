package cg3204l;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cg3204l.model.Image;


/**
 * 
 * Crawler class for crawling web pages
 * 
 */
public class Crawler {

	protected static final long DEFALT_SEARCH_LIMIT = 10;
	protected static final int MAX_THREADS = 50;
	protected static final int BATCH_SIZE = 50;

	protected List<String> mInitURL;
	protected long mSearchLimit;
	protected Analyzer mAnalyzer;
	protected Set<Image> mImageSet;
	protected Set<String> mURLSet;
	protected List<String> mURLQueue;

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
		this.mImageSet = new HashSet<Image>();
		this.mURLSet = new HashSet<String>();
		this.mURLQueue = new ArrayList<String>();
		
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
		int queueIndex = 0; 
		
		mURLSet.addAll(this.mInitURL);
		mURLQueue.addAll(this.mInitURL);

		ExecutorService executor = Executors
				.newFixedThreadPool(Crawler.MAX_THREADS);
		
		// recursively crawl links starting with mInitURL
		// until number of images reaches mSearchLimit
		while (this.mImageSet.size() < this.mSearchLimit) {
			if (queueIndex >= mURLQueue.size()) {
				// no more url to fetch, wait
				continue;
			}

			// get the head element from the queue
			String url = mURLQueue.get(queueIndex);
			
			// send url to callable
			Runnable worker = new FetcherRunnable(url, this, keyword);
			Future<?> future = executor.submit(worker);
			
			queueIndex++;
			//System.out.println(queueIndex++);
		}
		
		executor.shutdownNow();

		List<Image> resultImageList = new ArrayList<Image>(this.mImageSet);
		
		return resultImageList;
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
	
	public synchronized void addToImageSet(List<Image> imageList) {
		this.mImageSet.addAll(imageList);
		System.out.println("Image List Size: " + mImageSet.size());
	}
	
	public synchronized void addToURLSet(List<String> urlList) {
		for (String url : urlList) {
			if (this.mURLSet.add(url)) {
				this.mURLQueue.add(url);
			}
		}
		System.out.println("URL Queue Size: " + mURLQueue.size());
	}
		
}
