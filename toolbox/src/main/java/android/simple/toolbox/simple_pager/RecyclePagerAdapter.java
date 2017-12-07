package android.simple.toolbox.simple_pager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sikang on 2017/12/7.
 */

public class RecyclePagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private List<Object> dataList;
    private List<Class> layoutList;
    private List<SimplePager> pagerList;
    private int lastSelected = 0;
    private RecyclePagerAdapter.OnPageSelectedListener listener;
    private boolean isCirculate = false;
    private ViewPager parentViewPager;

    private Context context;

    public RecyclePagerAdapter(ViewPager viewPager) {
        layoutList = new ArrayList<>();
        pagerList = new ArrayList<>();
        dataList = new ArrayList<>();
        if (viewPager == null)
            throw new RuntimeException("the param \"viewPager\" can not be null!");

        parentViewPager = viewPager;

        this.context = viewPager.getContext();
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(this);
    }

    public <T, E extends SimplePager> RecyclePagerAdapter setData(List<T> dataList, Class<E> clz) {
        if (dataList != null && dataList.size() > 0) {
            this.dataList.clear();
            this.layoutList.clear();
            this.pagerList.clear();
            this.dataList.addAll(dataList);
            for (int i = 0; i < dataList.size(); i++)
                layoutList.add(clz);
            circulate();
            notifyDataSetChanged();
        }
        return this;
    }

    public RecyclePagerAdapter openCirculate() {
        if (!isCirculate) {
            isCirculate = true;
            circulate();
        }
        return this;
    }

    private void circulate() {
        if (isCirculate && dataList.size() > 1) {
            dataList.add(0, dataList.get(dataList.size() - 1));
            dataList.add(dataList.get(1));
            layoutList.add(layoutList.get(0));
            layoutList.add(layoutList.get(0));
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return layoutList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        try {
            SimplePager pager = getPager(position);

            container.addView(pager.view());
            pagerList.add(pager);
            pager.onAttach();

            return pager.view();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        SimplePager removePager = null;
        for (SimplePager pager : pagerList) {
            if (pager.sign == position)
                removePager = pager;
        }
        if (removePager != null) {
            removePager.onDetached();
            container.removeView(removePager.rootView);
            pagerList.remove(removePager);
        }
    }


    public void setOnPageSelectedListener(RecyclePagerAdapter.OnPageSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * 通过反射获取Pager对象
     */
    private SimplePager getPager(int position) {
        try {
            Constructor<?> constructor = layoutList.get(position).getConstructor(Context.class);
            SimplePager pager = (SimplePager) constructor.newInstance(context);
            pager.initSign(position);
            pager.onCreate(pager.rootView, dataList.get(position));
            return pager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface OnPageSelectedListener {
        void onPageSelected(int position);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (listener != null)
            listener.onPageSelected(position);
        //向左移动
        boolean isMoveLeft = position > lastSelected;

        int invisiblePager = isMoveLeft ? position - 2 : position + 2;

        for (SimplePager pager : pagerList) {
            if (pager.sign == position)
                pager.onVisible();
            else if (invisiblePager > 0 && invisiblePager < layoutList.size()) {
                if (pager.sign == position)
                    pager.onInvisible();
            }
        }
        lastSelected = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //若viewpager滑动未停止，直接返回
        if (state != ViewPager.SCROLL_STATE_IDLE)
            return;
        //若当前为第一张，设置页面为倒数第二张
        if (lastSelected == 0) {
            parentViewPager.setCurrentItem(layoutList.size() - 2, false);
        } else if (lastSelected == layoutList.size() - 1) {
            // 若当前为倒数第一张，设置页面为第二张
            parentViewPager.setCurrentItem(1, false);
        }
    }
}
