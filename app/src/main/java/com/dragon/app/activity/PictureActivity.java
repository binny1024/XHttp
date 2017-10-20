package com.dragon.app.activity;


import android.widget.ListView;

import com.dragon.R;
import com.dragon.abs.activity.BaseActivity;
import com.dragon.app.bean.MoocBean;
import com.dragon.app.holder.ListDataViewHolderHelper;
import com.smart.holder.CommonAdapter;

import java.util.List;


public class PictureActivity extends BaseActivity {

    private ListView mListView;

    private List<MoocBean> mDataBeanList;
    @Override
    protected void afterInit() {
        mListView.setAdapter(new CommonAdapter(this, mDataBeanList, R.layout.picture_list_item,new ListDataViewHolderHelper()));
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_qqmain;
    }

    @Override
    protected void initView() {
        mListView = (ListView) findViewById(R.id.picture_lv);
    }

    @Override
    protected void initData() {

    }
}
