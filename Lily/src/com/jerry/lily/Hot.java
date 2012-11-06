package com.jerry.lily;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.jerry.model.Article;
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

public class Hot extends ListActivity{
	private List<Article> hotList;
	private List<Map<String, Object>> dataMap;
	private MyProgressDialog waitingDialog;
	private Button refresh;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot);
		hotList = getIntent().getParcelableArrayListExtra("hotList");
		dataMap = getData();
		initComponents();
		initList();
	}

	private Thread thread;

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

	private void initComponents() {
		refresh = (Button)findViewById(R.id.hot_refresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(waitingDialog == null) {
					waitingDialog = MyProgressDialog.createDialog(Hot.this);
				}
				waitingDialog.show();
				if(thread != null) {
					thread.run();
					return;
				}
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						Message msg = Message.obtain();
						hotList = DocParser.getHotArticleTitleList("http://bbs.nju.edu.cn/bbstopall", 3);
						if(hotList == null) {
							msg.arg1 = 10;
							mHandler.sendMessage(msg);
							return;
						}
						dataMap = getData();
						msg.arg1 = 11;
						mHandler.sendMessage(msg);
					}
				});
				thread.start();
			}
		});
	}

	private void initList() {
		SimpleAdapter adapter = new SimpleAdapter(this, dataMap, R.layout.list_hot, new String[] {"title","board"}, new int[] {R.id.lh_title, R.id.lh_board});
		setListAdapter(adapter);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(Article article : hotList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", article.getTitle());
			map.put("board", "∞ÊøÈ:"+ article.getBoard());
			list.add(map);
		}
		return list;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, ArticleActivity.class);
		intent.putExtra("board", hotList.get(position).getBoard());
		intent.putExtra("contentUrl", hotList.get(position).getContentUrl());
		intent.putExtra("title", hotList.get(position).getTitle());
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		ShutDown.shutDownActivity(this);
	}
}
