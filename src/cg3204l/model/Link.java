package cg3204l.model;

public class Link {

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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Link))
			return false;
		Link other = (Link) obj;
		if (mHref == null) {
			if (other.mHref != null)
				return false;
		} else if (!mHref.equals(other.mHref))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mHref == null) ? 0 : mHref.hashCode());
		return result;
	}
}
