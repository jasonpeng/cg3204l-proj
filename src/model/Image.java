package model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Image implements Comparable<Image> {

	private String mSrc;
	private String mAlt;
	private String mCaption;
	private String mFilename;

	public Image(String src, String alt, String caption) {
		mSrc = src;
		mAlt = alt;
		mCaption = caption;
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
	public int compareTo(Image o) {
		if (this.mSrc.equals(o.mSrc)) {
			return 0;
		} else {
			return 1;
		}
	}
}
