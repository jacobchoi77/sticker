package com.masterpiece.sticker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Border {
	private static final int BORDER_WIDTH = 3;

	private float mRotation = 0.0f;
	private RectF mRect;
	private Paint paint = new Paint();
	private Icon mIcon;

	public Border(Icon icon) {
		mIcon = icon;
		paint.setAntiAlias(true);
		paint.setColor(Color.LTGRAY);
		paint.setStrokeWidth(BORDER_WIDTH);
		paint.setStyle(Paint.Style.STROKE);
	}

	public void refresh() {
		mRect = mIcon.getRect();
		mRotation = mIcon.getRotation();
	}

	public void draw(Canvas canvas) {
		canvas.save();
		canvas.rotate(mRotation, mRect.centerX(), mRect.centerY());
		canvas.drawRect(mRect, paint);
		canvas.restore();
	}

	public RectF getRect() {
		return mRect;
	}

	public float getLeft() {
		return mRect.left;
	}

	public float getTop() {
		return mRect.top;
	}

	public float getRight() {
		return mRect.right;
	}

	public float getBottom() {
		return mRect.bottom;
	}

	public float getRotation() {
		return mRotation;
	}

	public float getCenterX() {
		return mRect.centerX();
	}

	public float getCenterY() {
		return mRect.centerY();
	}

	public boolean isInBorder(float x, float y) {
		double radian = Math.toRadians(mRotation);
		float rectangleCenterX = mRect.left + mRect.width() / 2;
		float rectangleCenterY = mRect.top + mRect.height() / 2;
		float dx = x - rectangleCenterX;
		float dy = y - rectangleCenterY;
		double distance = Math.sqrt(dx * dx + dy * dy);
		double currA = Math.atan2(dy, dx);
		double newA = currA - radian;
		double x2 = Math.cos(newA) * distance;
		double y2 = Math.sin(newA) * distance;
		if (x2 > -0.5 * mRect.width() && x2 < 0.5 * mRect.width() && y2 > -0.5 * mRect.height()
				&& y2 < 0.5 * mRect.height()) {
			return true;
		}
		return false;
	}

}
