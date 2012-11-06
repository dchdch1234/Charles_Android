package com.jerry.lily;

import java.util.List;

import com.jerry.model.LoginInfo;
import com.jerry.utils.DatabaseDealer;
import com.jerry.utils.DocParser;
import com.jerry.utils.LoginHelper;
import com.jerry.utils.ShutDown;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Board extends ListActivity{
	private List<String> favList;
	private Button allBoard;
	private Button syn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);
		favList = getIntent().getStringArrayListExtra("favList");
		initComponents();
	}

	@Override
	protected void onResume() {
		super.onResume();
		favList = DatabaseDealer.getFavList(Board.this);
		setListAdapter(new ArrayAdapter<String>(Board.this,R.layout.list_board_name,favList));
	}

	private void initComponents() {
		if (favList == null) {
			return;
		}
		allBoard = (Button)findViewById(R.id.all_board);
		syn = (Button) findViewById(R.id.board_syn);
		allBoard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Board.this, AllBoard.class);
				startActivity(intent);
			}
		});
		syn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				thread.start();
				syn.setVisibility(View.GONE);
			}
		});
		if (favList != null && favList.size() > 0) {
			setListAdapter(new ArrayAdapter<String>(this,R.layout.list_board_name,favList));
		}
	}

	private Handler mHandler = new Handler(){ 
		@Override
		public void handleMessage(Message msg) {
			setListAdapter(new ArrayAdapter<String>(Board.this,R.layout.list_board_name,favList));
		}
	};

	private Thread thread = new Thread(new Runnable() {
		@Override
		public void run() {
			DatabaseDealer.clearOnlineFav(Board.this);
			LoginInfo loginInfo = LoginHelper.getInstance(Board.this);
			DatabaseDealer.addFav(Board.this, DocParser.synchronousFav(loginInfo, 3));
			favList = DatabaseDealer.getFavList(Board.this);
			mHandler.sendEmptyMessage(0);
		}
	});

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, BoardList.class);
		intent.putExtra("boardName", favList.get(position));
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onBackPressed() {
		ShutDown.shutDownActivity(this);
	}
}
