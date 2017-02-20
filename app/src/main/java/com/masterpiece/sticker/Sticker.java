package com.masterpiece.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class Sticker extends View {

    private String id;
    private Bitmap mIconBitmap;
    private Bitmap mOriginalBitmap;
    private int mImageHeight, mImageWidth;
    private float mTranslatePosX;
    private float mTranslatePosY;
    private float rotate = 0;
    private float scale = 1;
    private Icon icon;
    private Border border;
    private Close close;
    private Handle handle;
    private boolean isMoving = false;
    private boolean isRotateScaling = false;
    private float mLastTouchX;
    private float mLastTouchY;
    private double mDegreeOffset;
    private double mDiagonalDistance;
    private boolean isInitialized = false;
    private boolean isSelected = true;
    private StickerCallback mCallback;

    public interface StickerCallback {
        void closeSticker(Sticker sticker);

        void selectSticker(Sticker sticker);
    }

    public Sticker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Sticker(Context context) {
        super(context);
        mCallback = (StickerCallback) context;
    }

    public String getIdString() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void clear() {
        if (mOriginalBitmap != null)
            mOriginalBitmap.recycle();
        if (icon != null)
            icon.clear();
        if (close != null)
            close.clear();
        if (handle != null)
            handle.clear();

    }

    public void unSelect() {
        isSelected = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mTranslatePosX == 0) {
            mTranslatePosX = w / 2;
            mTranslatePosY = h / 2;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init() {
        isInitialized = true;
        Bitmap mCloseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_sticker_close);
        Bitmap mHandleBitmap = BitmapFactory
                .decodeResource(getResources(), R.drawable.ic_edit_sticker_handle);
        float scaledImageCenterX = (mImageWidth * scale) / 2;
        float scaledImageCenterY = (mImageHeight * scale) / 2;
        icon = new Icon(mIconBitmap, mTranslatePosX - scaledImageCenterX, mTranslatePosY - scaledImageCenterY);
        border = new Border(icon);
        close = new Close(mCloseBitmap, border);
        handle = new Handle(mHandleBitmap, border);
        float dx = icon.getRect().right - icon.getRect().centerX();
        float dy = icon.getRect().bottom - icon.getRect().centerY();
        mDegreeOffset = Math.toDegrees(Math.atan2(dy, dx));
        mDiagonalDistance = Math.sqrt(dx * dx + dy * dy);
        invalidate();
    }

    public void setImageBitmap(Bitmap bitmap) {
        mOriginalBitmap = bitmap;
        mIconBitmap = ImageUtils.loadResizedSticker(bitmap);
        // mIconBitmap = mOriginalBitmap;
        mImageWidth = mIconBitmap.getWidth();
        mImageHeight = mIconBitmap.getHeight();
    }

    public Bitmap getOriginalBitmap() {
        return mOriginalBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInitialized == false)
            init();
        float scaledImageCenterX = (mImageWidth * scale) / 2;
        float scaledImageCenterY = (mImageHeight * scale) / 2;
        icon.moveTo(mTranslatePosX - scaledImageCenterX, mTranslatePosY - scaledImageCenterY);
        icon.setScale(scale);
        icon.setRotation(rotate);
        border.refresh();
        close.refresh();
        handle.refresh();
        icon.draw(canvas);
        if (isSelected) {
            border.draw(canvas);
            close.draw(canvas);
            handle.draw(canvas);
        }

    }

    public void scaleTo(float scale) {
        this.scale = scale;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                return onActionDown(event.getX(), event.getY());

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null)
                    getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp();
                return true;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX(), event.getY());
                if (getParent() != null)
                    getParent().requestDisallowInterceptTouchEvent(true);
                return true;

            default:
                return false;
        }
    }

    private boolean onActionDown(float x, float y) {
        isRotateScaling = handle.isInHandle(x, y);
        if (isRotateScaling == true) {
            isSelected = true;
            mCallback.selectSticker(this);
            return true;
        }
        boolean isClose = close.isInClose(x, y);
        if (isClose == true) {
            mCallback.closeSticker(this);
            return true;
        }
        isMoving = border.isInBorder(x, y);
        if (isMoving == true) {
            isSelected = true;
            mCallback.selectSticker(this);
            invalidate();
            return true;
        }
        isSelected = false;
        invalidate();
        return false;
    }

    private void onActionUp() {
        isMoving = false;
        isRotateScaling = false;

    }

    private void onActionMove(float x, float y) {
        if (isMoving && isSelected) {
            final float dx = x - mLastTouchX;
            final float dy = y - mLastTouchY;
            mLastTouchX = x;
            mLastTouchY = y;
            mTranslatePosX += dx;
            mTranslatePosY += dy;
            invalidate();
        } else if (isRotateScaling && isSelected) {
            final RectF rect = icon.getRect();
            float touchDx = x - rect.centerX();
            float touchDy = y - rect.centerY();
            final double degree = Math.toDegrees(Math.atan2(touchDy, touchDx));
            rotate = (float) (degree - mDegreeOffset);
            final double distance = Math.sqrt(touchDx * touchDx + touchDy * touchDy);
            scaleTo((float) (distance / mDiagonalDistance));
            invalidate();
        }
    }

    public Matrix getIconMatrix() {
        Matrix matrix = new Matrix();
        matrix.postTranslate(mTranslatePosX, mTranslatePosY);
        matrix.postRotate(rotate, icon.getWidth() / 2, icon.getHeight() / 2);
        matrix.postScale(scale, scale);
        return matrix;
    }

    public void unselect() {
        if (isSelected) {
            isSelected = false;
            invalidate();
        }
    }
}
