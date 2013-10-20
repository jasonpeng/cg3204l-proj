package model;

public class Link {

	/**
	 * @param args
	 */
	private String Href;
	private String Text;
	
	public Link(String hrefsrc, String textsrc)
	{
		Href = hrefsrc;
		Text = textsrc;
	}
	public void setHref(String hrefsrc)
	{
		Href = hrefsrc;
	}
	
	public void setText(String Textsrc)
	{
		Text = Textsrc;
	}
	
	public String getHref()
	{
		return Href;
	}
	public String getText()
	{
		return Text;
	}
}
