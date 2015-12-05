package com.example.weixindemo.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weixindemo.R;
import com.example.weixindemo.utils.ImageLoader;

import java.io.File;

public class CameraActivity extends Activity{
	private ImageView camera;
	private	String imgUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera);
		init();
	}
	private void init() {
		camera=(ImageView) findViewById(R.id.camera);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		imgUrl = bundle.getString("camera");
		//获取相机返回的数据，并转换为图片格式
		//Bitmap bitmap = (Bitmap)msg.get("data");
		Toast.makeText(this, imgUrl, Toast.LENGTH_LONG).show();
		ImageLoader.getInstance().loadImage(imgUrl, camera);
	}

	public void btnClick(View v){
		switch (v.getId()) {
			case R.id.cancel_camera:
				File file = new File(imgUrl);
				if (file.exists()) {
					file.delete();
				}
				CameraActivity.this.finish();
				break;
			case R.id.confirm_camera:
				//告诉聊天界面需要发送数据到服务器，并且在聊天界面中显示
				//Toast.makeText(CameraActivity.this, "发送图片", 1).show();
				Intent intent=new Intent();
				intent.putExtra("imgUrl", imgUrl);
				CameraActivity.this.setResult(MainActivity.SHOW_CAMER_PICTURE_FROM,intent);
				CameraActivity.this.finish();
				break;
		}
	}

}
