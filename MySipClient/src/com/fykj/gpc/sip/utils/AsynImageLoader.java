package com.fykj.gpc.sip.utils;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class AsynImageLoader {
	private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();

	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback callback) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Drawable) msg.obj);
			}
		};
		new Thread() {
			public void run() {
				if (imageCache.containsKey(imageUrl)) { // 检查缓冲imageCache是否存在对应的KEY
					SoftReference<Drawable> softReference = imageCache
							.get(imageUrl); // 存在就获取对应的值
					if (softReference.get() != null) {
						Message message = handler.obtainMessage(0,
								softReference.get());
						handler.sendMessage(message);

					}
				} else {
					Drawable drawable = loadImageFromUrl(imageUrl);
					imageCache.put(imageUrl, new SoftReference<Drawable>(
							drawable));
					handler.sendMessage(handler.obtainMessage(0, drawable));
				}
			};
		}.start();
		return null;
	}

	protected Drawable loadImageFromUrl(String imgUrl) {
		URL myImgUrl = null;
		Bitmap bitmap = null;
		if (imgUrl != null && imgUrl.trim() != "") {
			try {
				myImgUrl = new URL(imgUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myImgUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();

				int length = (int) conn.getContentLength();

				if (length != -1) {
					byte[] imgData = new byte[length];
					byte[] temp = new byte[1024];
					int readLen = 0;
					int destPos = 0;
					while ((readLen = is.read(temp)) > 0) {
						System.arraycopy(temp, 0, imgData, destPos, readLen);
						destPos += readLen;
					}

					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					Bitmap bm = BitmapFactory.decodeByteArray(imgData, 0,
							destPos, options);
					options.inJustDecodeBounds = false;
					int be = options.outHeight / 200;
					if (be <= 0) {
						be = 1;
					}
					options.inSampleSize = be;
					bitmap = BitmapFactory.decodeByteArray(imgData, 0, destPos,
							options);
				}

				is.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
				return null;
			}
			// Bitmap2Drawable
			Drawable d = new BitmapDrawable(bitmap);
			return d;
		}
		return null;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable);
	}
}
