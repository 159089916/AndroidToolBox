package android.simple.tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.simple.toolbox.utils.IntentBuilder;
import android.simple.tools.simple_cornerimage_demo.SimpleCornerImageActivity;
import android.simple.tools.simple_pager_demo.SimplePagerActivity;
import android.simple.tools.simple_tab_demo.SimpleTabActivity;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

/**
 * Created by Sikang on 2017/12/6.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button simplePagerBtn = (Button) findViewById(R.id.simplePagerBtn);
        Button simpleTabBtn = (Button) findViewById(R.id.simpleTabBtn);
        Button simpleCornerImageBtn = (Button) findViewById(R.id.simpleCornerImageBtn);
        simplePagerBtn.setOnClickListener(this);
        simpleTabBtn.setOnClickListener(this);
        simpleCornerImageBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.simplePagerBtn:
                IntentBuilder.build(MainActivity.this, SimplePagerActivity.class)
                        .start();
                break;
            case R.id.simpleTabBtn:
                IntentBuilder.build(MainActivity.this, SimpleTabActivity.class)
                        .start();
                break;
            case R.id.simpleCornerImageBtn:
                IntentBuilder.build(MainActivity.this, SimpleCornerImageActivity.class)
                        .start();
                break;

        }
    }

}
