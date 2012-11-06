package com.jerry.lily;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jerry.utils.DatabaseDealer;
import com.jerry.utils.DocParser;
import com.jerry.utils.ShutDown;
import com.jerry.widget.MyProgressDialog;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Mail extends ListActivity{

	private List<com.jerry.model.Mail> mailList;
	private MyProgressDialog waitingDialog;
	private List<Map<String, Object>> contentList;

	private Button add;
	private Button refresh;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mail);
		initComponents();
		initList();
	}

	@Override
	protected void onDestroy() {
		if(waitingDialog != null) {
			waitingDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(Mail.this, MailActivity.class);
		intent.putExtra("contentUrl", mailList.get(position).getContentUrl());
		startActivity(intent);
	}

	private Handler mHandler = new Handler(){ 
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 10:
				if (waitingDialog != null) {
					waitingDialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "µ«¬º ß∞‹£¨«ÎºÏ≤ÈÕ¯¬Á", Toast.LENGTH_SHORT).show();
				break;
			case 11:
				if (waitingDialog != null) {
					waitingDialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "Õ¯¬Á“Ï≥££¨«Î…‘∫Û÷ÿ ‘", Toast.LENGTH_SHORT).show();
				break;
			case 12:
				MySimpleAdapter adapter = new MySimpleAdapter(Mail.this, contentList, R.layout.list_mail, new String[] {"title","author","time"}, new int[] {R.id.lm_title, R.id.lm_author, R.id.lm_time});
				setListAdapter(adapter);
				if (waitingDialog != null) {
					waitingDialog.dismiss();
				}
				break;
			}
		}
	};


	private void initComponents() {
		refresh = (Button)findViewById(R.id.mail_refresh);
		add = (Button)findViewById(R.id.mail_add);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initList();
			}
		});
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Mail.this, NewMail.class);
				startActivity(intent);
			}
		});
	}

	private void initList() {
		if(waitingDialog == null) {
			waitingDialog = MyProgressDialog.createDialog(this);
		}
		waitingDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message  msg = Message.obtain();
				mailList = DocParser.getMailList(3, DatabaseDealer.getBlockList(Mail.this), Mail.this);
				Collections.reverse(mailList);
				if(mailList == null) {
					msg.arg1 = 11;
					mHandler.sendMessage(msg);
					return;
				}
				contentList = getData();
				msg.arg1 = 12;
				mHandler.sendMessage(msg);
			}
		});
		thread.start();
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i =  0; i < mailList.size(); i++) {
			com.jerry.model.Mail mail = mailList.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", mail.getTitle());
			map.put("author", "∑¢–≈»À:" + mail.getPoster());
			map.put("time", mail.getTime());
			list.add(map);
		}
		return list;
	}

	@Override
	public void onBackPressed() {
		ShutDown.shutDownActivity(this);
	}

	class MySimpleAdapter extends SimpleAdapter {

		public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
						String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			final TextView title = (TextView)view.findViewById(R.id.lm_title);
			if(mailList.get(position).isRead()) {
				title.setTextColor(Color.RED);
			} else {
				title.setTextColor(Color.BLACK);
			}
			return view;
		}

	}
}
