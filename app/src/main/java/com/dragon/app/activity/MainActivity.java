package com.dragon.app.activity;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.GridView;
import android.widget.Toast;

import com.bean.logger.JJLogger;
import com.bean.xhttp.XHttp;
import com.bean.xhttp.callback.OnXHttpCallback;
import com.bean.xhttp.response.Response;
import com.dragon.R;
import com.dragon.abs.activity.FullscreenActivity;
import com.dragon.api.WebApi;
import com.dragon.app.activity.http.XHttpActivity;
import com.dragon.app.activity.launcherpage.FragmentViewPagerActivity;
import com.dragon.app.activity.launcherpage.LauncherActivity;
import com.dragon.app.activity.launcherpage.SimpleViewPagerActivity;
import com.dragon.app.activity.tab.CoordinatorTabLayoutActivity;
import com.dragon.app.activity.tab.TabLayoutActivity;
import com.dragon.app.bean.BeanMainActivity;
import com.dragon.app.callback.ViewHolderItemClickedCallback;
import com.dragon.app.helper.ViewHolderHelperMain;
import com.dragon.widget.BaseTitleBar;
import com.smart.holder.CommonAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.dragon.util.UtilWidget.getView;
import static com.dragon.util.UtilWidget.showErrorInfo;


public class MainActivity extends FullscreenActivity implements ViewHolderItemClickedCallback {

    private final String TAG = "timeout";


    private GridView gridView;

    private String[] ITEMS_MAIN;//主界面的选项卡标签


    @Override
    protected void afterInit() {

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mTitleBar = getView(this, R.id.base_title_bar);
        gridView = getView(this, R.id.main_grid);

    }

    @Override
    protected void initData() {
        ITEMS_MAIN = getResources().getStringArray(R.array.mian_data);
        mTitleBar.setBaseTitleBar("主界面", R.mipmap.back, R.mipmap.share, new BaseTitleBar.OnBaseTitleBarButtonListener() {
            @Override
            public void onLeftButton() {
                finish();
            }

            @Override
            public void onRightButton() {
                showErrorInfo(MainActivity.this, "分享", "分享测试");
            }
        });
        List<BeanMainActivity> mItemBeanList = new ArrayList<>();
        BeanMainActivity bean;
        for (int i = 0; i < ITEMS_MAIN.length; i++) {
            bean = new BeanMainActivity();
            bean.setTextViewName(ITEMS_MAIN[i]);
            mItemBeanList.add(bean);
        }
        gridView.setAdapter(new CommonAdapter<>(this, mItemBeanList, R.layout.main_item_view, new ViewHolderHelperMain(this)));
    }

    @Override
    public void onItemClickedInList(String itemName) {
        //item的 点击回调
        if (itemName.equals(ITEMS_MAIN[0])) {
            skipActivity(XHttpActivity.class);
        }  else if (itemName.equals(ITEMS_MAIN[1])) {
            showErrorInfo(MainActivity.this, "由于小米手机适配问起暂时停止此功能", "适配问题禁止此功能");
//            uploadFile();
        }  else if (itemName.equals(ITEMS_MAIN[2])) {
            skipActivity(TabLayoutActivity.class);
        } else if (itemName.equals(ITEMS_MAIN[3])) {
            showErrorInfo(MainActivity.this, "暂未实现", "https");
        }
        else if (itemName.equals(ITEMS_MAIN[4])) {
            skipActivity(CoordinatorTabLayoutActivity.class);
        }
        else if (itemName.equals(ITEMS_MAIN[5])) {
            skipActivity(SimpleViewPagerActivity.class);
        }
        else if (itemName.equals(ITEMS_MAIN[6])) {
            skipActivity(FragmentViewPagerActivity.class);
        }
        else if (itemName.equals(ITEMS_MAIN[7])) {
            skipActivity(LauncherActivity.class);
        }
        else if (itemName.equals(ITEMS_MAIN[8])) {
            skipActivity(FileDownloadActivity.class);
        }
        else if (itemName.equals(ITEMS_MAIN[9])) {
            skipActivity(PictureActivity.class);
        }
    }

    /**
     * @param cls 跳转到activity
     */
    private void skipActivity(final Class<?> cls) {
        startActivity(new Intent(MainActivity.this, cls));
    }

    /**
     * 文件上传
     */
    private void uploadFile() {
        final String[] projectionPhotos = {
                MediaStore.Images.Media._ID,//每一列的ID 图片的ID
                MediaStore.Images.Media.BUCKET_ID,//图片所在文件夹ID
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,//图片所在文件夹名称
                MediaStore.Images.Media.DATA,//图片路径
                MediaStore.Images.Media.DATE_TAKEN,//图片创建时间
        };

        Cursor mCursor = null;
        mCursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , projectionPhotos, "", null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        int count = mCursor.getCount();
        int realCount = count;
        for (int i = 0; i < count; i++) {
            mCursor.moveToNext();
            String p = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            if (!new File(p).exists()) {
                --realCount;
            }
        }
        Toast.makeText(this, "一共 " + realCount + "张", Toast.LENGTH_SHORT).show();
        mCursor.moveToFirst();
        String[] path = new String[realCount];

        for (int i = 0; i < 5; i++) {

            // 获取图片的路径
            path[i] = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            JJLogger.logInfo(TAG, "onItemClickedInList: " + path[i]);
            mCursor.moveToNext();
        }
        mCursor.close();

        JJLogger.logInfo(TAG, "图片个数 :  " + path.length);
        XHttp.getInstance().post(WebApi.UPLOAD_FILE_URL)
                .uploadFiles(path)
                .setHeads("platform", "mobile_phone")
                .setOnXHttpCallback(new OnXHttpCallback() {
                    @Override
                    public void onSuccess(final Response response) {
                        JJLogger.logInfo(TAG, "MainActivity.onSuccess :" +
                                response.toString());
                        showErrorInfo(MainActivity.this, response.toString(), "");

                    }

                    @Override
                    public void onFailure(final Exception ex, final String errorCode) {
                        JJLogger.logInfo(TAG, "MainActivity.onFailure :" + ex.getMessage());
                    }
                });
    }



}
