package android.simple.toolbox.simple_imageloader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.simple.toolbox.utils.SimpleUtils;
import android.util.LruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Sikang on 2017/3/24.
 * 图片缓存管理
 */

public class ImageCache {
    /**
     * 图片在内存中的缓存池，在内存不够时会将近期使用最少的图片回收
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * 图片硬盘缓存类
     */
    private DiskLruCache mDiskLruCache;


    public ImageCache(Context context, String diskCachePath) {
        //初始化
        initLruCache();
        initDiskCache(context, diskCachePath);
    }

    public ImageCache(Context context) {
        //初始化
        initLruCache();
        initDiskCache(context, getDefaultDiskPath());
    }

    private String getDefaultDiskPath() {
        return Environment.getExternalStorageDirectory().toString() + File.separator + "toolbox" + File.separator + "cache" + File.separator;
    }

    /**
     * 加载图片
     *
     * @param imageUrl    图片地址
     * @param width       需要图片宽度
     * @param height      需要图片高度
     * @param isKeepScale 是否保持图片比例
     */
    public Bitmap loadImage(String imageUrl, float width, float height, boolean isKeepScale) {
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        DiskLruCache.Snapshot snapShot = null;
        try {
            String key = hashKeyForDisk(imageUrl);
            // 查找key对应的缓存
            snapShot = mDiskLruCache.get(key);
            if (snapShot == null) {
                // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (downloadUrlToStream(imageUrl, outputStream)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
                // 缓存被写入后，再次查找key对应的缓存
                snapShot = mDiskLruCache.get(key);
            }
            if (snapShot != null) {
                fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                fileDescriptor = fileInputStream.getFD();
            }
            // 将缓存数据解析成Bitmap对象
            Bitmap bitmap = null;
            if (fileDescriptor != null) {
                bitmap = SimpleUtils.getBitmapThumbnail(fileDescriptor, width, height, isKeepScale);
            }
            if (bitmap != null) {
                //如果制指定了图片大小
//                if (isFirstLoad && width > 0 && height > 0) {
//                    bitmap = resizeImage(bitmap, width, height);
//                    //重新添加到Disk
//                    addBitmapToDiskCache(key, bitmap);
//                }
                // 将Bitmap对象添加到内存缓存当中
                addBitmapToMemoryCache(key, bitmap);

            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileDescriptor == null && fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }



    /**
     * 建立HTTP请求，并获取Bitmap对象。
     *
     * @param urlString 图片的URL地址
     * @return 解析后的Bitmap对象
     */
    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key    LruCache的键，这里传入图片的URL地址。
     * @param bitmap LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }


    /**
     * 初始化内存缓存
     */
    public void initLruCache() {
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    private String cachePath;

    /**
     * 初始化硬盘缓存
     */
    public void initDiskCache(Context context, String diskCachePath) {
        try {
            // 获取图片硬盘缓存路径
            File cacheDir = getDiskCacheDir(context, diskCachePath);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            // 创建DiskLruCache实例，初始化缓存数据
            mDiskLruCache = DiskLruCache
                    .open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据传入的uniqueName获取硬盘缓存的路径地址。
     */
    private File getDiskCacheDir(Context context, String uniqueName) {
        String path;
        //SD卡存在或SD卡为内置不可被移除的时候
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //获取缓存目录（/sdcard/Android/data/<application package>/cache）
            path = context.getExternalCacheDir().getPath();
        } else {
            //获取应用程序缓存目录，当设备内存不足时，可能会被删除（ /data/data/<application package>/cache）
            path = context.getCacheDir().getPath();
        }
        cachePath = path + File.separator + uniqueName;
        return new File(cachePath);
    }

    /**
     * 获取当前应用程序的版本号。
     * 每当版本号改变，缓存路径下存储的所有数据都会被清除掉，DiskLruCache认为当应用程序有版本更新的时候，所有的数据都应该从网上重新获取
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }


    /**
     * 使用MD5算法对传入的key进行加密并返回。
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
