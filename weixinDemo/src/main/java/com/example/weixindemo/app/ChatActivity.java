package com.example.weixindemo.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.weixindemo.R;
import com.example.weixindemo.utils.FaceConversionUtil;

public class ChatActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new Thread(new Runnable() {
			@Override
			public void run() {
				FaceConversionUtil.getInstace().getFileText(getApplication());
			}
		}).start();
	}
	public void btnClick(View v){
		Intent intent=new Intent(ChatActivity.this,MainActivity.class);
		ChatActivity.this.startActivity(intent);
	}
}
