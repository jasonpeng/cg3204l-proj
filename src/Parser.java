
import java.io.IOException;
import java.util.List;

import model.Image;
import model.Link;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {

	/**
	 * @param args
	 */
	private List<Image> Imagelist;
	private List<Link> Linklist;
	private Document doc;
	private String src;
	private String alt;
	private String caption;
	
	private String iLink;
	private String iText;
	public void Parse(Document docsrc, String pageUrl)
	{
		//get all images
		doc = docsrc;
		Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");			
		for (Element image : images) {
 
				src = image.attr("src");
				if (src.startsWith("/") == true)
				{
					src = pageUrl + src;
				}
					
				alt = image.attr("alt");
				caption = image.attr("caption");
				Imagelist.add(new Image(src,alt,caption));
		}
		Elements links = doc.select("a[href]");
		for (Element link : links) {
 
			// get the value from href attribute
			iLink = link.attr("href");
			iText = link.text();
			Linklist.add(new Link(iLink,iText));
		}
 
	}
	public List<Image> getImages()
	{
		return Imagelist;
	}
	public List<Link> getLinks()
	{
		return Linklist;
	}

}
