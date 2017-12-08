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
    protected int lastSelected = 0;
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

    public void setOnPageSelectedListener(OnPageSelectedListener listener) {
        this.listener = listener;
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
        SimplePager pager = getPager(position);
        container.addView(pager.view());
        pagerList.add(pager);
        pager.onAttach();
        return pager.view();
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

    abstract SimplePager getPager(int position);


}
