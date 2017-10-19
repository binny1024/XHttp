package com.dragon.app.activity.launcherpage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.dragon.R;
import com.dragon.app.fragment.FragmentView;

import java.util.ArrayList;
import java.util.List;

public class FragmentViewPagerActivity extends FragmentActivity {
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmeng_view_pager);

        viewPager= (ViewPager) findViewById(R.id.vp);
        initData();
    }

    private void initData() {
        List<Fragment> list=new ArrayList<>();

        Bundle bundle1=new Bundle();
        bundle1.putString("Title","第一个Fragment");
        bundle1.putInt("pager_num",1);
        Fragment fg1=FragmentView.newInstance(bundle1);

        Bundle bundle2=new Bundle();
        bundle2.putString("Title","第二个Fragment");
        bundle2.putInt("pager_num",2);
        Fragment fg2= FragmentView.newInstance(bundle2);

        Bundle bundle3=new Bundle();
        bundle3.putString("Title","第三个Fragment");
        bundle3.putInt("pager_num",3);
        Fragment fg3=FragmentView.newInstance(bundle3);

        Bundle bundle4=new Bundle();
        bundle4.putString("Title","第四个Fragment");
        bundle4.putInt("pager_num",4);
        Fragment fg4=FragmentView.newInstance(bundle4);

        list.add(fg1);
        list.add(fg2);
        list.add(fg3);
        list.add(fg4);

        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(),list));
    }


    /*
    * FragmentPagerAdapter 继承自 PagerAdapter。
    * 相比通用的 PagerAdapter，该类更专注于每一页均为 Fragment 的情况。
    * 该类内的每一个生成的 Fragment 都将保存在内存之中，尽管不可见的视图有时会被销毁，
    * 但用户所有访问过的fragment都会被保存在内存中，因此fragment实例会保存大量的各种状态，
    * 这就造成了很大的内存开销。所以FragmentPagerAdapter比较适用于那些相对静态的页，
    * 数量也比较少的应用情景，如主流主界面；如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，
    * 应该使用FragmentStatePagerAdapter。
    * 对应实现FragmentPagerAdapter ，我们只需重写getCount()与getItem()两个方法，
    * 因此相对于继承自 PagerAdapter，更方便一些。
    * */
    /*
    * 接下来我们来看看如何用代码实现FragmentPagerAdapter
    * */
    class MyFragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> list;

        public MyFragmentAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list=list;
        }

        /**
         * 返回需要展示的fragment
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        /**
         * 返回需要展示的fangment数量
         * @return
         */
        @Override
        public int getCount() {
            return list.size();
        }
    }

    /*
    * FragmentStatePagerAdapter 和 FragmentPagerAdapter 一样，是继承子 PagerAdapter。
    * 但是它们的不同点在于其类名中的 ‘State’ 所表明的含义一样，该 PagerAdapter 的实现将只保留当前页面，
    * 当页面离开视线后，就会被消除，释放其资源；而在页面需要显示时，再生成新的页面。
    * 这样实现的最大好处在于当拥有大量的页面时，不必在内存中占用大量的内存。我
    * 们在实现FragmentStatePagerAdapter是也同样只需重写getCount()与getItem()两个方法，
    * 而且其方法含义跟FragmentPagerAdapter是一样的。下面我们来看看实现案例，其实就改了个继承类而已。
    * */
    class MyFragmentStateAdapter extends FragmentStatePagerAdapter {

        List<Fragment> list;

        public MyFragmentStateAdapter(FragmentManager fm,List<Fragment> list) {
            super(fm);
            this.list=list;
        }

        /**
         * 返回需要展示的fragment
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        /**
         * 返回需要展示的fangment数量
         * @return
         */
        @Override
        public int getCount() {
            return list.size();
        }
    }
}
