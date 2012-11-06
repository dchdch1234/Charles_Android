package com.jerry.lily;

import com.jerry.model.Settings;
import com.jerry.utils.DatabaseDealer;
import com.jerry.utils.DocParser;
import com.jerry.utils.FileDealer;
import com.jerry.utils.ShutDown;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Set extends Activity{
	private Settings settings;
	private ToggleButton isLogin;
	private ToggleButton isSavePic;
	private ToggleButton isShowPic;
	private ToggleButton isReplyMail;
	
	private Button clearPicCache;

	private RelativeLayout acc;
	private RelativeLayout sign;
	private RelativeLayout block;
	private RelativeLayout about;
	private RelativeLayout update;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set);
		initComponents();
		updateComponents();
	}

	@Override
	protected void onResume() {
		updateComponents();
		super.onResume();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 10:
				Toast.makeText(getApplicationContext(), "检查到新版本!", Toast.LENGTH_SHORT).show();
				break;

			case 11:
				Toast.makeText(getApplicationContext(), "已经是最新版本!", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private void initComponents() {
		acc = (RelativeLayout)findViewById(R.id.set_acc);
		sign = (RelativeLayout)findViewById(R.id.set_sign);
		block = (RelativeLayout)findViewById(R.id.set_block);
		about = (RelativeLayout)findViewById(R.id.set_about);
		update = (RelativeLayout)findViewById(R.id.set_check_update);

		clearPicCache = (Button)findViewById(R.id.set_delete_pic);
		
		isLogin = (ToggleButton)findViewById(R.id.set_autoLogin);
		isSavePic = (ToggleButton)findViewById(R.id.set_savPic);
		isShowPic = (ToggleButton)findViewById(R.id.set_showPic);
		isReplyMail = (ToggleButton)findViewById(R.id.set_reply_mail); 

		clearPicCache.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FileDealer.clearPicCache();
				Toast.makeText(getApplicationContext(), "图片清理成功!", Toast.LENGTH_SHORT).show();
			}
		});
		
		update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						Message msg = Message.obtain();
						boolean isNewVersion = DocParser.isNewVersionAvailable(getApplicationContext());
						if(isNewVersion) {
							msg.arg1 = 10;
						} else {
							msg.arg1 = 11;
						}
						mHandler.sendMessage(msg);
						return;
					}
				});
				thread.start();
			}
		});

		about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Set.this, About.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right2, R.anim.out_to_left2);
			}
		});

		isLogin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP) {
					isLogin.setChecked(!isLogin.isChecked());
					DatabaseDealer.updateSettings(Set.this, "isLogin", isLogin.isChecked() ? "1" : "0");
				}
				return true;
			}
		});

		isReplyMail.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP) {
					isReplyMail.setChecked(!isReplyMail.isChecked());
					DatabaseDealer.updateSettings(Set.this, "isSendMail", isReplyMail.isChecked() ? "1" : "0");
				}
				return true;
			}
		});

		isSavePic.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP) {
					isSavePic.setChecked(!isSavePic.isChecked());
					DatabaseDealer.updateSettings(Set.this, "isSavePic", isSavePic.isChecked() ? "1" : "0");
				}

				return true;
			}
		});
		isShowPic.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP) {
					isShowPic.setChecked(!isShowPic.isChecked());
					DatabaseDealer.updateSettings(Set.this, "isShowPic", isShowPic.isChecked() ? "1" : "0");
				}
				return true;
			}
		});

		acc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Set.this, Login.class);
				intent.putExtra("username", DatabaseDealer.query(Set.this).getString("username"));
				intent.putExtra("pass", DatabaseDealer.query(Set.this).getString("password"));
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right2, R.anim.out_to_left2);
			}
		});

		sign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Set.this, Sign.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right2, R.anim.out_to_left2);
			}
		});

		block.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Set.this, Block.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right2, R.anim.out_to_left2);
			}
		});
	}

	private void updateComponents() {
		settings = DatabaseDealer.getSettings(Set.this);
		isLogin.setChecked(settings.isLogin());
		isSavePic.setChecked(settings.isSavePic());
		isShowPic.setChecked(settings.isShowPic());
		isReplyMail.setChecked(settings.isSendMail());
	}

	@Override
	public void onBackPressed() {
		ShutDown.shutDownActivity(this);
	}
}
