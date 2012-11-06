package com.jerry.lily;


import java.util.List;

import com.jerry.utils.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity implements OnClickListener{
	private Button pre;
	private Button next;
	private Button refresh;
	private Button add2Fav;
	private Button postNewArticle;

	private List<Integer> remove;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		initComponents();
	}

	private void initComponents() {
		remove = getIntent().getIntegerArrayListExtra("remove");

		pre = (Button) findViewById(R.id.pre);
		next = (Button) findViewById(R.id.next);
		refresh = (Button) findViewById(R.id.refresh);
		add2Fav = (Button) findViewById(R.id.add2_fav);
		postNewArticle = (Button) findViewById(R.id.post_article);

		pre.setOnClickListener(this);
		next.setOnClickListener(this);
		refresh.setOnClickListener(this);
		add2Fav.setOnClickListener(this);
		postNewArticle.setOnClickListener(this);

		if(remove.contains(Constants.NEXT)) {
			next.setVisibility(View.GONE);
		}
		if(remove.contains(Constants.PRE)) {
			pre.setVisibility(View.GONE);
		}
		if(remove.contains(Constants.REFRESH)) {
			refresh.setVisibility(View.GONE);
		}
		if(remove.contains(Constants.ADD_2_FAV)) {
			add2Fav.setVisibility(View.GONE);
		}
		if(remove.contains(Constants.POST_ARTICLE)) {
			postNewArticle.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		finish();
		return false;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.pre:
			setResult(Constants.PRE);
			finish();
			break;
		case R.id.next:
			setResult(Constants.NEXT);
			finish();
			break;
		case R.id.refresh:
			setResult(Constants.REFRESH);
			finish();
			break;
		case R.id.add2_fav:
			setResult(Constants.ADD_2_FAV);
			finish();
			break;
		case R.id.post_article:
			setResult(Constants.POST_ARTICLE);
			finish();
			break;
		}

	}

}
