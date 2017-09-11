package cn.diaovision.omnicontrol.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by TaoYimin on 2017/9/6.
 */

public class BitmapUtils {
    public static Bitmap decodeImage(String filePath,int FACTOR) {
        //用于配置解码参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        //是否只解码边框（true:此时不会将整个图片全部加载到内存中，只会将边框的像素解码到内存）
        options.inJustDecodeBounds = true;
        //第一次采样，采样图片的高和宽
        //BitmapFactory.decodeResource(context.getResources(), R.mipmap.pic, options);
        BitmapFactory.decodeFile(filePath,options);
        int width = options.outWidth;//图片实际的宽
        int height = options.outHeight;//图片实际的高
        int scaleWidth = width / FACTOR;//图片宽的压缩比
        int scaleHeight = height / FACTOR;//图片高的压缩比
        //false：解码整个图片
        options.inJustDecodeBounds = false;
        //设置第二采样使用的压缩比（谁大用谁），+1弥补损失的精度
        options.inSampleSize = (scaleWidth > scaleHeight ? scaleWidth : scaleHeight)+1;
        //设置图片的质量，也就是每个像素所包含的像素信息
        /**
         * Bitmap.Config.ARGB_8888: 占用32位，4个字节 质量最好，有透明效果
         * Bitmap.Config.ARGB_4444: 占用16位，2个字节 质量稍次（比RGB_565效果要差），有透明效果
         * Bitmap.Config.RGB_565:   占用16位，2个字节 如果只是用来浏览的图片，建议用RGB_565
         */
        options.inPreferredConfig= Bitmap.Config.RGB_565;
        //第二次采样，解码整个图片
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        //bitmap.getByteCount()只用于api19以后，bitmap.getRowBytes()*bitmap.getHeight()通用
        Log.i("info", "位图占用内存的大小：" + bitmap.getByteCount());
        return bitmap;
    }
}
