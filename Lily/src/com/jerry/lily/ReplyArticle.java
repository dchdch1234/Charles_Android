package com.jerry.lily;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.jerry.utils.Constants;
import com.jerry.utils.FileDealer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReplyArticle extends Activity{

	private Button reply;
	private Button quit;
	private Button photo;
	private Button pic;
	private EditText title;
	private EditText content;

	private boolean isTitleVisiable;
	private List<String> picPath = new ArrayList<String>();
	private File tmpPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.reply);
		initComponents();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case Constants.GALLERY:
			if (data == null) {
				return;
			}
			Uri uri = data.getData();
			if (uri == null) {
				return;
			}
			String[] proj = { MediaStore.Images.Media.DATA };  
			Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);  
			int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
			actualimagecursor.moveToFirst();  
			String imgPath= actualimagecursor.getString(actual_image_column_index);  
			picPath.add(imgPath);
			break;
		case Constants.CAMERA:
			if(tmpPhoto == null || !tmpPhoto.exists()) {
				return;
			}
			picPath.add(tmpPhoto.getPath());
			break;
		}
	}

	private void initComponents() {
		reply = (Button)findViewById(R.id.reply_submit);
		quit = (Button)findViewById(R.id.reply_quit);
		content = (EditText)findViewById(R.id.reply_edit);
		title = (EditText) findViewById(R.id.reply_title);
		pic = (Button)findViewById(R.id.reply_pic);
		photo = (Button)findViewById(R.id.reply_photo);

		isTitleVisiable = getIntent().getBooleanExtra("isTitleVisiable", true);
		if(!isTitleVisiable) {
			title.setVisibility(View.GONE);
		}
		quit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Constants.CANCEL_REPLY);
				finish();
			}
		});
		reply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (content.getText() == null || content.getText().length() == 0) {
					return;
				}
				if (isTitleVisiable && (title.getText() == null || title.getText().length() == 0)) {
					return;
				}
				Intent intent = new Intent();
				intent.putExtra("replyContent", content.getText().toString());
				intent.putExtra("title", title.getText().toString());
				intent.putStringArrayListExtra("picPath", (ArrayList<String>) picPath);
				setResult(Constants.SEND_REPLY,intent);
				finish();
			}
		});
		pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {  
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
					intent.setType("image/*");
					intent.putExtra("return-data", true);  
					startActivityForResult(intent, Constants.GALLERY);  
				} catch (ActivityNotFoundException e) {  
					Toast.makeText(ReplyArticle.this, "无法打开相册",Toast.LENGTH_LONG).show();  
				}  
			}
		});
		
		photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String filePath = FileDealer.getPhotoDirPath() + "/" + UUID.randomUUID() + ".jpg";
					tmpPhoto = new File(filePath);
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpPhoto));
					startActivityForResult(intent, Constants.CAMERA);
				} catch (ActivityNotFoundException e) {  
					Toast.makeText(ReplyArticle.this, "无法打开相册",Toast.LENGTH_LONG).show();  
				}  
				
			}
		});
	}

}
