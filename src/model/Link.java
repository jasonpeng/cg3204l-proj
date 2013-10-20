package model;

public class Link implements Comparable<Link> {

	private String mHref;
	private String mText;

	public Link(String hrefsrc, String textsrc) {
		mHref = hrefsrc;
		mText = textsrc;
	}

	public void setHref(String hrefsrc) {
		mHref = hrefsrc;
	}

	public void setText(String Textsrc) {
		mText = Textsrc;
	}

	public String getHref() {
		return mHref;
	}

	public String getText() {
		return mText;
	}

	@Override
	public int compareTo(Link o) {
		if (this.mHref.equals(o.mHref)) {
			return 0;
		} else {
			return 1;
		}
	}
}
