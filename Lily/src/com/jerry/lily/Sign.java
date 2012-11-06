package com.jerry.lily;

import com.jerry.utils.DatabaseDealer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Sign extends Activity{
	private Button submit;
	private Button quit;
	private EditText edit;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign);
		initComponents();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_right2, R.anim.out_to_left2);
	}

	private void initComponents() {
		submit = (Button)findViewById(R.id.sign_submit);
		quit = (Button)findViewById(R.id.sign_exit);
		edit = (EditText)findViewById(R.id.sign_sign);
		edit.setText(DatabaseDealer.getSettings(Sign.this).getSign());

		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DatabaseDealer.updateSettings(Sign.this, "sign", edit.getText().toString());
				Toast.makeText(getApplicationContext(), "±£´æ³É¹¦", Toast.LENGTH_SHORT).show();
				finish();
				overridePendingTransition(R.anim.in_from_right2, R.anim.out_to_left2);
			}
		});

		quit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
}
