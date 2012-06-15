package com.fykj.gpc.sip.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.TextView;

public class NewsDetail extends Activity {

	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsdetail);
		Intent it = getIntent();
		String url = it.getExtras().getString("url");
		String title = it.getExtras().getString("title");
		View btn_back = findViewById(R.id.btn_back);
		TextView tv_title = (TextView) findViewById(R.id.txt_title);
		tv_title.setText(title);

		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.setInitialScale(200);//·Å´ó2±¶
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if (url != null && !url.equals("")) {

			String detail = getNewsDetailString(url);
			detail = ConstantUtil.HTTPSTART + detail + ConstantUtil.HTTPEND;
			webView.loadDataWithBaseURL(ConstantUtil.WEBURL + url, detail,
					"text/html", "UTF-8", null);
		}
	}

	private String getNewsDetailString(String url) {
		String result = "";
		try {
			URL newsurl = new URL(ConstantUtil.WEBURL + url);
			HttpURLConnection conn = (HttpURLConnection) newsurl
					.openConnection();
			// conn.setDoInput(true);
			// conn.setRequestMethod("get");
			conn.connect();
			int count = conn.getContentLength();
			InputStream in = conn.getInputStream();
			if (count > 0) {
				StringBuilder sb = new StringBuilder();
				byte[] temp = new byte[1024 * 4];
				int i = -1;
				while ((i = in.read(temp)) != -1) {
					sb.append(new String(temp, 0, i, "UTF-8"));
				}
				result = sb.toString();
				int start = result.indexOf("<div class=\"main\">");
				int end = result.indexOf("<div class=\"footer\">");
				result = result.substring(start, end);
				System.out.println(result);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
