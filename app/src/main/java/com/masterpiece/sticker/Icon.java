package com.masterpiece.sticker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class Icon {
	private PointF mPosition = new PointF();
	private float mRotation = 0.0f;
	private float mScale = 1.0f;
	private Bitmap mBitmap = null;
	private static final int maxImageWidth = 640;
	private static final int maxImageHeight = 500;
	private Paint paint = new Paint();
	private Matrix mMatrix;

	public Icon(Bitmap bitmap, float x, float y) {
		mBitmap = bitmap;
		mPosition.x = x;
		mPosition.y = y;
		imageSizeCheck();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
	}

	private void imageSizeCheck() {
		if (mBitmap.getWidth() > maxImageWidth) {
			int newWidth = maxImageWidth;
			int newHeight = (mBitmap.getHeight() * newWidth) / mBitmap.getWidth();
			float scaleWidth = ((float) newWidth) / mBitmap.getWidth();
			float scaleHeight = ((float) newHeight) / mBitmap.getHeight();
			mScale = Math.max(scaleWidth, scaleHeight);
		}
		if (mBitmap.getHeight() > maxImageHeight) {
			int newHeight = maxImageHeight;
			int newWidth = (mBitmap.getWidth() * newHeight) / mBitmap.getHeight();
			float scaleWidth = ((float) newWidth) / mBitmap.getWidth();
			float scaleHeight = ((float) newHeight) / mBitmap.getHeight();
			mScale = Math.max(scaleWidth, scaleHeight);
		}
	}

	public void moveTo(float x, float y) {
		mPosition.x = x;
		mPosition.y = y;
	}

	public void setScale(float scale) {
		mScale = scale;
	}

	public void setRotation(float rotation) {
		mRotation = rotation;
	}

	public Matrix getMatrix() {
		return mMatrix;
	}

	public void draw(Canvas canvas) {
		canvas.save();
		canvas.translate(mPosition.x, mPosition.y);
		canvas.rotate(mRotation, getWidth() * mScale / 2, getHeight() * mScale / 2);
		canvas.scale(mScale, mScale);
		canvas.drawBitmap(mBitmap, 0, 0, paint);
		canvas.restore();
	}

	public int getWidth() {
		if (mBitmap != null)
			return mBitmap.getWidth();
		else
			return 0;
	}

	public int getHeight() {
		if (mBitmap != null)
			return mBitmap.getHeight();
		else
			return 0;
	}

	public RectF getRect() {
		return new RectF(mPosition.x, mPosition.y, mPosition.x + getWidth() * mScale, mPosition.y
				+ getHeight() * mScale);
	}
	public float getRotation() {
		return mRotation;
	}

	public void clear() {
		if (mBitmap != null)
			mBitmap.recycle();
	}

}
