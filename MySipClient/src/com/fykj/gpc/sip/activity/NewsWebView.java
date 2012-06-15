package com.fykj.gpc.sip.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class NewsWebView extends Activity {

	private String newsString;
	private WebView webView;
	private int backNum = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_news);
		newsString = getNewsString();
		webView = (WebView) findViewById(R.id.webView);
		// 启用javascript功能
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadData(newsString, "text/html", "utf-8");
		//webView.loadDataWithBaseURL(LoadActivity.WEBURL, newsString,
		//		"text/html", "utf-8", "");
		// 设置链接跳转客户端，取消默认操作，跳转到自己webview显示
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				// 记录可以后退的次数
				backNum++;
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				view.loadUrl("aboout:blank");
				backNum++;
				Toast.makeText(getApplicationContext(), "连接出错，请稍候再试！", 1500)
						.show();
			}
		});
	}

	// 后退操作该为webview的后退
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView.canGoBack()) {
				webView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private String getNewsString() {
		String path = Environment.getExternalStorageDirectory()
				+ "/sip_temp.txt";
		File temp = new File(path);
		StringBuilder sb = new StringBuilder();
		try {
			FileInputStream in = new FileInputStream(temp);
			byte[] bytes = new byte[1024];
			int i = -1;
			while ((i = in.read(bytes)) != -1) {
				sb.append(new String(bytes, 0, i));
			}
			in.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 去掉要闻以外的新闻内容
		int start = sb
				.indexOf("<tr><td colspan=\"2\" style=\"border-top:dotted 1px gray;\"><FONT style=\"FONT-SIZE: 10pt\">[要闻]</font></td></tr>");
		int end = sb
				.indexOf("<tr><td colspan=\"2\" style=\"border-top:dotted 0px gray;\"><FONT style=\"FONT-SIZE: 10pt\">[动态]</font></td></tr>");
		// sb.delete(start, end);
		return sb.toString();
	}

}
