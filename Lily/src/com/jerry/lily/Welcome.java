package com.jerry.lily;


import java.util.ArrayList;
import java.util.List;

import com.jerry.model.Article;
import com.jerry.utils.ActivityManager;
import com.jerry.utils.DatabaseDealer;
import com.jerry.utils.DocParser;
import com.jerry.utils.FileDealer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.widget.Toast;

public class Welcome extends Activity{
	private Thread getUserInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		initComponents();
		ActivityManager.activityList.add(this);
		getUserInfo.start();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			showToast("Õ¯¬Á¡¨Ω” ß∞‹£¨«Î…‘∫Û‘Ÿ ‘£°");
			finish();
		};
	};

	private void showToast(String content) {
		Toast.makeText(getApplicationContext(), content, Toast.LENGTH_LONG).show();
	}

	private void initComponents() {
		getUserInfo = new Thread(new Runnable() {
			@Override
			public void run() {
				FileDealer.createDir();
				Bundle userInfo = DatabaseDealer.query(Welcome.this);

				List<Article> topList = DocParser.getArticleTitleList("http://bbs.nju.edu.cn/bbstop10", 3, DatabaseDealer.getBlockList(Welcome.this));
				if(topList == null) {
					mHandler.sendEmptyMessage(0);
					return;
				}
				List<Article> hotList = DocParser.getHotArticleTitleList("http://bbs.nju.edu.cn/bbstopall", 3);
				if(hotList == null) {
					mHandler.sendEmptyMessage(0);
					return;
				}
				boolean newMail = false;
				if(DatabaseDealer.getSettings(Welcome.this).isLogin()) {
					List<com.jerry.model.Mail> mailList = DocParser.getMailList(3, DatabaseDealer.getBlockList(Welcome.this), Welcome.this);
					if(mailList != null) {
						for(com.jerry.model.Mail mail : mailList) {
							if(mail.isRead()) {
								newMail = true;
								break;
							}
						}
					}
				}
				
				if (userInfo.size() == 0) {
					Intent intent = new Intent(Welcome.this, Login.class);
					intent.putParcelableArrayListExtra("topList", (ArrayList<? extends Parcelable>) topList);
					intent.putParcelableArrayListExtra("hotList", (ArrayList<? extends Parcelable>) hotList);
					startActivity(intent);
				} else {
					Intent intent = new Intent(Welcome.this, LilyActivity.class);
					intent.putParcelableArrayListExtra("topList", (ArrayList<? extends Parcelable>) topList);
					intent.putParcelableArrayListExtra("hotList", (ArrayList<? extends Parcelable>) hotList);
					intent.putStringArrayListExtra("favList", (ArrayList<String>) DatabaseDealer.getFavList(Welcome.this));
					intent.putExtra("newMail", newMail);
					startActivity(intent);
				}
			}
		});
	}
}
