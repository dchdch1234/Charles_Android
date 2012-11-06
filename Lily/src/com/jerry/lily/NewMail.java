package com.jerry.lily;

import com.jerry.utils.DocParser;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewMail extends Activity{

	private Button reply;
	private Button quit;
	private Button photo;
	private Button pic;
	private EditText title;
	private EditText content;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reply);
		initComponents();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 10:
				Toast.makeText(getApplicationContext(), "�ż����ͳɹ�!",Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 11:
				Toast.makeText(getApplicationContext(), "�ż�����ʧ��!",Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private void initComponents() {
		reply = (Button)findViewById(R.id.reply_submit);
		quit = (Button)findViewById(R.id.reply_quit);
		content = (EditText)findViewById(R.id.reply_edit);
		title = (EditText) findViewById(R.id.reply_title);
		
		photo = (Button)findViewById(R.id.reply_photo);
		pic = (Button)findViewById(R.id.reply_pic);
		photo.setVisibility(View.GONE);
		pic.setVisibility(View.GONE);

		title.setHint("�������ռ���");

		quit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		reply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(title.getText() == null || title.getText().length() == 0 || content.getText() == null || content.getText().length() == 0) {
					Toast.makeText(getApplicationContext(), "�ռ��˺��ż����ݲ���Ϊ��!",Toast.LENGTH_SHORT).show();
					return;
				}

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						Message msg = Message.obtain();
						boolean isSuccess = DocParser.sendMail(title.getText().toString(), content.getText().toString(), NewMail.this, 3);
						if(isSuccess) {
							msg.arg1 = 10;
						} else {
							msg.arg1 = 11;
						}
						mHandler.sendMessage(msg);
					}
				});
				thread.start();

			}
		});
	}
}
