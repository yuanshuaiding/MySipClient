package com.fykj.gpc.sip.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoadActivity extends Activity {
	private ProgressBar progressBar;
	private ConnectivityManager connMgr;
	private LoadThread loadThread;
	public static final String WEBURL = "http://www.sipac.gov.cn/multiclient/sipaciphone/";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		loadThread = new LoadThread();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (loadThread.getStatus() != Status.RUNNING) {
			loadThread = new LoadThread();
			loadThread.execute(new String[] {});
		} else {
			Toast.makeText(getApplicationContext(), "正在检查，请稍候", 1500)
					.show();
		}
	}

	private class LoadThread extends AsyncTask<String, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			// 检查联网
			NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
			if (netinfo != null && netinfo.isAvailable()
					&& netinfo.isConnected()) {
				// 网络可用时检查网站是否可以连接
				try {
					URL url = new URL(WEBURL);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					// 5秒超时则说明网站无法访问
					conn.setConnectTimeout(5 * 1000);
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Content-type", "text/html;charset=utf-8");
					conn.connect();
					if (conn.getResponseCode() == 200) {
						// 下载首页源文件，以便解析
						InputStream in = conn.getInputStream();
						// 写入sdcard
						return WriteStream2SDCard(in);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return 0;
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			switch (result) {
			case -1:
				// TODO 网络连接未打开，提示是否设置
				Builder builder = new AlertDialog.Builder(LoadActivity.this);
				builder.setTitle("网络不可用，是否要打开网络链接");
				builder.setPositiveButton("打开", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_WIRELESS_SETTINGS);
						startActivity(intent);
					}
				});
				builder.setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				builder.show();
				break;
			case 0:
				// TODO 网站不可用，请联想管理人员
				Toast.makeText(getApplicationContext(), "网站暂时不可用，请联系网站管理人员！",
						1500).show();
				finish();
				break;
			case 1:
				// 取消进度条，并跳转到主页面
				progressBar.setVisibility(View.GONE);
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				// 结束当前加载的页面
				finish();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "请检查Sdcard是否可用！", 2000)
						.show();
				finish();
			}
		}

	};

	/* 检查网络联机是否正常 
	public boolean checkWebConnection(String strURL, String strEncoding) {
		// 最多延时n秒若无响应则表示无法联机 
		int intTimeout = 10;
		try {
			HttpURLConnection urlConnection = null;
			URL url = new URL(strURL);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0"
					+ " (compatible; MSIE 6.0; Windows 2000)");

			urlConnection.setRequestProperty("Content-type",
					"text/html; charset=" + strEncoding);
			urlConnection.setConnectTimeout(1000 * intTimeout);
			urlConnection.connect();
			if (urlConnection.getResponseCode() == 200) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}*/

	public Integer WriteStream2SDCard(InputStream in) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String path = Environment.getExternalStorageDirectory()
					+ "/sip_temp.txt";
			File temp = new File(path);
			if (!temp.exists()) {
				try {
					temp.createNewFile();
					OutputStream os=new FileOutputStream(temp);
					byte[] bytes=new byte[1024*4];
					int i=-1;
					while((i=in.read(bytes))!=-1){
						os.write(bytes, 0, i);
					}
					os.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return 1;
		}
		return 2;
	}
}