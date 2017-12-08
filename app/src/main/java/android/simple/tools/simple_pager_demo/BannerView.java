package android.simple.tools.simple_pager_demo;

import android.content.Context;
import android.simple.toolbox.simple_imageloader.SimpleImageLoader;
import android.simple.toolbox.simple_pager.SimplePager;
import android.simple.tools.R;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Sikang on 2017/12/7.
 */

public class BannerView extends SimplePager {
    private String url = "";
    private ImageView imageView;

    /**
     * 注意继承SimplePager类 必须实现一个带有Context入参的构造，且只能有这一个参数
     * */
    public BannerView(Context context) {
        super(context, R.layout.banner);
    }

    @Override
    public void onCreate(View view) {

    }

    @Override
    public void onCreate(View view, Object obj) {
        url = (String) obj;
        Log.d("ImageTest", "index: " + sign + "url: " + url);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        if (!SimpleImageLoader.getInstance().isInitialize())
            SimpleImageLoader.getInstance().init(context.getApplicationContext());
        SimpleImageLoader.getInstance().loadFill(imageView, url);
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetached() {

    }

    @Override
    public void onVisible() {

    }

    @Override
    public void onInvisible() {

    }
}
