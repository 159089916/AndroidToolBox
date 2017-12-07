package android.simple.tools.simple_pager_demo;

import android.content.Context;
import android.simple.toolbox.simple_pager.SimplePager;
import android.simple.tools.R;
import android.util.Log;
import android.view.View;

/**
 * Created by Sikang on 2017/12/6.
 */

public class PagerThree extends SimplePager {
    public PagerThree(Context context) {
        super(context, R.layout.pager3);
    }

    @Override
    public void onCreate(View view) {
        Log.d("PagerTest", "PagerThree被创建");
    }

    @Override
    public void onCreate(View view, Object obj) {

    }

    @Override
    public void onAttach() {
        Log.d("PagerTest", "PagerThree被添加到ViewPager");
    }

    @Override
    public void onDetached() {
        Log.d("PagerTest", "PagerThree被移出ViewPager");
    }

    @Override
    public void onVisible() {
        Log.d("PagerTest", "PagerThree 当前可见");
    }

    @Override
    public void onInvisible() {
        Log.d("PagerTest", "PagerThree 当前不可见");
    }
}