package cg3204l;
import java.util.concurrent.Callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class FetcherCallable implements Callable<Document> {
	
	protected String mURL;
	
	public FetcherCallable(String url) {
		this.mURL = url;
	}

	@Override
	public Document call() throws Exception {

		// fetch html content from URL
		Document doc = Jsoup.connect(mURL).get();
		return doc;
	}
}
