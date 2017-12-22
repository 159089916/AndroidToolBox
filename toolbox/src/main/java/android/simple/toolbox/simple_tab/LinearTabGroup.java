package android.simple.toolbox.simple_tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.simple.toolbox.R;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sikang on 2017/11/30.
 */

public class LinearTabGroup extends LinearLayout implements TabGroup {
    /**
     * items
     */
    private List<TabView> tabList;

    /**
     * 最后选中
     */
    private int lastSelected = -1;

    /**
     * 最大选中数量
     */
    private int maxSelected = 1;

    /**
     * 被选中的item
     */
    private List<TabView> selectedTabs;

    /**
     * 选中事件
     */
    private TabGroup.OnTabSelectedListener mTabSelectedListener;


    @Override
    public void removeAllViews() {
        super.removeAllViews();
        tabList.clear();
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        if (view instanceof TabView)
            tabList.remove(view);
    }

    public LinearTabGroup(Context context) {
        super(context);
        init();
    }

    public LinearTabGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LinearTabGroup);
        maxSelected = array.getInt(R.styleable.LinearTabGroup_maxSelect, 1);
        if (maxSelected < 1)
            maxSelected = 1;
        array.recycle();
        init();
    }

    public LinearTabGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 当布局加载完成调用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 0)
            lastSelected = -1;
    }


    private void init() {
        tabList = new ArrayList<>();
        selectedTabs = new ArrayList<>(maxSelected + 1);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        addSubject(child);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        addSubject(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        addSubject(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        addSubject(child);
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
        addSubject(child);
    }


    /**
     * 记录items
     */
    private void addSubject(View child) {
        if (child instanceof TabView) {
            TabView tab = (TabView) child;
            if (!tabList.contains(tab)) {
                tab.registerObserver(this);
                tabList.add(tab);
            }
        }
    }


    @Override
    public int getTabCount() {
        return tabList.size();
    }

    @Override
    public TabView[] getTabs() {
        return tabList.toArray(new TabView[selectedTabs.size()]);
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public void addTabGroup(TabGroup group) {
        for (TabView tab : group.getTabs()) {
            tab.unSelect();
            tab.registerObserver(this);
            addSubject(tab);
        }
    }

    @Override
    public int getLastSelectedIndex() {
        return lastSelected;
    }

    @Override
    public TabView[] getSelected() {
        return getSelectedTabArray();
    }


    @Override
    public void addSelected(int selectedIndex) {
        tabList.get(selectedIndex).select();
    }

    @Override
    public void setOnTabSelectedListener(TabGroup.OnTabSelectedListener listener) {
        this.mTabSelectedListener = listener;
    }

    @Override
    public void beMerged(TabGroup parent) {
    }

    @Override
    public void recycleMerged() {
        for (TabView tab : tabList) {
            if (selectedTabs.contains(tab))
                tab.select();
            tab.registerObserver(this);
        }
    }

    @Override
    public void setSelecteCount(int maxCount) {
        maxSelected = maxCount;
        if (selectedTabs.size() > maxCount) {
            int size = selectedTabs.size();
            List<TabView> cancle = new ArrayList<>();
            for (int i = 0; i + maxCount < size; i++) {
                selectedTabs.get(i).unSelect();
                cancle.add(selectedTabs.get(i));
            }
            if (cancle.size() > 0)
                selectedTabs.removeAll(cancle);
        }
    }


    @Override
    public int indexOfTab(TabView tab) {
        return tabList.indexOf(tab);
    }

    @Override
    public void notifyObservers(TabView tabView) {
        if (selectedTabs.contains(tabView)) {
            //多选模式可以取消选中
            if (maxSelected > 1) {
                tabView.unSelect();
                mTabSelectedListener.onTabCancle(tabView, tabList.indexOf(tabView), getSelectedTabArray(), this);
                selectedTabs.remove(tabView);
                lastSelected = tabList.indexOf(selectedTabs.get(selectedTabs.size() - 1));
            }
            return;
        }
        lastSelected = tabList.indexOf(tabView);
        //添加选中项
        selectedTabs.add(tabView);
        if (selectedTabs.size() > maxSelected) {
            selectedTabs.get(0).unSelect();
            selectedTabs.remove(0);
        }
        mTabSelectedListener.onTabSelected(tabView, lastSelected, getSelectedTabArray(), this);

//        for (TabView observer : tabList) {
//            if (!selectedTabs.contains(tabList))
//                observer.unSelect();
//            else {
//                selectedIndex = tabList.indexOf(observer);
//                if (mTabSelectedListener != null)
//                    mTabSelectedListener.onTabSelected(tabView, selectedIndex, getTabArray(), this);
//            }
//        }
    }

    private TabView[] getSelectedTabArray() {
        return selectedTabs.toArray(new TabView[selectedTabs.size()]);
    }
}
