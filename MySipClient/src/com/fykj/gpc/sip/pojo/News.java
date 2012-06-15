package com.fykj.gpc.sip.pojo;

import android.graphics.Bitmap;

public class News {
	private String title;
	private String link;
	private String profile;
	private String imgUrl;

	public News() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	@Override
	public String toString() {
		return "News [title=" + title + ", link=" + link + ", profile="
				+ profile + ", imgUrl=" + imgUrl + "]";
	}
	

}
