package com.vinson.drawingtest.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;

import com.vinson.drawingtest.events.FPSEvent;

import de.greenrobot.event.EventBus;

public class DrawingTextview extends TextureView {

	private boolean mRestrictSpeed = false;
	private DrawingThread mDrawingThread;
	private Bitmap mBackgroundBitmap;
	private Canvas mBackgroundCanvas;
	private Path mPath;
	private Paint mPaint;

	public DrawingTextview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public DrawingTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public DrawingTextview(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		initCallback();
		initPath();
	}

	private void initCallback() {
		setSurfaceTextureListener(new SurfaceTextureListener() {

			@Override
			public void onSurfaceTextureUpdated(SurfaceTexture surface) {

			}

			@Override
			public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

			}

			@Override
			public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
				stopDrawingThread();
				return true;
			}

			@Override
			public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
				mBackgroundBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
				mBackgroundCanvas = new Canvas(mBackgroundBitmap);
				mBackgroundCanvas.drawColor(Color.WHITE);
				startDrawing();
			}
		});
	}

	private void initPath() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(4f);
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(Color.RED);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mPath = new Path();
			mPath.moveTo(x, y);
			break;

		case MotionEvent.ACTION_MOVE:
			mPath.lineTo(x, y);
			break;

		case MotionEvent.ACTION_POINTER_UP:
			mPath.lineTo(x, y);
			break;

		default:
			break;
		}
//		mBackgroundCanvas.drawColor(Color.WHITE);
		mBackgroundCanvas.drawPath(mPath, mPaint);
		return true;
	}

	public void clear() {
		mBackgroundCanvas.drawColor(Color.WHITE);
	}

	private void startDrawing() {
		if (null == mDrawingThread || !mDrawingThread.isAlive()) {
			mDrawingThread = new DrawingThread();
			mDrawingThread.start();
		}
	}

	private void stopDrawingThread() {
		if (null != mDrawingThread) {
			mDrawingThread.stopThread();
		}
	}

	private class DrawingThread extends Thread {

		private boolean mIsRunning = false;

		@Override
		public void run() {
			super.run();
			mIsRunning = true;

			int fps = 0;
			FPSEvent event = new FPSEvent();
			long start = System.currentTimeMillis();
			while (mIsRunning) {

				long beforeLock = System.currentTimeMillis();
				Canvas canvas = null;
				canvas = lockCanvas();
				if (null != canvas) {
					fps++;
//					canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
					if (null != mPath) {
						canvas.drawPath(mPath, mPaint);
					}
					unlockCanvasAndPost(canvas);
				}

				if (mRestrictSpeed) {
					long sleepFor = System.currentTimeMillis() - beforeLock;
					sleepFor = 16 - sleepFor;
					if (sleepFor > 0) {
						try {
							Thread.sleep(sleepFor);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				long end = System.currentTimeMillis();
				if (end - start > 1000) {
					start = end;
					event.mFpsCount = fps;
					fps = 0;
					EventBus.getDefault().post(event);
				}
			}
		}

		public void stopThread() {
			mIsRunning = false;
		}
	}
}
