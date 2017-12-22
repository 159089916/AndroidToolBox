package android.simple.toolbox.simple_tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.simple.toolbox.R;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * Created by Sikang on 2017/11/16.
 */

public class TabView extends View {

    /**
     * 默认布局（图片在上）
     */
    private int layoutGravity = Gravity.TOP | Gravity.START;

    /**
     * 默认对齐（居中）
     */
    private int contentGravity = Gravity.CENTER;

    /**
     * 文字
     */
    private String tabText = "";

    /**
     * 选中状态图片
     */
    private Bitmap selectedBitmap;

    /**
     * 未选中状态图片
     */
    private Bitmap normalBitmap;

    private Bitmap drawBitmap;

    /**
     * 选中状态文字颜色
     */
    private int selectedColor = Color.parseColor("#058FF1");

    /**
     * 未选中状态文字颜色
     */
    private int normalColor = Color.parseColor("#000000");


    /**
     * 间距
     */
    private float drawablePadding = 10;


    /**
     * 字体大小
     */
    float textSize = 15;

    /**
     * 图片宽度
     */
    private float drawableWidth;

    /**
     * 图片高度
     */
    private float drawableHeight;

    /**
     * 只显示图片/文字
     */
    private boolean onlyDrawable = false;
    private boolean onlyText = false;

    /**
     * 观察者
     */
    private TabGroup observer;
    /**
     * 选中
     */
    private boolean isSelected = false;

    /**
     * 内容宽度
     */
    private float contentWidth = 0;

    /**
     * 内容高度
     */
    private float contentHeight = 0;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 文字部分宽度
     */
    private float textWidth;


    /**
     * 取消选中
     */
    private boolean isCancle = false;

    public TabView(Context context) {
        super(context);
        init();
    }

    public TabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获取属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TabView);
        setLayoutGravity(array.getInt(R.styleable.TabView_drawableGravity, Gravity.LEFT));
        setContentGravity(array.getInt(R.styleable.TabView_contentGravity, contentGravity));
        tabText = array.getString(R.styleable.TabView_tabText);
        textSize = array.getDimensionPixelOffset(R.styleable.TabView_textSize, 10);
        drawablePadding = array.getDimensionPixelOffset(R.styleable.TabView_drawablePadding, 10);
        drawableWidth = array.getDimensionPixelOffset(R.styleable.TabView_drawableWidth, 10);
        drawableHeight = array.getDimensionPixelOffset(R.styleable.TabView_drawableHeight, 10);
        int select = array.getResourceId(R.styleable.TabView_selectedColor, 0);
        int normal = array.getResourceId(R.styleable.TabView_normalColor, 0);
        selectedColor = select != 0 ? ContextCompat.getColor(context, select) : selectedColor;
        normalColor = normal != 0 ? ContextCompat.getColor(context, normal) : normalColor;

        onlyDrawable = array.getBoolean(R.styleable.TabView_onlyDrawable, false);
        onlyText = array.getBoolean(R.styleable.TabView_onlyText, false);
        if (onlyText)
            onlyDrawable = false;
        isSelected = array.getBoolean(R.styleable.TabView_selected, false);

        int resource = array.getResourceId(R.styleable.TabView_selectedDrawable, 0);
        //图片缩放
        if (resource != 0)
            selectedBitmap = resizeImage(BitmapFactory.decodeResource(getResources(), resource), Math.round(drawableWidth), Math.round(drawableHeight));

        int resource2 = array.getResourceId(R.styleable.TabView_normalDrawable, 0);
        if (resource2 != 0) {
            normalBitmap = resizeImage(BitmapFactory.decodeResource(getResources(), resource2), Math.round(drawableWidth), Math.round(drawableHeight));
        }
        drawBitmap = normalBitmap;

        array.recycle();
        init();
    }

    public TabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        mPaint.setColor(normalColor);
        mPaint.setAlpha(255);
        getContentWidth();
        getContentHeight();

        //自动开启点击监听
        setClickable(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isSelected)
            select();
    }

    /**
     * 内容宽度
     */
    private float getContentWidth() {
        textWidth = mPaint.measureText(tabText);
        float width = 0;
        if (onlyDrawable)
            width = drawableWidth;
        else if (onlyText)
            width = textWidth;
        else
            switch (layoutGravity) {
                case Gravity.LEFT:
                case Gravity.RIGHT:
                    width = textWidth + drawablePadding + drawableWidth;
                    break;
                case Gravity.TOP:
                case Gravity.BOTTOM:
                    width = textWidth > drawableWidth ? textWidth : drawableWidth;
                    break;
            }
        contentWidth = width;
        return width;
    }

    /**
     * 内容高度
     */
    private float getContentHeight() {
        float textHeight = measureTextHeight(1);
        float height = 0;
        if (onlyDrawable)
            height = drawableHeight;
        else if (onlyText)
            height = textHeight;
        else
            switch (layoutGravity) {
                case Gravity.LEFT:
                case Gravity.RIGHT:
                    height = textHeight > drawableHeight ? textHeight : drawableHeight;
                    break;
                case Gravity.TOP:
                case Gravity.BOTTOM:
                    height = textHeight + drawablePadding + drawableHeight;
                    break;
            }
        contentHeight = height;
        return height;
    }

    /**
     * 计算宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        float width = MeasureSpec.getSize(widthMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);
        //如果是 wrap_content
        if (widthMode == MeasureSpec.AT_MOST)
            width = getContentWidth() + getPaddingLeft() + getPaddingRight();
        if (heightMode == MeasureSpec.AT_MOST) {
            height = getContentHeight() + getPaddingTop() + getPaddingBottom();
        }
        int widthSpec = MeasureSpec.makeMeasureSpec((int) width, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, heightSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float bitmapStartX = 0;
        float bitmapStartY = 0;
        float textStartX = 0;
        float textStartY = 0;
        float textHeight = measureTextHeight(0.65f);
        if (!onlyText && !onlyDrawable) {
            float left = measureLeft(getWidth(), contentWidth);
            float top = measureTop(getHeight(), contentHeight);
            switch (layoutGravity) {
                case Gravity.LEFT:
                    bitmapStartX = left;
                    bitmapStartY = drawableHeight >= textHeight ? top : top + heightDiff() / 2;
                    textStartX = bitmapStartX + drawableWidth + drawablePadding;
                    textStartY = (textHeight >= drawableHeight ? top : top + heightDiff() / 2) + textHeight;
                    break;
                case Gravity.TOP:
                    bitmapStartX = drawableWidth >= textWidth ? left : left + widthDiff() / 2;
                    bitmapStartY = top;
                    textStartX = textWidth >= drawableWidth ? left : left + widthDiff() / 2;
                    textStartY = bitmapStartY + drawablePadding + drawableHeight + textHeight;
                    break;
                case Gravity.RIGHT:
                    textStartX = left;
                    textStartY = (textHeight >= drawableHeight ? top : top + heightDiff() / 2) + textHeight;
                    bitmapStartX = textStartX + textWidth + drawablePadding;
                    bitmapStartY = drawableHeight >= textHeight ? top : top + heightDiff() / 2;
                    break;
                case Gravity.BOTTOM:
                    textStartX = textWidth >= drawableWidth ? left : left + widthDiff() / 2;
                    textStartY = top + measureTextHeight(1);
                    bitmapStartX = drawableWidth >= textWidth ? left : left + widthDiff() / 2;
                    bitmapStartY = textStartY + drawablePadding;
                    break;
            }
            canvas.drawText(tabText, textStartX, textStartY, mPaint);
            if (drawBitmap != null)
                canvas.drawBitmap(drawBitmap, bitmapStartX, bitmapStartY, mPaint);
        } else if (onlyText) {
            textStartX = (getWidth() - textWidth) / 2;
            textStartY = (getHeight() - textHeight) / 2 + textHeight;
            canvas.drawText(tabText, textStartX, textStartY, mPaint);
        } else if (onlyDrawable) {
            bitmapStartX = (getWidth() - textWidth) / 2;
            bitmapStartY = (getHeight() - textHeight) / 2;
            if (drawBitmap != null)
                canvas.drawBitmap(drawBitmap, bitmapStartX, bitmapStartY, mPaint);
        }
    }

    /**
     * 文字部分和icon部分，高度差
     */
    private float heightDiff() {
        return Math.abs(drawableHeight - measureTextHeight(0.65f));
    }

    /**
     * 文字部分和icon部分，宽度差
     */
    private float widthDiff() {
        return Math.abs(drawableWidth - textWidth);
    }

    public void registerObserver(TabGroup observer) {
        this.observer = observer;
    }

    public void select() {
        if (isCancle) {
            isCancle = false;
            if (observer != null)
                observer.notifyObservers(this);
            return;
        }
        mPaint.setColor(selectedColor);
        drawBitmap = selectedBitmap;
        isSelected = true;
        if (observer != null)
            observer.notifyObservers(this);
        postInvalidate();
    }

    //取消选中
    public void unSelect() {
        mPaint.setColor(normalColor);
        drawBitmap = normalBitmap;
        isSelected = false;
        postInvalidate();
    }


    /**
     * 拦截点击事件
     */
    @Override
    public boolean performClick() {
        isCancle = isSelected;
        select();
        return super.performClick();
    }

    /**
     * 拦截点击事件
     */
    @Override
    public boolean callOnClick() {
        isCancle = isSelected;
        select();
        return super.callOnClick();
    }

    /**
     * 缩放图片
     */
    public Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        if (w == width && h == height)
            return bitmap;

        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }


    /**
     * 布局方式
     */
    public void setLayoutGravity(int gravity) {
        switch (gravity) {
            case 0:
                layoutGravity = Gravity.LEFT;
                break;
            case 1:
                layoutGravity = Gravity.TOP;
                break;
            case 2:
                layoutGravity = Gravity.RIGHT;
                break;
            case 3:
                layoutGravity = Gravity.BOTTOM;
                break;
            default:
                layoutGravity = Gravity.LEFT;
                break;
        }
        invalidate();
    }


    /**
     * 内容对齐方式
     */
    private void setContentGravity(int gravity) {
        if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.START;
        }
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.TOP;
        }

        if (gravity != layoutGravity) {
            contentGravity = gravity;
            invalidate();
        }
    }

    private float measureTextHeight(float scale) {
        return mPaint.measureText("高") * scale;
    }


    /**
     * top
     */
    public float measureTop(float layoutHeight, float contentHeight) {
        float top = getPaddingTop();
        final float gravity = contentGravity & Gravity.VERTICAL_GRAVITY_MASK;
        if (gravity == Gravity.TOP) {
            return top;
        } else if (gravity == Gravity.BOTTOM) {
            return top + layoutHeight - contentHeight;
        } else { // (gravity == Gravity.CENTER_VERTICAL)
            return top + (layoutHeight - contentHeight) / 2;
        }
    }

    /**
     * left
     */
    public float measureLeft(float layoutWidth, float contentWidth) {
        float left = getPaddingLeft();
        final float gravity = contentGravity & Gravity.HORIZONTAL_GRAVITY_MASK;
        if (gravity == Gravity.LEFT) {
            return left;
        } else if (gravity == Gravity.RIGHT) {
            return left + layoutWidth - contentWidth;
        } else { // (gravity == Gravity.CENTER_HORIZONTAL)
            return left + (layoutWidth - contentWidth) / 2;
        }
    }


    public void setTabText(String tabText) {
        this.tabText = tabText;
        invalidate();
    }

    public void setSelectedBitmap(Bitmap bitmap) {
        this.selectedBitmap = resizeImage(bitmap, Math.round(drawableWidth), Math.round(drawableHeight));
        invalidate();
    }


    public void setSelectedDrawable(int id) {
        if (id != 0) {
            this.selectedBitmap = resizeImage(BitmapFactory.decodeResource(getResources(), id), Math.round(drawableWidth), Math.round(drawableHeight));
        }
    }


    public void setNormalBitmap(Bitmap bitmap) {
        this.normalBitmap = resizeImage(bitmap, Math.round(drawableWidth), Math.round(drawableHeight));
        invalidate();
    }


    public void setSelectedColor(int color) {
        if (color != 0) {
            selectedColor = ContextCompat.getColor(getContext(), color);
            if (isSelected) {
                mPaint.setColor(selectedColor);
                invalidate();
            }
        }
    }

    public void setNormalColor(int color) {
        if (color != 0) {
            normalColor = ContextCompat.getColor(getContext(), color);
            if (!isSelected) {
                mPaint.setColor(normalColor);
                invalidate();
            }
        }
    }

    public void setTextSize(int size) {
        mPaint.setTextSize(size);
        invalidate();
    }


    public void setDrawableWidth(int width) {
        drawableWidth = width;
        invalidate();
    }

    public void setDrawableHeight(int height) {
        drawableHeight = height;
        invalidate();
    }

    public void setDrawablePadding(int padding) {
        drawablePadding = padding;
        invalidate();
    }

    public void hiddenDrawable() {
        onlyText = true;
        onlyDrawable = false;
        invalidate();
    }

    public void hiddenText() {
        onlyText = false;
        onlyDrawable = true;
        invalidate();
    }

    public void showDrawable() {
        onlyText = false;
        invalidate();
    }

    public void showText() {
        onlyDrawable = false;
        invalidate();
    }

    @Override
    public void setSelected(boolean selected) {
        isSelected = selected;
        invalidate();
    }
}
