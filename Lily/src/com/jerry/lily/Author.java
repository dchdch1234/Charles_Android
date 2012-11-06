package com.jerry.lily;

import com.jerry.utils.DatabaseDealer;
import com.jerry.utils.DocParser;
import com.jerry.widget.MyProgressDialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Author extends Activity{
	private Button quit;
	private Button submit;
	
	private TextView acc;
	private TextView name;
	private ImageView image;
	private MyProgressDialog waitingDialog;
	
	private Button block;
	private Button info;
	private Button send;
	private EditText content;

	private boolean isMale;
	private boolean isOnline;
	private String nameString;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.author);
		initComponents();
	}

	@Override
	protected void onDestroy() {
		if(waitingDialog != null) {
			waitingDialog.dismiss();
		}
		super.onDestroy();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.arg1) {
			case 10:
				if(waitingDialog != null) {
					waitingDialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "获取信息失败，请稍后重试!", Toast.LENGTH_SHORT).show();
				finish();
			case 11:
				name.setText(nameString);
				if (nameString.length() <= 5) {
					name.setTextSize(30);
				}
				if(isMale && isOnline) {
					image.setImageDrawable(getResources().getDrawable(R.drawable.male));
				} else if(isMale && !isOnline) {
					image.setImageDrawable(getResources().getDrawable(R.drawable.male_offline));
				} else if(!isMale && isOnline) {
					image.setImageDrawable(getResources().getDrawable(R.drawable.female));
				} else {
					image.setImageDrawable(getResources().getDrawable(R.drawable.female_offline));
				}
				if(waitingDialog != null) {
					waitingDialog.dismiss();
				}
				break;
			case 12:
				Toast.makeText(getApplicationContext(), "信息发送成功", Toast.LENGTH_SHORT).show();
				acc.setVisibility(View.VISIBLE);
				name.setVisibility(View.VISIBLE);
				image.setVisibility(View.VISIBLE);
				send.setVisibility(View.VISIBLE);
				
				info.setVisibility(View.INVISIBLE);
				submit.setVisibility(View.INVISIBLE);
				content.setVisibility(View.INVISIBLE);
				break;
			case 13:
				Toast.makeText(getApplicationContext(), "信息发送失败，请重试！", Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	private void initComponents() {
		quit = (Button)findViewById(R.id.author_quit);
		submit = (Button)findViewById(R.id.author_submit);
		acc = (TextView)findViewById(R.id.author_acc);
		name = (TextView)findViewById(R.id.author_name);
		image = (ImageView)findViewById(R.id.author_pic);
		block = (Button)findViewById(R.id.author_block);
		info = (Button)findViewById(R.id.author_info);
		send = (Button)findViewById(R.id.author_send_mail);
		content = (EditText)findViewById(R.id.author_content);
		
		quit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		final String authorUrl = getIntent().getStringExtra("authorUrl");
		final String authorName = getIntent().getStringExtra("authorName");
		acc.setText(authorName);
		
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						Message msg = Message.obtain();
						boolean success = DocParser.sendMail(authorName, content.getText().toString(), getApplicationContext(), 3);
						if(success) {
							msg.arg1 = 12;
						} else {
							msg.arg1 = 13;
						}
						mHandler.sendMessage(msg);
					}
				});
				thread.start();
			}
		});
		
		block.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DatabaseDealer.add2Block(Author.this, authorName);
				Toast.makeText(getApplicationContext(), "已将" + authorName + "屏蔽，请刷新页面!", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				acc.setVisibility(View.INVISIBLE);
				name.setVisibility(View.INVISIBLE);
				image.setVisibility(View.INVISIBLE);
				send.setVisibility(View.INVISIBLE);
				
				info.setVisibility(View.VISIBLE);
				submit.setVisibility(View.VISIBLE);
				content.setVisibility(View.VISIBLE);
			}
		});
		
		info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				acc.setVisibility(View.VISIBLE);
				name.setVisibility(View.VISIBLE);
				image.setVisibility(View.VISIBLE);
				send.setVisibility(View.VISIBLE);
				
				info.setVisibility(View.INVISIBLE);
				submit.setVisibility(View.INVISIBLE);
				content.setVisibility(View.INVISIBLE);
				
			}
		});

		if(waitingDialog == null) {
			waitingDialog = MyProgressDialog.createDialog(this);
		}
		waitingDialog.show();

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = Message.obtain();
				Bundle bundle = DocParser.getAuthorInfo(authorUrl,authorName, 3);
				if(bundle == null) {
					msg.arg1 = 10;
					mHandler.sendMessage(msg);
					return;
				}
				isMale = bundle.getBoolean("isMale");
				isOnline = bundle.getBoolean("isOnline");
				nameString = bundle.getString("name");
				msg.arg1 = 11;
				mHandler.sendMessage(msg);
			}
		});
		thread.start();
	}
}
