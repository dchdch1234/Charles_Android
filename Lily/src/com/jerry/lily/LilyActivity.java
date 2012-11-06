package com.jerry.lily;


import java.util.ArrayList;

import com.jerry.utils.ActivityManager;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class LilyActivity extends TabActivity implements OnCheckedChangeListener{
	private TabHost tabHost;
	private RadioGroup radioGroup;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainui);
		removePreActivity();
		ActivityManager.activityList.add(LilyActivity.this);
		initComponents();
	}

	private void removePreActivity () {
		if (ActivityManager.activityList.size() >0) {
			ActivityManager.removeAllActivity();
		}
	}

	private void initComponents() {
		//boolean newMail = getIntent().getBooleanExtra("newMail", false);
		tabHost = getTabHost();
		radioGroup = (RadioGroup)findViewById(R.id.radio);
		radioGroup.setOnCheckedChangeListener(this);
		Intent intent1 = new Intent(this, Top.class);
		intent1.putParcelableArrayListExtra("topList",(ArrayList<? extends Parcelable>) getIntent().getParcelableArrayListExtra("topList"));
		Intent intent2 = new Intent(this, Board.class);
		intent2.putStringArrayListExtra("favList", getIntent().getStringArrayListExtra("favList"));
		Intent intent3 = new Intent(this, Hot.class);
		intent3.putParcelableArrayListExtra("hotList",(ArrayList<? extends Parcelable>) getIntent().getParcelableArrayListExtra("hotList"));
		Intent intent4 = new Intent(this, Mail.class);
		Intent intent5 = new Intent(this, Set.class);

		tabHost.addTab(tabHost.newTabSpec("top")
				.setIndicator("",getResources().getDrawable(android.R.drawable.ic_menu_call))
				.setContent(intent1));
		tabHost.addTab(tabHost.newTabSpec("board")
				.setIndicator("",getResources().getDrawable(android.R.drawable.ic_menu_add))
				.setContent(intent2));
		tabHost.addTab(tabHost.newTabSpec("hot")
				.setIndicator("",getResources().getDrawable(android.R.drawable.ic_media_play))
				.setContent(intent3));
		tabHost.addTab(tabHost.newTabSpec("search")
				.setIndicator("",getResources().getDrawable(android.R.drawable.ic_menu_camera))
				.setContent(intent4));
		tabHost.addTab(tabHost.newTabSpec("set")
				.setIndicator("",getResources().getDrawable(android.R.drawable.ic_menu_camera))
				.setContent(intent5));
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.top:
			this.tabHost.setCurrentTabByTag("top");
			break;
		case R.id.board:
			this.tabHost.setCurrentTabByTag("board");
			break;
		case R.id.hot:
			this.tabHost.setCurrentTabByTag("hot");
			break;
		case R.id.settings:
			this.tabHost.setCurrentTabByTag("set");
			break;
		default:
			this.tabHost.setCurrentTabByTag("top");
			break;
		}
	}
}  
