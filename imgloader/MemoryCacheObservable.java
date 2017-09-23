package com.example.android.myrxjava.imgloader;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Created by LuYu on 2017/9/14.
 */
public class MemoryCacheObservable extends CacheObservable {

    int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);//KB
    int cacheSize = maxMemory/8;

    //什么意思？
    LruCache<String,Bitmap> mLruCache = new LruCache<String,Bitmap>(cacheSize){
        @Override
        protected int sizeOf(String key, Bitmap value) {
//            return super.sizeOf(key, value);
            return  value.getRowBytes() * value.getHeight() /1024;
        }
    };

    @Override
    public Image getDataFromCache(String url) {
        Log.d("MemoryCacheObservable","getDataFromCache");
        Bitmap bitmap = mLruCache.get(url);
        if (bitmap!=null){
            return new Image(url,bitmap);
        }

        return null;
    }

    @Override
    public void putDataIntoCache(Image image) {
        Log.d("MemoryCacheObservable","putDataIntoCache");
        mLruCache.put(image.getUrl(),image.getBitmap());
    }
}
