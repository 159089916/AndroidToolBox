package android.simple.toolbox.simple_pager;

import android.content.Context;
import android.support.v4.view.ViewPager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Sikang on 2017/12/6.
 */

public class SimplePagerAdapter extends PagerBaseAdapter implements ViewPager.OnPageChangeListener {
    public SimplePagerAdapter(ViewPager viewPager) {
        super(viewPager);
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

    /**
     * 通过反射获取Pager对象
     */
    @Override
    SimplePager getPager(int position) {
        try {
            Constructor<?> constructor = layoutList.get(position).getConstructor(Context.class);
            SimplePager pager = (SimplePager) constructor.newInstance(context);
            pager.initSign(position);
            pager.onCreate(pager.rootView);
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
}
