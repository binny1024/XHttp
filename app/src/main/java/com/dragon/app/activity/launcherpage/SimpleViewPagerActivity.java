package com.dragon.app.activity.launcherpage;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dragon.R;

import java.util.ArrayList;
import java.util.List;

public class SimpleViewPagerActivity extends Activity {

    private List<View> viewContainter = new ArrayList<>();

    private List<String> titleContainer = new ArrayList<>();

    private ViewPager pager = null;
    private PagerTabStrip tabStrip = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_view_pager);
        initTitle();
        initTab();
    }

    private void initTitle() {
        //页签项
        titleContainer.add("今日头条");
        titleContainer.add("今天热点");
        titleContainer.add("今日财经");
        titleContainer.add("今日军事");
    }

    private void initTab() {
        pager = (ViewPager) this.findViewById(R.id.viewpager);

        tabStrip = (PagerTabStrip) this.findViewById(R.id.tabstrip);
        //取消tab下面的长横线
        tabStrip.setDrawFullUnderline(false);
        //设置tab的背景色
        tabStrip.setBackgroundResource(R.color.white);
        //设置当前tab页签的下划线颜色
        tabStrip.setTabIndicatorColorResource(R.color.red);
        tabStrip.setTextSpacing(400);

        View tab1 = LayoutInflater.from(this).inflate(R.layout.simple_view_pager_tab, null);
        View tab2 = LayoutInflater.from(this).inflate(R.layout.simple_view_pager_tab, null);
        View tab3 = LayoutInflater.from(this).inflate(R.layout.simple_view_pager_tab, null);
        View tab4 = LayoutInflater.from(this).inflate(R.layout.simple_view_pager_tab, null);

        tab1.setBackgroundColor(getResources().getColor(R.color.hotpink));
        setTabText(tab1, "page1");
        tab2.setBackgroundColor(getResources().getColor(R.color.aquamarine));
        setTabText(tab2, "page2");
        tab3.setBackgroundColor(getResources().getColor(R.color.blanchedalmond));
        setTabText(tab3, "page3");
        tab4.setBackgroundColor(getResources().getColor(R.color.darkorange));
        setTabText(tab4, "page4");

        viewContainter.add(tab1);
        viewContainter.add(tab2);
        viewContainter.add(tab3);
        viewContainter.add(tab4);
        //设置Adapter
        pager.setAdapter(new MyPagerAdapters());
    }

    private void setTabText(View view, String title) {
        ((TextView) view.findViewById(R.id.simple_tab_text)).setText(title);
    }

    /**
     * ViewPager的数据适配器
     */
    class MyPagerAdapters extends PagerAdapter {
        //返回可以滑动的VIew的个数
        @Override
        public int getCount() {
            return viewContainter.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleContainer.get(position);
        }

        //滑动切换的时候销毁当前的组件
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            ((ViewPager) container).removeView(viewContainter.get(position));
        }

        //将当前视图添加到container中并返回当前View视图
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(viewContainter.get(position));
            return viewContainter.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }
}
