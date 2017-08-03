package com.jingjiu.sdk.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jingjiu.sdk.core.AdSDK;
import com.jingjiu.sdk.core.ad.bean.AdListBean;
import com.jingjiu.sdk.core.ad.common.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

/**
 * function
 */

public class CommonMethod {
    /**
     * 获取应用程序程序的缓存目录
     *
     * @param context    上下文
     * @param uniqueName
     * @return 目录 uniqueName
     */

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //noinspection ConstantConditions
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
//        JJLogger.logError("getDiskCacheDirPath",cachePath + File.separator + uniqueName);

        return new File(cachePath + File.separator + uniqueName);
    }


    /**
     * 获取应用程序的版本号
     *
     * @return 版本号
     */
    public static int getAppVersion() {
        try {
            PackageInfo info = AdSDK.getContext().getPackageManager().getPackageInfo(AdSDK.getContext().getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 在 Activity 中调用 该方法
     *
     * @param activity   当前的activity
     * @param itemViewId view 的id
     * @param <V>        泛型 ：返回类型
     * @return View的实例
     */
    @SuppressWarnings("unchecked")
    public static <V extends View> V getView(Activity activity, int itemViewId) {
        return (V) activity.findViewById(itemViewId);
    }

    /**
     * @param convertView itemViewId的容器
     * @param itemViewId  子View
     * @param <V>         子View的实例
     * @return 子View的实例
     */
    @SuppressWarnings("unchecked")
    public static <V extends View> V getView(View convertView, int itemViewId) {
        return (V) convertView.findViewById(itemViewId);
    }


    /**
     * MD5算法
     *
     * @param key
     * @return
     */
    public static String getMD5(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytes2Bitmap(byte[] bytes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        return bitmap;
    }


    /**
     * Drawable → putBitmap
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /*
         * putBitmap → Drawable
		 */
    @SuppressWarnings("deprecation")
    public static Drawable bitmap2Drawable(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        BitmapDrawable bd = new BitmapDrawable(bm);
        bd.setTargetDensity(bm.getDensity());
        return new BitmapDrawable(bm);
    }

    public static String getAdAppId() {
        Integer ad_app_id = null;
        ad_app_id = getAdAppInfo().metaData.getInt("ADAPPID");
        return String.valueOf(ad_app_id);
    }

    public static ApplicationInfo getAdAppInfo() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = AdSDK.getContext().getPackageManager().getApplicationInfo(
                    AdSDK.getContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    /**
     * 检测本地有无广告列表
     * 1、检测列表清单
     *
     * @return false 无缓存；true 有缓存
     */
    public static AdListBean getAdInfoCache() {
        AdListBean oldAdList = AdSDK.getAdInfoCacheHelper().getSerializable(Configuration.AD_LIST_CACHE);

        if (oldAdList != null) {
            return oldAdList;
        } else {
            return null;
        }
    }

    //获得独一无二的Psuedo ID
    public static String getUniquePsuedoID() {
        String serial;
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
    /**
     * 检测网络是否连接
     * @return
     */
    /**
     * 判断当前是否有网络连接
     * toast 是否开启内置的信息提示
     * ture 开启弹窗提示用户
     * false 不提示用户
     */
    public static boolean isActiveConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) AdSDK.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        ;
        NetworkInfo mActiveNetworkInfo;//当前正在活动的网络
        mActiveNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mActiveNetworkInfo != null && mActiveNetworkInfo.isConnected();
    }

    public static void releaseImageViewResouce(ViewGroup viewGroup, ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            //将imageView从父容器移除，并将imag置为null
            if (imageView != null) {
                viewGroup.removeView(imageView);
            }
            System.gc(); //提醒系统及时回收
        }
    }

    public static void releaseImageViewResouce(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    /**
     * 移除广告列表缓存
     *
     * @param listBeanMap
     */
    public static void removeAll(Map<String, AdListBean.DataBean.ListBean> listBeanMap) {
        AdSDK.getAdInfoCacheHelper().remove(Configuration.AD_LIST_CACHE);//从广告信息缓存文件夹下广告列表信息
        for (final Map.Entry<String, AdListBean.DataBean.ListBean> entry : listBeanMap.entrySet()) {
            String idOld = entry.getKey();//旧列表中的广告id
            AdSDK.getAdBitmapCacheHelper().remove(idOld);//从广告图片缓存文件夹下移除广告图片
        }
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void deleteFile(File file) {

        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        return out.toByteArray();
    }
}
