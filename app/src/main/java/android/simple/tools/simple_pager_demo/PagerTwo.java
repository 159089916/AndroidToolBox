package android.simple.tools.simple_pager_demo;

import android.content.Context;
import android.simple.toolbox.simple_pager.SimplePager;
import android.simple.tools.R;
import android.util.Log;
import android.view.View;

/**
 * Created by Sikang on 2017/12/6.
 */

public class PagerTwo extends SimplePager {
    public PagerTwo(Context context) {
        super(context, R.layout.pager2);
    }

    @Override
    public void onCreate(View view) {
        Log.d("PagerTest", "PagerTwo被创建");
    }

    @Override
    public void onCreate(View view, Object obj) {

    }

    @Override
    public void onAttach() {
        Log.d("PagerTest", "PagerTwo被添加到ViewPager");
    }

    @Override
    public void onDetached() {
        Log.d("PagerTest", "PagerTwo被移出ViewPager");
    }

    @Override
    public void onVisible() {
        Log.d("PagerTest", "PagerTwo 当前可见");
    }

    @Override
    public void onInvisible() {
        Log.d("PagerTest", "PagerTwo 当前不可见");
    }
}