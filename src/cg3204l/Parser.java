package cg3204l;
import java.util.ArrayList;
import java.util.List;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cg3204l.model.Image;
import cg3204l.model.Link;

public class Parser {

	private List<Image> mImageList;
	private List<Link> mLinkList;
	private String mSiteUrl;

	public Parser(String url) {
		mSiteUrl = url;
		mImageList = new ArrayList<Image>();
		mLinkList = new ArrayList<Link>();
	}

	public void parse(Document doc) {
		String src;
		String alt;
		String caption;
		long now = System.currentTimeMillis();
		
		// get all images
		Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif|tiff)]");
		for (Element image : images) {
			src = image.attr("abs:src");
			alt = image.attr("alt");
			caption = image.attr("caption");
			mImageList.add(new Image(src, alt, caption, mSiteUrl, now));
		}

		// get all links
		String href;
		String text;

		Elements links = doc.select("a[href]");
		for (Element link : links) {
			href = link.attr("abs:href");
			text = link.text();

			mLinkList.add(new Link(href, text));
		}

	}

	public List<Image> getImages() {
		return mImageList;
	}

	public List<Link> getLinks() {
		return mLinkList;
	}

}
