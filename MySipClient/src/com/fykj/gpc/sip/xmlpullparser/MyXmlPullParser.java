package com.fykj.gpc.sip.xmlpullparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.fykj.gpc.sip.pojo.News;

public class MyXmlPullParser {

	public static List<News> getNewsList(String result) {
		List<News> newsList=new ArrayList<News>();
		News item=null;
		try {
			XmlPullParserFactory xmlPF = XmlPullParserFactory.newInstance();
			xmlPF.setNamespaceAware(true);
			XmlPullParser xpp = xmlPF.newPullParser();
			xpp.setInput(new InputStreamReader(new ByteArrayInputStream(
					result.getBytes())));
			int envenType=-1;
			while((envenType=xpp.next())!=XmlPullParser.END_DOCUMENT){
				if(envenType==XmlPullParser.START_TAG){
					if(xpp.getName().equals("tr")){
						item=new News();
					}
					if(xpp.getName().equals("image")){
						item.setImgUrl(xpp.getAttributeValue(0));
					}
					if(xpp.getName().equals("td")&&!xpp.nextText().trim().equals("")){
						item.setProfile(xpp.nextText().trim());
					}
					if(xpp.getName().equals("a")&&!xpp.nextText().trim().equals("")){
						item.setLink(xpp.getAttributeValue(0));
						item.setTitle(xpp.nextText().trim());
						newsList.add(item);
						System.out.println(item.toString());
					}
					
				}
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
