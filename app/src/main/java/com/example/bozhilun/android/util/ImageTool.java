package com.example.bozhilun.android.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageTool {

    /**
     * bitmap转byte
     *
     * @param bm
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        try {
            if (bitmap != null) {
                byte[] bitmapBytes = compressImage(bitmap);
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据路径读取图片
     *
     * @param path
     * @return
     */
    public static Bitmap SDPhoto(String path) {
        File mFile = new File(path);
        // 若该文件存在
        if (mFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            return bitmap;
        }
        return null;
    }

    private static int maxRevisionImageNum;

    /**
     * 压缩图片
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static Bitmap revisionImageSize(String path) throws IOException,
            OutOfMemoryError {
        maxRevisionImageNum = Common.REVISION_IMAGE_MAX_NUM;
        Bitmap bitmap = null;
        try {
            BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int i = 0;
            while (true) {
                if ((options.outWidth >> i <= 1000)
                        && (options.outHeight >> i <= 1000)) {
                    in = new BufferedInputStream(new FileInputStream(new File(
                            path)));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
        } catch (OutOfMemoryError memory) {// 内存溢出，多在三星手机出现，怕崩，设置最多处理多少次
            bitmap = revisionImageSize(path, 2);
        }
        return bitmap;
    }

    /**
     * 压缩图片
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static Bitmap revisionImageSize(String path, int inSampleSize)
            throws IOException, OutOfMemoryError {
        maxRevisionImageNum--;
        Bitmap bitmap = null;
        try {
            BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int i = inSampleSize;
            while (true) {
                if ((options.outWidth >> i <= 1000)
                        && (options.outHeight >> i <= 1000)) {
                    in = new BufferedInputStream(new FileInputStream(new File(
                            path)));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
        } catch (OutOfMemoryError memory) {
            if (maxRevisionImageNum > 0)
                bitmap = revisionImageSize(path, inSampleSize++);
            else
                throw new OutOfMemoryError("图片过大");
        }
        return bitmap;
    }

    public static byte[] compressImage(Bitmap image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.PNG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > Common.IMAGE_MAX_SIZE) { // 循环判断如果压缩后图片是否大于上传图片的上限,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(CompressFormat.PNG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }

    //将图片转换成Base64
    public static String GetImageStr(String imgFilePath) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }

    /**
     * draw2bmp
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * 保存图片到本地
     *
     * @param bitmap
     * @param imageName
     * @param path
     */
    public static String saveBitmap(Bitmap bitmap, String imageName, String path) {
        String filePath = "";
        File file;
        File pic;
        file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        pic = new File(file, imageName);
        try {
            if (!pic.exists()) {
                pic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(pic);
            if (pic.getName().endsWith(".jpg")) {
                bitmap.compress(CompressFormat.JPEG, 100, fos);
            } else {
                bitmap.compress(CompressFormat.PNG, 100, fos);
            }
            fos.flush();
            fos.close();
            filePath = path + imageName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    /**
     * 图片灰色处理
     *
     * @param drawable
     * @return
     */
    public static void dealGrayImage(Drawable drawable) {
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
        drawable.setColorFilter(cf);
    }

    /**
     * 根据uri获取图片
     *
     * @param uri
     * @return
     */
    public static Bitmap decodeUriAsBitmap(Uri uri, Context context) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 获取背景图
     *
     * @param context
     * @param resId
     * @param isSaveMemory       是否节省内存（不高清）
     * @param isHaveTransparency 是否有透明度
     * @return 2015-5-19上午10:58:24
     */
    public static BitmapDrawable getBitmapDrawable(Context context, int resId,
                                                   boolean isSaveMemory, boolean isHaveTransparency) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        if (isSaveMemory) {
            if (!isHaveTransparency)
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
            else
                opt.inPreferredConfig = Bitmap.Config.ARGB_4444;
        } else
            opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static void Rotate(Context context, int oldId, ImageView imageView) {
        Matrix matrix = new Matrix();
        Bitmap bitmap = ((BitmapDrawable) ContextCompat.getDrawable(context, oldId)).getBitmap();
        // 设置旋转角度
        matrix.setRotate(180);
        // 重新绘制Bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(bitmap);
    }

    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(null, newbmp);
    }

}
