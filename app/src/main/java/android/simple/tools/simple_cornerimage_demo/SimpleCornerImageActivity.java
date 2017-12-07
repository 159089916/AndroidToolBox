package android.simple.tools.simple_cornerimage_demo;

import android.app.Activity;
import android.os.Bundle;
import android.simple.toolbox.widget.CornerImageView;
import android.support.annotation.Nullable;
import android.simple.tools.R;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Sikang on 2017/12/7.
 */

public class SimpleCornerImageActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cornerimage);
        CornerImageView imageView = (CornerImageView) findViewById(R.id.progress_imageview);
        imageView.progress(20);
        imageView.setOnCornerClickListener(new CornerImageView.OnCornerClickListener() {
            @Override
            public void onCornerClickListener(View view) {
                Toast.makeText(SimpleCornerImageActivity.this, "角标被点击", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
