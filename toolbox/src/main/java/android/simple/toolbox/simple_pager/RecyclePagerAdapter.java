package android.simple.toolbox.simple_pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sikang on 2017/12/7.
 */

public class RecyclePagerAdapter extends PagerBaseAdapter {
    private List<Object> dataList;
    private boolean isCirculate = false;
    private boolean isAutoPager = false;
    protected OnItemClickListener mListener;
    private Timer mTimer;
    private long time;

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

    public RecyclePagerAdapter openAutoPager(final long time) {
        if (!isAutoPager && time >= 100 && dataList.size() >= 2) {
            isAutoPager = true;
            this.time = time;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isAutoPager) {
                        try {
                            Thread.sleep(time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        parentViewPager.post(new Runnable() {
                            @Override
                            public void run() {
                                int target = selected >= 0 ? selected + 1 : 1;
                                parentViewPager.setCurrentItem(target, true);
                            }
                        });

                    }
                }
            }).start();
        }
        return this;
    }

    public void closeAutoPager() {
        isAutoPager = false;
        this.time = 0;
//        cancleTimer();
    }

    private boolean isPagerRunning = false;

    private void startTimer() {
        //是否开启自动轮播
        if (mTimer == null) {
            mTimer = new Timer();
            if (time >= 100)
                mTimer.schedule(new MyTask(), time, time);
        }
    }

    private void cancleTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    //定时轮播
    class MyTask extends TimerTask {
        @Override
        public void run() {
            int target = selected >= 0 ? selected + 1 : 1;
            parentViewPager.setCurrentItem(target, true);
        }

    }


    private void circulate() {
        if (isCirculate && dataList.size() > 1) {
//            dataList.add(0, dataList.get(dataList.size() - 1));
//            dataList.add(dataList.get(1));
            layoutList.add(layoutList.get(0));
            layoutList.add(layoutList.get(0));
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 通过反射获取Pager对象
     */
    @Override
    SimplePager getPager(int position) {
        try {
            Constructor<?> constructor = layoutList.get(position).getConstructor(Context.class);
            SimplePager pager = (SimplePager) constructor.newInstance(context);
            if (mListener != null)
                pager.setOnItemClickListener(mListener);
            pager.position = position;

            if (position == layoutList.size() - 1)
                pager.sign = 0;
            else if (position == 0)
                pager.sign = dataList.size() - 1;
            else
                pager.sign = position - 1;
            pager.onCreate(pager.rootView, dataList.get(pager.sign));
            return pager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //若viewpager滑动未停止，直接返回
        if (state != ViewPager.SCROLL_STATE_IDLE)
            return;
        if (!isCirculate)
            return;
        //若当前为第一张，设置页面为倒数第二张
        if (selected == 0) {
            parentViewPager.setCurrentItem(layoutList.size() - 2, false);
        } else if (selected == layoutList.size() - 1) {
            // 若当前为倒数第一张，设置页面为第二张
            parentViewPager.setCurrentItem(1, false);
        }
    }

//    @Override
//    public void onPageSelected(int position) {
//        super.onPageSelected(position);
//        if (!isCirculate)
//            return;
//        //若当前为第一张，设置页面为倒数第二张
//        if (selected == 0) {
//            parentViewPager.setCurrentItem(layoutList.size() - 2, false);
//        } else if (selected == layoutList.size() - 1) {
//            // 若当前为倒数第一张，设置页面为第二张
//            parentViewPager.setCurrentItem(1, false);
//        }
//    }

    public interface OnItemClickListener {
        void onItemClick(View view, int tag, int position);
    }

}
