package android.simple.toolbox.simple_pager;

import android.content.Context;
import android.support.v4.view.ViewPager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sikang on 2017/12/7.
 */

public class RecyclePagerAdapter extends PagerBaseAdapter {
    private List<Object> dataList;
    private boolean isCirculate = false;

    public RecyclePagerAdapter(ViewPager viewPager) {
        super(viewPager);
        dataList = new ArrayList<>();
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

    /**
     * 通过反射获取Pager对象
     */
    @Override
    SimplePager getPager(int position) {
        try {
            Constructor<?> constructor = layoutList.get(position).getConstructor(Context.class);
            SimplePager pager = (SimplePager) constructor.newInstance(context);
            pager.initSign(position);
            pager.onCreate(pager.rootView, dataList.get(position));
            return pager;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Constructor not found! The child of SimplePager need a Constructor(Context context)!");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (!isCirculate)
            return;
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
