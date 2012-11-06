package com.jerry.widget;

import com.jerry.lily.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;

public class MyProgressDialog extends Dialog {
	private Context context;
	private static MyProgressDialog myProgressDialog = null;
	public MyProgressDialog(Context context){
		super(context);
		this.context = context;
	}

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public static MyProgressDialog createDialog(Context context){
		myProgressDialog = new MyProgressDialog(context,R.style.CustomProgressDialog);
		myProgressDialog.setContentView(R.layout.custom_progress_dialog);
		myProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return myProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus){
		if (myProgressDialog == null){
			return;
		}

		ImageView imageView = (ImageView) myProgressDialog.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
		animationDrawable.start();
	}
	public MyProgressDialog setTitile(String strTitle){
		return myProgressDialog;
	}
}

