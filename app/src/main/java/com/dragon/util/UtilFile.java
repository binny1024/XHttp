package com.dragon.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.dragon.InitSDK;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * author xander on  2017/10/19.
 * function
 */

public class UtilFile {
    public static String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = InitSDK.getContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(InitSDK.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /**
     * 根据byte数组生成文件
     *
     * @param bytes    生成文件用到的byte数组
     * @param fileName
     */
    public static void createFileWithByte(byte[] bytes, String fileName) {
        // 通过文件路径穿件文件夹,可以创建多级文件夹，不能再创建多级文件夹+文件

        String extensionName = getApplicationName()+File.separator +fileName.substring(fileName.lastIndexOf(".") + 1);

        String file_holder_name = Environment.getExternalStorageDirectory() + File.separator + "Download" + File.separator;
        //新建一个File，传入文件夹路径
        File file_holder = new File(file_holder_name);
        //判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file_holder.exists()) {
           /*
           * 通过file的mkdirs()方法创建目录中包含却不存在的文件夹
           * */
            file_holder.mkdirs();
        }

        /*
        * 创建File对象，其中包含文件所在的目录以及文件的命名
        * */
        File file = new File(file_holder.getPath(),
                fileName);
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            file.createNewFile();
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
            Log.i(TAG, "createFileWithByte: 下载成功" + file_holder_name);
        } catch (Exception e) {
            // 打印异常信息
            Log.i(TAG, "createFileWithByte: 下载失败：\n" + e.getMessage());
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}
