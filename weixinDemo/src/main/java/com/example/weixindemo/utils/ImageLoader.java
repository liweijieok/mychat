package com.example.weixindemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ImageLoader {
    /**
     * 默认线程池大小
     */
    private static final int DEFAULT_THREEPOOL_COUNT = 1;
    private static ImageLoader mInstance;
    /**
     * 内存缓存类 核心对象
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreePool;
    /**
     * 任务类型
     */
    private Type type = Type.LIFO;
    /**
     * 同步线程池的handler初始化问题
     */
    private Semaphore mPoolThreadHandlerSemaphore = new Semaphore(0);
    private Semaphore mSemaphoreThreadPool;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQUeue;

    /**
     * 轮询线程
     */
    private Thread mPoolThread;
    /**
     * 发送轮询消息
     */
    private Handler mPoolThreadHandler;
    /**
     * 更新UI
     */
    private Handler UIHandler;

    private ImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (null == mInstance) {
                    getInstance(DEFAULT_THREEPOOL_COUNT, Type.FIFO);
                }
            }
        }
        return mInstance;
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static ImageLoader getInstance(int count, Type type) {
        if (null == mInstance) {
            synchronized (ImageLoader.class) {
                if (null == mInstance) {
                    mInstance = new ImageLoader(count, type);
                }
            }
        }
        return mInstance;
    }

    /**
     * 利用反射
     *
     * @param obj
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldVaule(Object obj, String fieldName) {
        int value = 0;
        try {
            Field filed = ImageView.class.getField(fieldName);
            int int1 = filed.getInt(obj);
            if (int1 > 0 && int1 < Integer.MAX_VALUE) {
                value = int1;
            }
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 初始化
     *
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {
        // TODO Auto-generated method stub

        mPoolThread = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Looper.prepare();

                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        // TODO Auto-generated method stub
                        super.handleMessage(msg);
                        // 线程池取出任务进行
                        mThreePool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                };
                mPoolThreadHandlerSemaphore.release();
                Looper.loop();
                super.run();
            }
        };

        mPoolThread.start();

        int maxMenory = (int) Runtime.getRuntime().maxMemory();
        int cacheMenory = maxMenory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMenory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // TODO Auto-generated method stub
                return value.getRowBytes() * value.getHeight();
            }
        };
        mThreePool = Executors.newFixedThreadPool(threadCount);
        mTaskQUeue = new LinkedList<Runnable>();
        this.type = type;
        mSemaphoreThreadPool = new Semaphore(threadCount);
    }

    /**
     * 取出任务
     *
     * @return
     */
    private Runnable getTask() {
        // TODO Auto-generated method stub
        if (type == Type.FIFO) {
            return mTaskQUeue.removeFirst();
        } else {
            return mTaskQUeue.removeLast();
        }
    }

    /**
     * 下载图片
     *
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView) {
        imageView.setTag(path);
        if (UIHandler == null) {
            UIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    ImageHolder holder = (ImageHolder) msg.obj;
                    if (holder.path.equals(holder.imageview.getTag())) {
                        holder.imageview.setImageBitmap(holder.bm);
                    }

                }
            };
        }
        Bitmap bm = getBitmapFromLruCache(path);
        Log.i("TAG", path + "=path");
        if (bm != null) {
            refreshBitmap(path, imageView, bm);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    // 加载图片，图片压缩，
                    ImageSize imgeViewSize = getImgeViewSize(imageView);
                    Bitmap bm = decodeSampleBitmapFromPath(path, imgeViewSize.width, imgeViewSize.heigh);
                    if (bm != null) {
                        addBitmapToCache(path, bm);
                        refreshBitmap(path, imageView, bm);
                    }
                    mSemaphoreThreadPool.release();
                }
            });
        }
    }

    /**
     * 发送消息，刷新界面
     *
     * @param path
     * @param imageView
     * @param bm
     */
    private void refreshBitmap(String path, final ImageView imageView, Bitmap bm) {
        Message msg = Message.obtain();
        ImageHolder holder = new ImageHolder();
        holder.bm = bm;
        holder.imageview = imageView;
        holder.path = path;
        msg.obj = holder;
        UIHandler.sendMessage(msg);
    }

    /**
     * 添加内存缓存
     *
     * @param path
     * @param bm
     */
    protected void addBitmapToCache(String path, Bitmap bm) {
        // TODO Auto-generated method stub
        if (getBitmapFromLruCache(path) == null) {
            mLruCache.put(path, bm);
        }

    }

    /**
     * 根据图片显示高和宽进行压缩
     *
     * @param path
     * @param width
     * @param heigh
     * @return
     */
    protected Bitmap decodeSampleBitmapFromPath(String path, int width, int heigh) {
        // TODO Auto-generated method stub
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = caculateInSampleSize(options, width, heigh);
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        return bm;
    }

    /**
     * 计算压缩倍数
     *
     * @param options
     * @param reWidth
     * @param reHeigh
     * @return
     */
    private int caculateInSampleSize(Options options, int reWidth, int reHeigh) {
        // TODO Auto-generated method stub
        int width = options.outWidth;
        int heigh = options.outHeight;
        int sampleSize = 1;
        if (width > reWidth || heigh > reHeigh) {
            int widthRadio = Math.round(width * 1.0f / reWidth);
            int heighRadio = Math.round(heigh * 1.0f / reHeigh);
            sampleSize = Math.max(widthRadio, heighRadio);
        }
        return sampleSize;
    }

    /**
     * 获取需要显示的图片大小
     *
     * @param imageView
     * @return
     */
    protected ImageSize getImgeViewSize(ImageView imageView) {
        // TODO Auto-generated method stub
        ImageSize imageSize = new ImageSize();
        LayoutParams layoutParams = imageView.getLayoutParams();
        int width;
        if ((width = imageView.getWidth()) <= 0) {
            width = layoutParams.width;
        }
        if (width <= 0) {
            // width = imageView.getMaxWidth();
            width = getImageViewFieldVaule(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
            width = displayMetrics.widthPixels;
        }
        imageSize.width = width;
        int hight;
        if ((hight = imageView.getHeight()) <= 0) {
            hight = layoutParams.height;
        }
        if (hight <= 0) {
            // hight = imageView.getMaxHeight();
            hight = getImageViewFieldVaule(imageView, "mMaxHeight");
        }
        if (hight <= 0) {
            DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();

            hight = displayMetrics.heightPixels;
        }
        imageSize.heigh = hight;
        return imageSize;
    }

    /**
     * 增加新任务 多线程同步
     *
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        // TODO Auto-generated method stub
        mTaskQUeue.add(runnable);
        try {
            if (mPoolThreadHandler == null)
                mPoolThreadHandlerSemaphore.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 获取缓存
     *
     * @param path
     * @return
     */
    private Bitmap getBitmapFromLruCache(String path) {
        // TODO Auto-generated method stub
        return mLruCache.get(path);
    }

    /**
     * 任务类型
     *
     * @author liweijie
     */
    public enum Type {
        LIFO, FIFO;
    }

    private class ImageSize {
        int width;
        int heigh;
    }

    /**
     * 发送消息体
     *
     * @author liweijie
     */
    private class ImageHolder {
        String path;
        Bitmap bm;
        ImageView imageview;
    }
}
