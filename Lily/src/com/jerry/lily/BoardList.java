package com.jerry.lily;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jerry.model.Article;
import com.jerry.utils.Constants;
import com.jerry.utils.DatabaseDealer;
import com.jerry.utils.DocParser;
import com.jerry.widget.MyProgressDialog;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BoardList extends ListActivity{
	private Button backButton;
	private Button moreMenu;
	private TextView boardName;
	private List<Article> articleList;
	private MyProgressDialog waitingDialog;
	private List<Map<String, Object>> contentList;

	private Intent menuIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singleboard);
		initComponents();
		initList("http://bbs.nju.edu.cn/bbstdoc?board=" + boardName.getText());
	}

	private Handler mHandler = new Handler(){ 
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 11:
				SimpleAdapter adapter = new SimpleAdapter(BoardList.this, contentList, R.layout.list_board_article, new String[] {"title","author","reply"}, new int[] {R.id.lb_title, R.id.lb_author, R.id.lb_reply});
				setListAdapter(adapter);
				if (waitingDialog != null) {
					waitingDialog.dismiss();
				}
				break;
			case 12:
				if (waitingDialog != null) {
					waitingDialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "网络异常，请稍后再试!", Toast.LENGTH_SHORT).show();
				finish();
				overridePendingTransition(R.anim.in_from_right2, R.anim.out_to_left2);
				break;
			case 13:
				if (waitingDialog != null) {
					waitingDialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "网络异常，请稍后再试!", Toast.LENGTH_SHORT).show();
				break;
			case 14:
				if (waitingDialog != null) {
					waitingDialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "图片上传失败，请稍后重试!", Toast.LENGTH_SHORT).show();
				break;
			case 15:
				if (waitingDialog != null) {
					waitingDialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "发帖成功!", Toast.LENGTH_SHORT).show();
				initList("http://bbs.nju.edu.cn/bbstdoc?board=" + boardName.getText());
				break;
			}
		}
	};

	private void initList(String url) {
		final String boardUrl = url;
		if(waitingDialog == null) {
			waitingDialog = MyProgressDialog.createDialog(this);
		}
		waitingDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message  msg = Message.obtain();
				articleList = DocParser.getBoardArticleTitleList(boardUrl,boardName.getText().toString(), 3, DatabaseDealer.getBlockList(BoardList.this));
				if(articleList == null) {
					msg.arg1 = 12;
					mHandler.sendMessage(msg);
					return;
				}
				contentList = getData();
				msg.arg1 = 11;
				mHandler.sendMessage(msg);
			}
		});
		thread.start();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_right2, R.anim.out_to_left2);
	}

	private void initComponents() {
		backButton = (Button) findViewById(R.id.sb_bbutton);
		moreMenu = (Button) findViewById(R.id.sb_menu);
		boardName = (TextView) findViewById(R.id.sb_board);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.in_from_right2, R.anim.out_to_left2);
			}
		});
		moreMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startMenuActivity();
			}
		});
		boardName.setText(getIntent().getStringExtra("boardName"));
	}

	private void startMenuActivity() {
		if(menuIntent == null) {
			menuIntent = new Intent(BoardList.this, MenuActivity.class);
		}
		List<Integer> removeElements = new ArrayList<Integer>();
		if (articleList == null || articleList.size() == 0) {
			removeElements.add(Constants.PRE);
		}
		String oldNextUrl = articleList.get(articleList.size() - 1).getBoardUrl();
		if (oldNextUrl ==null || oldNextUrl.length() == 0) {
			removeElements.add(Constants.NEXT);
		}
		menuIntent.putIntegerArrayListExtra("remove", (ArrayList<Integer>) removeElements);
		startActivityForResult(menuIntent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode) {
		case Constants.PRE:
			if (articleList == null || articleList.size() == 0) {
				return;
			}
			String preUrl = "http://bbs.nju.edu.cn/" + articleList.get(articleList.size() - 1).getBoard();
			initList(preUrl);
			break;
		case Constants.NEXT:
			if (articleList == null || articleList.size() == 0) {
				return;
			}
			String oldNextUrl = articleList.get(articleList.size() - 1).getBoardUrl();
			if (oldNextUrl == null || oldNextUrl.length() == 0) {
				return;
			}
			String nextUrl = "http://bbs.nju.edu.cn/" + oldNextUrl;
			initList(nextUrl);
			break;
		case Constants.ADD_2_FAV:
			boolean isFav = DatabaseDealer.isFav(BoardList.this, boardName.getText().toString());
			if (isFav) {
				Toast.makeText(BoardList.this, "该版面已在收藏夹中", Toast.LENGTH_SHORT).show();
			} else {
				DatabaseDealer.addBoard2LocalFav(BoardList.this, boardName.getText().toString());
				Toast.makeText(BoardList.this, "已将该版面加入本地收藏夹", Toast.LENGTH_SHORT).show();
			}
			break;
		case Constants.POST_ARTICLE:
			Intent intent = new Intent(BoardList.this, ReplyArticle.class);
			intent.putExtra("isTitleVisiable", true);
			startActivityForResult(intent, 1);
			break;
		case Constants.REFRESH:
			initList("http://bbs.nju.edu.cn/bbstdoc?board=" + boardName.getText());
			break;
		case Constants.SEND_REPLY:
			Toast.makeText(getApplicationContext(), "正在发帖，请稍后...", Toast.LENGTH_SHORT).show();
			final String replyContent = data.getStringExtra("replyContent");
			final List<String> picPath = data.getStringArrayListExtra("picPath");
			final String title = data.getStringExtra("title");
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					Message msg = Message.obtain();
					String picUrl = DocParser.upLoadPic2Server(picPath,boardName.getText().toString(), 3, BoardList.this);
					if(picUrl == null) {
						msg.arg1 = 14;
						mHandler.sendMessage(msg);
						return;
					}
					boolean success = sendReply(replyContent,title, picUrl);
					if(success) {
						msg.arg1 = 15;
						mHandler.sendMessage(msg);
						return;
					} else {
						msg.arg1 = 13;
						mHandler.sendMessage(msg);
						return;
					}
				}
			});
			thread.start();
			break;
		case Constants.CANCEL_REPLY:
			break;
		}
	}

	private boolean sendReply(String replyContent, String title,String picPath) {
		return DocParser.sendReply(boardName.getText().toString(), title, "0", "0", replyContent, null,picPath, BoardList.this, 3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		startMenuActivity();
		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, ArticleActivity.class);
		intent.putExtra("board", boardName.getText().toString());
		intent.putExtra("contentUrl", articleList.get(position).getContentUrl());
		intent.putExtra("title", articleList.get(position).getTitle());
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < articleList.size() - 1; i ++) {
			Article article = articleList.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", article.getTitle());
			map.put("author", "作者:" + article.getAuthorName());
			map.put("reply", article.getReply() + "/" + article.getView());
			list.add(map);
		}
		return list;
	}

}
