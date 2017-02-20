package com.masterpiece.sticker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

public class Handle {
	private static final int MARGIN = 5;
	private PointF mPosition = new PointF();
	private float mRotation = 0.0f;
	private Bitmap mBitmap = null;
	private Paint paint = new Paint();
	private Border mBorder;
	private Matrix mMatrix;

	public Handle(Bitmap bitmap, Border border) {
		mBitmap = bitmap;
		paint.setAntiAlias(true);
		mBorder = border;
	}

	public void moveTo(float x, float y) {
		mPosition.x = x;
		mPosition.y = y;
	}

	public void refresh() {
		mPosition.x = mBorder.getRight() - getWidth() / 2;
		mPosition.y = mBorder.getBottom() - getHeight() / 2;
		mRotation = mBorder.getRotation();
	}

	public void draw(Canvas canvas) {

		canvas.save();
		canvas.rotate(mRotation, mBorder.getCenterX(), mBorder.getCenterY());
		mMatrix = canvas.getMatrix();
		canvas.translate(mPosition.x, mPosition.y);
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

	public float getRotation() {
		return mRotation;
	}

	public boolean isInHandle(float x, float y) {
		Matrix matrix = new Matrix();
		mMatrix.invert(matrix);
		float[] fa = {x, y};
		matrix.mapPoints(fa);
		return fa[0] > mPosition.x - MARGIN && fa[0] < mPosition.x + MARGIN + getWidth()
				&& fa[1] > mPosition.y - MARGIN && fa[1] < mPosition.y + getHeight() + MARGIN;
	}

	public void clear() {
		if (mBitmap != null)
			mBitmap.recycle();
	}
}
