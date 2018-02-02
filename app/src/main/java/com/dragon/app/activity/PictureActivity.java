package com.dragon.app.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.binny.core.logger.JJLogger;
import com.dragon.R;
import com.dragon.abs.activity.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class PictureActivity extends BaseActivity {
private ImageView imageView;
    @Override
    protected void afterInit() {

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_qqmain;
    }

    @Override
    protected void initView() {
        imageView = (ImageView) findViewById(R.id.imgv);
    }

    @Override
    protected void initData() {

    }

    /**
     * 拍照获取图片
     */
    private String sdPath;//SD卡的路径
    private String picPath;//图片存储路径
    private static int REQUEST_ORIGINAL = 2;// 请求原图信号标识

    public void takePhoto(View view) {
        //获取SD卡的路径
        sdPath = Environment.getExternalStorageDirectory().getPath();
        picPath = sdPath + "/" + "temp.png";
        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.fromFile(new File(picPath));
        //为拍摄的图片指定一个存储的路径
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent2, REQUEST_ORIGINAL);
    }

    /**
     * 返回应用时回调方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ORIGINAL) {//对应第二种方法
                /**
                 * 这种方法是通过内存卡的路径进行读取图片，所以的到的图片是拍摄的原图
                 */
                FileInputStream fis = null;
                try {
                    Log.e("sdPath2", picPath);
                    //把图片转化为字节流
                    fis = new FileInputStream(picPath);
                    //把流转化图片
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    JJLogger.logInfo("error",e.getMessage());
                } finally {
                    try {
                        fis.close();//关闭流
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
