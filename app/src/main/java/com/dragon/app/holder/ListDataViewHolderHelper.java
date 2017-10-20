package com.dragon.app.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.dragon.R;
import com.dragon.app.bean.MoocBean;
import com.dragon.app.helper.ListDataViewHolder;
import com.dragon.util.UtilWidget;
import com.smart.holder.iinterface.IViewHolder;
import com.smart.holder.iinterface.IViewHolderHelper;

import java.util.List;

/**
 * Created by smart on 2017/4/26.
 */

/*
* 实例化你的viewholder
* 将数据和viewholder的控件绑定
* */
public class ListDataViewHolderHelper implements IViewHolderHelper<ListDataViewHolder,MoocBean.DataBean> {

    @Override
    public IViewHolder initItemViewHolder(ListDataViewHolder viewHolder, @NonNull View convertView) {
        viewHolder = new ListDataViewHolder();

        viewHolder.picSmall = UtilWidget.getView(convertView,R.id.icon1);

        return viewHolder;
    }

    @Override
    public void bindListDataToView(Context context, List<MoocBean.DataBean> iBaseBeanList, ListDataViewHolder viewHolder, int position) {
//        UtilImageloader.setImage(context,iBaseBeanList.get(position).getPicSmall(),viewHolder.picSmall);
    }
}
