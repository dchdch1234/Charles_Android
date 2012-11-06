package com.jerry.lily;

import java.util.ArrayList;
import java.util.List;

import com.jerry.model.LoginInfo;
import com.jerry.utils.ActivityManager;
import com.jerry.utils.DatabaseDealer;
import com.jerry.utils.DocParser;
import com.jerry.utils.LoginHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity{
	private Button exit;
	private Button login;
	private EditText editUsername;
	private EditText editPassword;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		removePreActivity();
		initComponents();
	}
	
	private void removePreActivity () {
		if (ActivityManager.activityList.size() >0) {
			ActivityManager.removeAllActivity();
			ActivityManager.activityList.add(this);
		}
	}

	private void initComponents() {
		exit = (Button) findViewById(R.id.login_exit);
		login = (Button) findViewById(R.id.login_submit);
		editUsername = (EditText) findViewById(R.id.login_username);
		editPassword = (EditText) findViewById(R.id.login_password);

		editUsername.setText(getIntent().getStringExtra("username"));
		editPassword.setText(getIntent().getStringExtra("password"));

		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginInfo loginInfo = new LoginInfo();
				String username=null;
				String password=null;
				
				if (editUsername.getText().length() == 0 || editPassword.getText().length() == 0) {
					//Login as guest.
					username="Guest";
					password="Guest";
					loginInfo.setUsername(username);
					loginInfo.setPassword(password);
					
					
				} else {

					username = editUsername.getText().toString();
					password = editPassword.getText().toString();

					Bundle bundle = new Bundle();
					bundle.putString("username", username);
					bundle.putString("password", password);
					loginInfo = LoginHelper.getInstance(bundle);

				}
				
				if(loginInfo == null) {
					Toast.makeText(getApplicationContext(), "无法登录，请确保网络通畅，并正确输入用户名和密码！", Toast.LENGTH_SHORT).show();
					return;
				} else {
					DatabaseDealer.deleteAcc(Login.this);
					DatabaseDealer.insert(Login.this, username, password);
					List<ContentValues> favList = new ArrayList<ContentValues>();
					if (username.equals("Guest")){
						//Define guest's favList
						ContentValues values = new ContentValues();						
						values.put("english", "Apple");
						values.put("chinese", "苹果电脑");
						values.put("islocal", 0);
						favList.add(values);
						
						ContentValues values1 = new ContentValues();	
						values1.put("english", "JobAndWork");
						values1.put("chinese", "创业与求职");
						values1.put("islocal", 0);
						favList.add(values1);
						
						ContentValues values2 = new ContentValues();
						values2.put("english", "JobExpress");
						values2.put("chinese", "就业特快");
						values2.put("islocal", 0);
						favList.add(values2);
						
						ContentValues values3 = new ContentValues();						
						values3.put("english", "Joke");
						values3.put("chinese", "笑话版");
						values3.put("islocal", 0);
						favList.add(values3);
						
						ContentValues values4 = new ContentValues();
						values4.put("english", "Mobile");
						values4.put("chinese", "手机天地");
						values4.put("islocal", 0);
						favList.add(values4);
						
						ContentValues values5 = new ContentValues();
						values5.put("english", "Pictures");
						values5.put("chinese", "贴图版");
						values5.put("islocal", 0);
						favList.add(values5);

						ContentValues values6 = new ContentValues();
						values6.put("english", "Girls");
						values6.put("chinese", "女生天地");
						values6.put("islocal", 0);
						favList.add(values6);
						
					} else {					
						favList = DocParser.synchronousFav(loginInfo, 3);
					}
					
					if(favList == null) {
						Toast.makeText(getApplicationContext(), "获取收藏版块失败，请检查网络！", Toast.LENGTH_SHORT).show();
						return;
					} else {
						DatabaseDealer.clearOnlineFav(Login.this);
						DatabaseDealer.addFav(Login.this, favList);
					}
					
					Intent intent = new Intent(Login.this, LilyActivity.class);
					intent.putParcelableArrayListExtra("topList", getIntent().getParcelableArrayListExtra("topList"));
					intent.putParcelableArrayListExtra("hotList",(ArrayList<? extends Parcelable>) getIntent().getParcelableArrayListExtra("hotList"));
					intent.putStringArrayListExtra("favList", (ArrayList<String>) DatabaseDealer.getFavList(Login.this));
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					
				}
			}

		});
	}
}
