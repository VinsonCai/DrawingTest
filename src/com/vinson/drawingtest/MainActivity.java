package com.vinson.drawingtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button mSurfaceButton;
	private Button mTextureButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}

	private void initViews() {
		mSurfaceButton = (Button) findViewById(R.id.surface_button);
		mTextureButton = (Button) findViewById(R.id.texture_button);

		mSurfaceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SurfaceActivity.class);
				startActivity(intent);
			}
		});

		mTextureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, TextureActivity.class);
				startActivity(intent);
			}
		});
	}

}
