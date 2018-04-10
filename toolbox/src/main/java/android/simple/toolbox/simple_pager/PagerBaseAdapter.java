package android.simple.toolbox.simple_pager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sikang on 2017/12/7.
 */

public abstract class PagerBaseAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    protected OnPageSelectedListener listener;
    protected int selected = -1;
    protected List<Class> layoutList;
    protected List<SimplePager> pagerList;
    protected Context context;
    protected ViewPager parentViewPager;

    public PagerBaseAdapter(ViewPager viewPager) {
        if (viewPager == null)
            throw new RuntimeException("the param \"viewPager\" can not be null!");

        layoutList = new ArrayList<>();
        pagerList = new ArrayList<>();
        parentViewPager = viewPager;
        this.context = viewPager.getContext();

        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(this);
    }

    public PagerBaseAdapter setOnPageSelectedListener(OnPageSelectedListener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnPageSelectedListener {
        void onPageSelected(int position);

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
    public void destroyItem(final ViewGroup container, final int position, Object object) {
        container.post(new Runnable() {
            @Override
            public void run() {
                SimplePager removePager = null;
                for (SimplePager pager : pagerList) {
                    if (pager.position == position)
                        removePager = pager;
                }
                if (removePager != null) {
                    removePager.onDetached();
                    container.removeView(removePager.rootView);
                    pagerList.remove(removePager);
                }
            }
        });
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (listener != null)
            listener.onPageSelected(position);
        //向左移动
        boolean isMoveLeft = position > selected;

        int invisiblePager = isMoveLeft ? position - 2 : position + 2;


        for (SimplePager pager : pagerList) {
            if (pager.position == position)
                pager.onVisible();
            else if (invisiblePager > 0 && invisiblePager < layoutList.size()) {
                if (pager.position == position)
                    pager.onInvisible();
            }
        }
        selected = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void onRefresh() {
        for (SimplePager pager : pagerList) {
            pager.onRefresh();
        }
    }

    abstract SimplePager getPager(int position);


    public void onStart() {
        if (selected > 0)
            pagerList.get(selected).onStart();
    }

    public void onPause() {
        if (selected > 0)
            pagerList.get(selected).onPause();
    }

    public void onStop() {
        if (selected > 0)
            pagerList.get(selected).onStop();
    }

}
