package android.simple.tools.simple_pager_demo;

import android.simple.toolbox.simple_pager.RecyclePagerAdapter;
import android.simple.toolbox.simple_pager.SimplePagerAdapter;
import android.simple.tools.R;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;


public class SimplePagerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_pager);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPger);
        final ViewPager banner = (ViewPager) findViewById(R.id.banner);

        new SimplePagerAdapter(viewPager)
                .addPager(PagerOne.class)
                .addPager(PagerTwo.class, PagerThree.class);


        final List<String> urlList = new ArrayList<>();
        urlList.add("http://img.ivsky.com/img/bizhi/pre/201703/06/huanle_haoshengyin.jpg");
        urlList.add("http://img.ivsky.com/img/bizhi/pre/201703/06/huanle_haoshengyin-001.jpg");
        urlList.add("http://img.ivsky.com/img/bizhi/pre/201703/06/huanle_haoshengyin-002.jpg");
        urlList.add("http://img.ivsky.com/img/bizhi/pre/201703/06/huanle_haoshengyin-003.jpg");
        urlList.add("http://img.ivsky.com/img/bizhi/pre/201703/06/huanle_haoshengyin-004.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512633172513&di=848c4430c769cec2661406d986495451&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0173535977357ca8012193a3e6ce01.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512633172616&di=192d9c5b865754c70722d1735cd9ec1c&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01b52757a1f2070000018c1b377293.jpg%401280w_1l_2o_100sh.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512633172616&di=393451afa6a259aa13bed28128c3e61c&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F014136570e84fe32f8751b3fcba344.jpg%401280w_1l_2o_100sh.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512633172616&di=1dd2c3281842d94137ff23031e337b36&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0122935809ae72a84a0e282b4715ce.png");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512633172616&di=a4325f84f51dc2b108712d7ba6cd6568&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F016f705553f4090000009c5091c0b7.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512633172616&di=ec826c98cf18fa3ae811382feecd02fa&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0135a256621fa66ac725b2c8445650.jpg");
        urlList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512633172615&di=cccfbac07a21ebc2244aa0c5023b5a81&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0153b557f9f9a0a84a0d304fb91a49.jpg%40900w_1l_2o_100sh.jpg");
        new RecyclePagerAdapter(banner)
                .setData(urlList, BannerView.class)
                .openCirculate();
    }
}
