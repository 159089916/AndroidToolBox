package android.simple.toolbox.simple_tab;

/**
 * Created by Sikang on 2017/11/30.
 */

public interface TabGroup {
    int getId();

    /**
     * item数量
     */
    int getTabCount();

    /**
     * 获得item
     */
    TabView[] getTabs();

    /**
     * 合并单选组
     */
    void addTabGroup(TabGroup group);

    /**
     * 指定选中的
     */
    int getLastSelectedIndex();

    /**
     * 获得选中项
     */
    TabView[] getSelected();

    /**
     * 获取当前选中的index
     */
    void addSelected(int selectedIndex);

    /**
     * tab在单选组中的index
     */
    int indexOfTab(TabView tab);

    /**
     * 通知观察者
     */
    void notifyObservers(TabView tabView);


    /**
     * 设置选中事件
     */
    void setOnTabSelectedListener(OnTabSelectedListener listener);

    /**
     * 被其他TabGroup合并
     */
    void beMerged(TabGroup parent);

    /**
     * 被其他TabGroup释放
     */
    void recycleMerged();

    /**
     * 设置最大选中数量 1为单选  >1 为多选
     */
    void setSelecteCount(int maxCount);

    /**
     * 单选选中事件
     */
    interface OnTabSelectedListener {
        /**
         * 选中
         *
         * @param tab      最后被选中的item
         * @param position 被选中的item索引
         * @param tabs     当前被选中的所有item（多选模式下使用）
         * @param parent   所在TabGroup
         */
        void onTabSelected(TabView tab, int position, TabView[] tabs, TabGroup parent);

        // 取消选中
        void onTabCancle(TabView tab, int position, TabView[] tabs, TabGroup parent);
    }
}
