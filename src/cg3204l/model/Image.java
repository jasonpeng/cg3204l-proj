package cg3204l.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Image {

	private String mSrc;
	private String mAlt;
	private String mCaption;
	private String mSiteUrl;
	private String mFilename;

	public Image(String src, String alt, String caption, String siteurl) {
		mSrc = src;
		mAlt = alt;
		mCaption = caption;
		mSiteUrl = siteurl;
		mFilename = filenameFromSrc(mSrc);
	}

	public void setSrc(String srcUrl) {
		mSrc = srcUrl;
	}

	public void setAlt(String altsrc) {
		mAlt = altsrc;
	}

	public void setCaption(String captionsrc) {
		mCaption = captionsrc;
	}

	public void setSiteUrl(String sitesrc) {
		mSiteUrl = sitesrc;
	}
	
	public String getSiteUrl() {
		return mSiteUrl;
	}
	
	public String getSrc() {
		return mSrc;
	}

	public String getAlt() {
		return mAlt;
	}

	public String getCaption() {
		return mCaption;
	}
	
	public String getFilename() {
		return mFilename;
	}
	
	private String filenameFromSrc(String src) {
		String filename = "";
		Pattern filenamePattern = Pattern.compile("(.+)\\.(jpg|jpeg|gif|png|tiff)");
		Matcher matcher = filenamePattern.matcher(src);
		if (matcher.find()) {
			filename = matcher.group(1);
		}
		
		return filename;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Image))
			return false;
		Image other = (Image) obj;
		if (mSrc == null) {
			if (other.mSrc != null)
				return false;
		} else if (!mSrc.equals(other.mSrc))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mSrc == null) ? 0 : mSrc.hashCode());
		return result;
	}
}
