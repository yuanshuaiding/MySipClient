package com.fykj.gpc.sip.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fykj.gpc.sip.pojo.News;
import com.fykj.gpc.sip.utils.AsynImageLoader;
import com.fykj.gpc.sip.utils.AsynImageLoader.ImageCallback;

public class DynamicActivity extends Activity {

	private ListView listView;
	private List<News> newsList;
	private String newsString;
	private LayoutInflater inflater;
	private AsynImageLoader asynImgLoader;
	private List<Drawable> imgList=new ArrayList<Drawable>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		inflater = getLayoutInflater();
		asynImgLoader = new AsynImageLoader();
		listView = (ListView) findViewById(R.id.listView);
		newsString = getNewsString();
		System.out.println(newsString);
		newsList = getNewsList(newsString);
		MyAdapter adapter = new MyAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent it=new Intent();
				it.putExtra("url", newsList.get(position).getLink());
				it.putExtra("title", newsList.get(position).getTitle());
				it.setClass(getApplicationContext(), NewsDetail.class);
				startActivity(it);
			}
		});
		
	}

	// 根据截取的字符串进行构造新闻列表
	private List<News> getNewsList(String result) {

		String[] newsArr = result.split("</tr><tr>");
		System.out.println("newsArr length:" + newsArr.length);
		List<News> newsList = new ArrayList<News>();
		News newsItem = null;
		int start = 0;
		int end = 0;
		if (newsArr != null && newsArr.length > 0) {
			for (String item : newsArr) {
				newsItem = new News();
				// 获取url
				start = item.indexOf("href=") + 7;
				end = item.indexOf("\"", start);
				newsItem.setLink(item.substring(start, end));
				// 获取图片url
				start = item.indexOf("src=") + 6;
				end = item.indexOf("\"", start);
				newsItem.setImgUrl(item.substring(start, end));
				// 获取标题
				start = item.lastIndexOf("htm\">") + 5;
				end = item.lastIndexOf("</a>");
				newsItem.setTitle(item.substring(start, end));
				// 获取简介
				start = item.lastIndexOf("<br/>") + 5;
				end = item.lastIndexOf("</td>");
				newsItem.setProfile(item.substring(start, end));
				System.out.println(newsItem);
				newsList.add(newsItem);
			}
		}
		return newsList;

		// return MyXmlPullParser.getNewsList(result);
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
				sb.append(new String(bytes, 0, i, "UTF-8"));
			}
			in.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String newsStr = sb.toString();
		int start = newsStr
				.indexOf("<tr><td colspan=\"2\" style=\"border-top:dotted 1px gray;\"><FONT style=\"FONT-SIZE: 10pt\">[���闻]</font></td></tr>");
		int end = newsStr
				.indexOf("<tr><td colspan=\"2\" style=\"border-top:dotted 0px gray;\"><FONT style=\"FONT-SIZE: 10pt\">[动态]</font></td></tr>");
		if (start >= 0 && end > start && end < newsStr.length()) {
			newsStr = newsStr.substring(start, end);
			newsStr = newsStr
					.replace(
							"<tr><td colspan=\"2\" style=\"border-top:dotted 1px gray;\"><FONT style=\"FONT-SIZE: 10pt\">[���闻]</font></td></tr>",
							"");
			newsStr = newsStr.replace("\n", "");
			newsStr = newsStr.replace("\t", "");
		}
		return newsStr;
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (newsList != null && newsList.size() > 0)
				return newsList.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			News news = newsList.get(position);
			View v = null;
			if (convertView == null || convertView.getTag() == null) {
				v = inflater.inflate(R.layout.news_item, null);
			} else {
				v = convertView;
			}
			TextView tv_title = (TextView) v.findViewById(R.id.txt_title);
			TextView tv_profile = (TextView) v.findViewById(R.id.txt_profile);
			final ImageView img = (ImageView) v.findViewById(R.id.img);
			if (news != null) {
				//设置新闻标题
				tv_title.setText(news.getTitle());
				//设置新闻简介
				tv_profile.setText(news.getProfile());
				//异步加载图片
				asynImgLoader.loadDrawable(
						ConstantUtil.WEBURL + news.getImgUrl(),
						new ImageCallback() {

							@Override
							public void imageLoaded(Drawable imageDrawable) {
								imgList.add(imageDrawable);
								img.setImageDrawable(imageDrawable);
							}
						});
			}
			return v;
		}

	}

}
