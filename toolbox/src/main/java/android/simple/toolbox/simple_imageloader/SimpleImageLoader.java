package android.simple.toolbox.simple_imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by Sikang on 2017/3/22.
 */

public class SimpleImageLoader {
    private static SimpleImageLoader mImageLoader;
    ImageCache imageCache;

    /**
     * 构造方法私有化
     */
    private SimpleImageLoader() {

    }

    public void init(Context context) {
        imageCache = new ImageCache(context);
    }


    public boolean isInitialize() {
        return imageCache != null;
    }


    /**
     * 获取单例
     */
    public static SimpleImageLoader getInstance() {
        if (mImageLoader == null)
            synchronized (SimpleImageLoader.class) {
                if (mImageLoader == null)
                    mImageLoader = new SimpleImageLoader();
            }
        return mImageLoader;
    }

//    /**
//     * @param url      图片地址
//     * @param position 图片序号
//     * @param width    指定宽度 0表示不指定
//     * @param height   指定高度
//     */
//    public void loadBitmap(String url, final int width, final int height, final int position, final SimpleCallBack<Bitmap> callBack) {
//        //先查看缓存中有没有图片
//        Bitmap bitmap = imageCache.getBitmapFromMemoryCache(imageCache.hashKeyForDisk(url));
//        if (bitmap != null)
//            callBack.action(bitmap, position);
//        else {
//            //RXjava 异步加载图片)(
//            Observable.just(url)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.io())
//                    .map(new Func1<String, Bitmap>() {
//                        @Override
//                        public Bitmap call(String imageUrl) {
//                            //在SD卡中查找有没有图片，没有则重新下载
//                            return imageCache.loadImage(imageUrl, width, height);
//                        }
//                    })
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Action1<Bitmap>() {
//                        @Override
//                        public void call(Bitmap bitmap) {
//                            if (bitmap != null)
//                                callBack.action(bitmap, position);
//                        }
//                    });
//        }
//    }


    /**
     * 加载图片（填充ImageView）
     */
    public void loadFill(final ImageView view, String url) {
        //先查看缓存中有没有图片
        Bitmap bitmap = imageCache.getBitmapFromMemoryCache(imageCache.hashKeyForDisk(url));
        if (bitmap != null)
            view.setImageBitmap(bitmap);
        else {
            new ImageloadTask(view, false).execute(url);
        }
    }

    /**
     * 加载图片(保持比例)
     */
    public void loadScale(final ImageView view, String url) {
        //先查看缓存中有没有图片
        Bitmap bitmap = imageCache.getBitmapFromMemoryCache(imageCache.hashKeyForDisk(url));
        if (bitmap != null)
            view.setImageBitmap(bitmap);
        else {
            new ImageloadTask(view, true).execute(url);
        }
    }


    class ImageloadTask extends AsyncTask<String, Integer, Bitmap> {
        ImageView imageView;
        boolean isKeepScale;

        float width = 0;
        float height = 0;

        ImageloadTask(ImageView imageView, boolean isKeepScale) {
            this.imageView = imageView;
            this.isKeepScale = isKeepScale;
            width = imageView.getMeasuredWidth();
            height = imageView.getMeasuredWidth();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            //在SD卡中查找有没有图片，没有则重新下载
            return imageCache.loadImage(params[0], width, height, isKeepScale);
        }

        @Override
        protected void onCancelled() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
        }
    }

}
