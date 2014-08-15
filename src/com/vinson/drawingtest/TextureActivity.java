package com.vinson.drawingtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.vinson.drawingtest.events.FPSEvent;
import com.vinson.drawingtest.views.DrawingTextview;

import de.greenrobot.event.EventBus;

public class TextureActivity extends Activity {

	private Button mButton;
	private TextView mFpsTextView;
	private DrawingTextview mDrawingTextview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.texture_layout);

		mDrawingTextview = (DrawingTextview) findViewById(R.id.drawing_textureView);

		mFpsTextView = (TextView) findViewById(R.id.texture_fps_textView);

		mButton = (Button) findViewById(R.id.texture_clear_button);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDrawingTextview.clear();
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	public void onEventMainThread(FPSEvent event) {
		mFpsTextView.setText(getString(R.string.fps_rate, event.mFpsCount));
	}
}
