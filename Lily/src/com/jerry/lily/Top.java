package com.jerry.lily;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.jerry.model.Article;
import com.jerry.utils.DatabaseDealer;
import com.jerry.utils.DocParser;
import com.jerry.utils.ShutDown;
import com.jerry.widget.MyProgressDialog;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Top extends ListActivity{
	private List<Article> articleList;
	private Button refresh;
	private MyProgressDialog waitingDialog;
	private List<Map<String, Object>> dataMap;

	private Thread refreshThread;
	private Handler mHandler = new Handler(){ 
		@Override
		public void handleMessage(Message msg) {
			switch(msg.arg1) {
			case 10:
				waitingDialog.dismiss();
				Toast.makeText(getApplicationContext(), "Õ¯¬Á“Ï≥££¨«Î…‘∫Û÷ÿ ‘!", Toast.LENGTH_SHORT).show();
				break;
			case 11:
				waitingDialog.dismiss();
				initList();
				break;
			}
		}
	};
	
	protected void onDestroy() {
		if(waitingDialog != null) {
			waitingDialog.dismiss();
		}
		if(refreshThread != null) {
			refreshThread = null;
		}
		super.onDestroy();
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.top);
		articleList = getIntent().getParcelableArrayListExtra("topList");
		dataMap = getData();
		initComponents();
		initList();
	}

	private void initList() {
		SimpleAdapter adapter = new SimpleAdapter(this, dataMap, R.layout.list_top10, new String[] {"title","author","board"}, new int[] {R.id.title, R.id.author, R.id.board});
		setListAdapter(adapter);
	}

	private void initComponents() {
		refresh = (Button)findViewById(R.id.top_refresh);
		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(waitingDialog == null) {
					waitingDialog = MyProgressDialog.createDialog(Top.this);
				}
				waitingDialog.show();
				if(refreshThread != null) {
					refreshThread.run();
					return;
				}
				refreshThread = new Thread(new Runnable() {
					@Override
					public void run() {
						Message msg = Message.obtain();
						articleList = DocParser.getArticleTitleList("http://bbs.nju.edu.cn/bbstop10", 3, DatabaseDealer.getBlockList(Top.this));
						if(articleList == null) {
							msg.arg1 = 10;
							mHandler.sendMessage(msg);
							return;
						}
						dataMap = getData();
						msg.arg1 = 11;
						mHandler.sendMessage(msg);
					}
				});
				refreshThread.start();
			}
		});
	}



	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, ArticleActivity.class);
		intent.putExtra("board", articleList.get(position).getBoard());
		intent.putExtra("contentUrl", articleList.get(position).getContentUrl());
		intent.putExtra("title", articleList.get(position).getTitle());
		startActivity(intent);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(articleList == null) {
			articleList = DocParser.getArticleTitleList("http://bbs.nju.edu.cn/bbstop10", 3, DatabaseDealer.getBlockList(Top.this));
		}
		for(Article article : articleList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", article.getTitle());
			map.put("author", "◊˜’ﬂ:" + article.getAuthorName());
			map.put("board", "∞ÊøÈ:"+ article.getBoard());
			list.add(map);
		}
		return list;
	}

	@Override
	public void onBackPressed() {
		ShutDown.shutDownActivity(this);
	}

}
