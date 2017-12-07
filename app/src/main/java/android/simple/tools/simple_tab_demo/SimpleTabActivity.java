package android.simple.tools.simple_tab_demo;

import android.os.Bundle;
import android.simple.toolbox.simple_tab.LinearTabGroup;
import android.simple.toolbox.simple_tab.RelativeTabGroup;
import android.simple.toolbox.simple_tab.TabGroup;
import android.simple.toolbox.simple_tab.TabView;
import android.simple.tools.R;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class SimpleTabActivity extends AppCompatActivity implements TabGroup.OnTabSelectedListener {
    LinearTabGroup tabGroup;
    LinearTabGroup tabGroup2;
    RelativeTabGroup tabGroup3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_tab);
        tabGroup = (LinearTabGroup) this.findViewById(R.id.tabGroup);
        tabGroup2 = (LinearTabGroup) this.findViewById(R.id.tabGroup2);
        tabGroup3 = (RelativeTabGroup) this.findViewById(R.id.tabGroup3);
        //将第二个Group并如 第一个Group
        tabGroup.addTabGroup(tabGroup2);


        //设置选中事件
        tabGroup.setOnTabSelectedListener(this);
        tabGroup3.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabView tab, int position, TabView[] tabs, TabGroup parent) {
        switch (parent.getId()) {
            case R.id.tabGroup:
                Toast.makeText(SimpleTabActivity.this, "第一组第" + (position + 1) + "个被选中", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tabGroup3:
                Toast.makeText(SimpleTabActivity.this, "第二组第" + (position + 1) + "个被选中", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public void onTabCancle(TabView tab, int position, TabView[] tabs, TabGroup parent) {

    }
}
