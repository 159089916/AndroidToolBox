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
 * Created by Sikang on 2017/12/6.
 */

public class SimplePagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private List<Class> layoutList;
    private List<SimplePager> pagerList;
    private int lastSelected = 0;
    private OnPageSelectedListener listener;

    private Context context;

    public SimplePagerAdapter(ViewPager viewPager) {
        layoutList = new ArrayList<>();
        pagerList = new ArrayList<>();
        if (viewPager == null)
            throw new RuntimeException("the param \"viewPager\" can not be null!");

        this.context = viewPager.getContext();
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(this);
    }

    public <T extends SimplePager> SimplePagerAdapter addPager(Class<T> pagerClz) {
        layoutList.add(pagerClz);
        notifyDataSetChanged();
        return this;
    }

    public SimplePagerAdapter addPager(Class... pagerClzs) {
        if (pagerClzs != null && pagerClzs.length > 0) {
            for (Class clz : pagerClzs)
                layoutList.add(clz);
            notifyDataSetChanged();
        }
        return this;
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


    public void setOnPageSelectedListener(OnPageSelectedListener listener) {
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
            pager.onCreate(pager.rootView);
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

    }
}
