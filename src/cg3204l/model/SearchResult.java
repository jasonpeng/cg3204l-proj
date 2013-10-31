package cg3204l.model;

import java.util.ArrayList;
import java.util.List;


public class SearchResult {
	private double mTimeUsed;
	private List<ImageResult> mListImageResult;
	private class ImageResult{
		private String mImageUrl;
		private String mSiteUrl;
		public ImageResult(String image,String site){
			mImageUrl = image;
			mSiteUrl = site;
			if(mImageUrl == null){
				mImageUrl = "";
			}
			if(mSiteUrl == null){
				mSiteUrl = "";
			}
		}
		public String toString(){
			String s = "";
			s += "{";
			s += "imageUrl:" + "'" + mImageUrl + "'";
			s += ",";
			s += "siteUrl:" + "'" + mSiteUrl + "'";
			s += "}"; 
			return s;
		}
	}
	
	public SearchResult(){
		mListImageResult = new ArrayList<ImageResult>();
		mTimeUsed = 0;
	}
	
	public void addImageList(List<Image> images){
		for(int i = 0; i< images.size(); i++){
			ImageResult ir = new ImageResult(images.get(i).getSrc(),images.get(i).getSiteUrl());
			mListImageResult.add(ir);
		}
	}
	public void addImage(String imgUrl, String siteUrl){
		ImageResult ir = new ImageResult(imgUrl,siteUrl);
		mListImageResult.add(ir);
	}
	
	public void setTime(double d){
		mTimeUsed = d;
	}
	
	public String toString(){
		String s = "";
		s += "{";
		s += "images:";
		s += "[";
		for(int i = 0; i< mListImageResult.size(); i++){
			s += mListImageResult.get(i).toString();
			if(i != (mListImageResult.size() - 1)){
				s += ",";
			}
		}
		s += "]";
		s += ",";
		s += "timeUsed:" + mTimeUsed;
		s += "}";
		return s;
	}
}
