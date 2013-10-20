package model;

public class Image {

	/**
	 * @param args
	 */
	private String Src;
	private String Alt;
	private String Caption;
	
	public Image(String srcurl, String altsrc, String captionsrc)
	{
		Src = srcurl;
		Alt = altsrc;
		Caption = captionsrc;
	}
	public void setSrc(String srcUrl)
	{
		Src = srcUrl;
	}
	
	public void setAlt(String altsrc)
	{
		Alt = altsrc;
	}
	public void setCaption(String captionsrc)
	{
		Caption = captionsrc;
	}
	public String getSrc()
	{
		return Src;
	}
	public String getAlt()
	{
		return Alt;
	}
	public String getCaption()
	{
		return Caption;
	}
}
