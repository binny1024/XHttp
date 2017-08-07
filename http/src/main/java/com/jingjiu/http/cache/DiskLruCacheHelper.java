package com.jingjiu.http.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.jingjiu.http.core.logger.JJLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import static com.jingjiu.http.common.CommonMethod.bitmap2Bytes;
import static com.jingjiu.http.common.CommonMethod.bitmap2Drawable;
import static com.jingjiu.http.common.CommonMethod.bytes2Bitmap;
import static com.jingjiu.http.common.CommonMethod.drawable2Bitmap;
import static com.jingjiu.http.common.CommonMethod.getAppVersion;
import static com.jingjiu.http.common.CommonMethod.getDiskCacheDir;
import static com.jingjiu.http.common.CommonMethod.getMD5;

/**
 * function  通过相同键（图片的索引）来保存图片和图片的详情
 */
@SuppressWarnings("unchecked")
public class DiskLruCacheHelper {
    private static final String DIR_NAME = "commonCache";
    private static final int MAX_COUNT = 5 * 1024 * 1024;
    private static final int DEFAULT_APP_VERSION = 1;

    /**
     * The default valueCount when open DiskLruCache.
     */
    private static final int DEFAULT_VALUE_COUNT = 1;

    private final String TAG = this.getClass().getSimpleName();

    private DiskLruCache mDiskLruCache;

    public DiskLruCacheHelper(Context context) throws IOException {
        mDiskLruCache = generateCache(context, DIR_NAME, MAX_COUNT);
    }

    public DiskLruCacheHelper(Context context, String dirName) throws IOException {
        mDiskLruCache = generateCache(context, dirName, MAX_COUNT);
    }

    public DiskLruCacheHelper(Context context, String dirName, int maxCount) throws IOException {
        mDiskLruCache = generateCache(context, dirName, maxCount);
    }

    //custom cache dir
    public DiskLruCacheHelper(File dir) throws IOException {
        mDiskLruCache = generateCache(null, dir, MAX_COUNT);
    }

    public DiskLruCacheHelper(Context context, File dir) throws IOException {
        mDiskLruCache = generateCache(context, dir, MAX_COUNT);
    }

    public DiskLruCacheHelper(Context context, File dir, int maxCount) throws IOException {
        mDiskLruCache = generateCache(context, dir, maxCount);
    }

    private DiskLruCache generateCache(Context context, File dir, int maxCount) throws IOException {
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException(
                    dir + " is not a directory or does not exists. ");
        }

        int appVersion = context == null ? DEFAULT_APP_VERSION : getAppVersion(context);
        DiskLruCache diskLruCache = null;
        diskLruCache = DiskLruCache.open(
                dir,
                appVersion,
                DEFAULT_VALUE_COUNT,
                maxCount);
        return diskLruCache;
    }

    private DiskLruCache generateCache(Context context, String dirName, int maxCount) throws IOException {
        DiskLruCache diskLruCache;
        diskLruCache = DiskLruCache.open(
                getDiskCacheDir(context, dirName),
                getAppVersion(context),
                DEFAULT_VALUE_COUNT,
                maxCount);
        return diskLruCache;
    }

    public boolean fileCacheExist(String key) {
        if (isClosed()) {
            try {
                throw new IOException("this cache has been closed");
            } catch (IOException e) {
                return false;
            }
        }
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getMD5(key));
            if (snapshot == null) //not find entry , or entry.readable = false
            {
                JJLogger.logInfo("", "id = " + key + " 没有发现缓存");
                return false;
            }
            //write READ
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * String 数据 读写
     *
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        DiskLruCache.Editor edit = null;
        BufferedWriter bw = null;
        try {
            edit = editor(key);
            if (edit == null) return;
            OutputStream os = edit.newOutputStream(0);
            bw = new BufferedWriter(new OutputStreamWriter(os));
            bw.write(value);
            edit.commit();//write CLEAN
        } catch (IOException e) {
            e.printStackTrace();
            try {
                //s
                edit.abort();//write REMOVE
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param key
     * @param jsonObject json对象
     */
    public void putJSONObject(String key, JSONObject jsonObject) {
        putString(key, jsonObject.toString());
    }


    /**
     * JSONArray 数据 读写
     *
     * @param key
     * @param jsonArray
     */
    public void putJSONArray(String key, JSONArray jsonArray) {
        putString(key, jsonArray.toString());
    }


    /**
     * byte 数据 读写
     *
     * @param key   保存的key
     * @param value 保存的数据
     */
    public void putByteArray(String key, byte[] value) {
        OutputStream out = null;
        DiskLruCache.Editor editor = null;
        try {
            editor = editor(key);
            if (editor == null) {
                return;
            }
            out = editor.newOutputStream(0);
            out.write(value);
            out.flush();
            editor.commit();//write CLEAN
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (editor != null) {
                    editor.abort();//write REMOVE
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 序列化 数据 读写
     *
     * @param key
     * @param value
     */
    public void putSerializableBean(String key, Serializable value) {
        DiskLruCache.Editor editor = editor(key);
        ObjectOutputStream oos = null;
        if (editor == null) return;
        try {
            OutputStream os = editor.newOutputStream(0);
            oos = new ObjectOutputStream(os);
            oos.writeObject(value);
            oos.flush();
            editor.commit();
            JJLogger.logInfo("save", "Bean 数据保存完成！");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (oos != null)
                    oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 缓存接受到的输入流
     *
     * @param key         缓存的键
     * @param inputStream 要缓存的输入流
     */
    public void putInputStream(String key, InputStream inputStream) {
        OutputStream out = null;
        DiskLruCache.Editor editor = null;
        try {
            editor = editor(key);
            if (editor == null) {
                return;
            }
            out = editor.newOutputStream(0);
            out.write(InputStreamToByte(inputStream));
            out.flush();
            editor.commit();//write CLEAN
            JJLogger.logInfo("save", "tBitmap 数据保存完成");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (editor != null) {
                    editor.abort();//write REMOVE
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * bitmap 数据 读写
     *
     * @param key
     * @param bitmap
     */
    public void putBitmap(String key, Bitmap bitmap) {
        putByteArray(key, bitmap2Bytes(bitmap));
    }

    /**
     * drawable 数据
     *
     * @param key
     * @param value
     */
    public void putDrawable(String key, Drawable value) {
        putBitmap(key, drawable2Bitmap(value));
    }


    //一系列的get方法
    public InputStream getInputStream(String key) throws IOException {
        if (isClosed()) {
            throw new IOException("数据被移除或数据流未打开");
        }
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getMD5(key));
            if (snapshot == null) //not find entry , or entry.readable = false
            {

                return null;
            }
            //write READ
            return snapshot.getInputStream(0);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public byte[] getBytes(String key) {
        if (isClosed()) {
            return null;
        }
        byte[] res = null;
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        InputStream is = null;
        try {
            is = getInputStream(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[256];
            int len;
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            res = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取bitmap   该函数还可以获取以 输入流形式put进来的图片信息
     *
     * @param key :该key多对应的数据 是以byte[]的形式存放
     * @return
     */
    public Bitmap getBitmap(String key) {
        if (isClosed()) {
            JJLogger.logError("", "DiskLruCacheHelper:getBitmap :已关闭");
            return null;
        }
        byte[] bytes = getBytes(key);
        if (bytes == null) return null;
        return bytes2Bitmap(bytes);
    }

    public Drawable getDrawable(String key) {
        if (isClosed()) {
            return null;
        }
        byte[] bytes = getBytes(key);
        if (bytes == null) {
            return null;
        }
        return bitmap2Drawable(bytes2Bitmap(bytes));
    }

    public <T> T getSerializable(String key) {
        if (isClosed()) {
            return null;
        }
        T t = null;
        InputStream is = null;
        try {
            is = getInputStream(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectInputStream ois = null;
        if (is == null) return null;
        try {
            ois = new ObjectInputStream(is);
            t = (T) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            JJLogger.logError("", e.getMessage());
        } finally {
            try {
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public JSONArray getJSONArray(String key) {
        if (isClosed()) {
            return null;
        }
        String JSONString = getString(key);
        try {
            return new JSONArray(JSONString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getString(String key) {
        if (isClosed()) {
            return null;
        }
        InputStream inputStream = null;
        try {
            inputStream = getInputStream(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputStream == null) return null;
        String str = null;
        try {
            str = UtilCache.readFully(new InputStreamReader(inputStream, UtilCache.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            try {
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return str;
    }

    public JSONObject getJSONObject(String key) {
        if (isClosed()) {
            return null;
        }
        String val = getString(key);
        try {
            if (val != null)
                return new JSONObject(val);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // =======================================
    // ============= other methods =============
    // =======================================
    public boolean remove(String key) {
        if (isClosed()) {
            return false;
        }
        try {
            key = getMD5(key);
            return mDiskLruCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
  * 删除所有文件
  * */
    public void removeAll() {
        if (isClosed()) {
            return;
        }
        try {
            mDiskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        mDiskLruCache.close();
    }


    public void flush() throws IOException {
        mDiskLruCache.flush();
    }

    public boolean isClosed() {
        return mDiskLruCache.isClosed();
    }

    public long size() {
        return mDiskLruCache.size();
    }

    public void setMaxSize(long maxSize) throws IOException {
        if (isClosed()) {
            throw new IOException("数据被移除或数据流未打开");
        }
        mDiskLruCache.setMaxSize(maxSize);
    }

    public File getDirectory() throws IOException {
        if (isClosed()) {
            throw new IOException("数据被移除或数据流未打开");
        }
        return mDiskLruCache.getDirectory();
    }

    public long getMaxSize() throws IOException {
        if (isClosed()) {
            throw new IOException("数据被移除或数据流未打开");
        }
        return mDiskLruCache.getMaxSize();
    }


    // =======================================
    // ===遇到文件比较大的，可以直接通过流读写 =====
    // =======================================
    //basic editor
    public DiskLruCache.Editor editor(String key) {
        try {
            key = getMD5(key);
            //wirte DIRTY
            DiskLruCache.Editor edit = mDiskLruCache.edit(key);
            //edit maybe null :the entry is editing
            if (edit == null) {
                JJLogger.logError("", "the entry spcified key:" + key + " is editing by other . ");
            }
            return edit;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 输入流转字节流
     */
    public byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        while ((len = is.read(buffer)) != -1) {
            bytestream.write(buffer, 0, len);
        }
        byte data[] = bytestream.toByteArray();
        bytestream.close();
        return data;
    }
}



