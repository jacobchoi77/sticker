package com.masterpiece.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

public class ImageUtils {

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }


    public static Bitmap loadResizedSticker(Bitmap bitmap) {
        int scale = 1;

        while (checkSize(bitmap.getWidth() / scale > Constants.MAX_STICKER_WIDTH,
                bitmap.getHeight() / scale > Constants.MAX_STICKER_HEIGHT)) {
            scale++;
        }

        scale--;
        if (scale < 1) {
            scale = 1;
        }
        return Bitmap
                .createScaledBitmap(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale, false);

    }

    public static File saveBitmapToPicturesFolder(Bitmap bitmap) {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.DISPLAY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        OutputStream outStream = null;
        File file = new File(mediaStorageDir.getPath() + File.separator
                + Utils.dateToString(new Date(), "yyyy_MM_dd_HH_mm_ss") + ".jpg");
        if (file.exists()) {
            if (file.delete() == false)
                return null;
        }
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // bitmap.recycle();
        return file;
    }

    private static boolean checkSize(boolean widthBigger, boolean heightBigger) {
        return widthBigger || heightBigger;
    }

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2, Matrix matrix) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(bmp2, matrix, null);
        return bmOverlay;
    }

    public static File saveTempBitmap(Context context, Bitmap bitmap) {
        File mediaStorageDir = new File(context.getCacheDir(), Constants.DISPLAY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        OutputStream outStream = null;

        File file = getTempOutputMediaFile(context);
        if (file.exists()) {
            if (file.delete() == false)
                return null;
        }
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // bitmap.recycle();
        return file;

    }

    public static Bitmap loadBitmapFromFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.delete() == false)
                return null;
        }
        return BitmapFactory.decodeFile(file.getPath());
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromUrl(String filePath, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static File getTempOutputMediaFile(Context context) {
        File mediaStorageDir = new File(context.getCacheDir(), Constants.DISPLAY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + Constants.TEMP_ORIGINAL_FILE_NAME);

        return mediaFile;
    }

    public static Bitmap rotateBitmap(String src, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(src);

            if (orientation == 1) {
                return bitmap;
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2:
                    matrix.setScale(-1, 1);
                    break;
                case 3:
                    matrix.setRotate(180);
                    break;
                case 4:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case 5:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case 6:
                    matrix.setRotate(90);
                    break;
                case 7:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case 8:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                        matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;

        try {
            /**
             * if your are targeting only api level >= 5 ExifInterface exif =
             * new ExifInterface(src); orientation =
             * exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
             */
            if (Build.VERSION.SDK_INT >= 5) {
                Class<?> exifClass = Class.forName("android.media.ExifInterface");
                Constructor<?> exifConstructor = exifClass.getConstructor(new Class[]{String.class});
                Object exifInstance = exifConstructor.newInstance(new Object[]{src});
                Method getAttributeInt = exifClass.getMethod("getAttributeInt", new Class[]{String.class,
                        int.class});
                Field tagOrientationField = exifClass.getField("TAG_ORIENTATION");
                String tagOrientation = (String) tagOrientationField.get(null);
                orientation = (Integer) getAttributeInt.invoke(exifInstance, new Object[]{tagOrientation, 1});
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return orientation;
    }

}
