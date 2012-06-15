package com.fykj.gpc.sip.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends TabActivity {

	private TabHost tabHost;
	private String[] tabTitleArr = { "要闻", "动态", "公告", "English", "日语" };
	private Class[] tabActivityArr = { NewsActivity.class, NewsActivity.class,
			NewsWebView.class, NewsActivity.class, NewsActivity.class };
	private LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		inflater = getLayoutInflater();
		tabHost = getTabHost();
		initTabHost();
		tabHost.setCurrentTab(0);
	}

	private void initTabHost() {
		int count=tabTitleArr.length;
		for(int i=0;i<count;i++){
			TabSpec tp=tabHost.newTabSpec(tabTitleArr[i]);
			//设置tabspec里面的视图
			tp.setIndicator(getTabWidgetView(i));
			//设置要显示内容
			tp.setContent(getTabWidgetIntent(i));
			tabHost.addTab(tp);
		}
	}

	private Intent getTabWidgetIntent(int i) {
		Intent it = new Intent();
		it.setClass(getApplicationContext(), tabActivityArr[i]);
		it.putExtra("title", tabTitleArr[i]);
		return it;
	}

	private View getTabWidgetView(int i) {
		View v = inflater.inflate(R.layout.tabwidget_item, null);
		TextView tv=(TextView)v.findViewById(R.id.txt_title);
		tv.setText(tabTitleArr[i]);
		return v;
	}

}
