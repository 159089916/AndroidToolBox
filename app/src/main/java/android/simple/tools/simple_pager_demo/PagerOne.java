package android.simple.tools.simple_pager_demo;

import android.content.Context;
import android.simple.toolbox.simple_pager.SimplePager;
import android.simple.tools.R;
import android.util.Log;
import android.view.View;

/**
 * Created by Sikang on 2017/12/6.
 */

public class PagerOne extends SimplePager {

    public PagerOne(Context context) {
        super(context, R.layout.pager1);
    }

    @Override
    public void onCreate(View view) {
        Log.d("PagerTest", "PagerOne被创建");
    }

    @Override
    public void onCreate(View view, Object obj) {
        Log.d("PagerTest", "PagerOne被创建");
    }

    @Override
    public void onAttach() {
        Log.d("PagerTest", "PagerOne被添加到ViewPager");
    }

    @Override
    public void onDetached() {
        Log.d("PagerTest", "PagerOne被移出ViewPager");
    }

    @Override
    public void onVisible() {
        Log.d("PagerTest", "PagerOne 当前可见");
    }

    @Override
    public void onInvisible() {
        Log.d("PagerTest", "PagerOne 当前不可见");
    }
}
