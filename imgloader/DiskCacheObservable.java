package com.example.android.myrxjava.imgloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by LuYu on 2017/9/14.
 */
public class DiskCacheObservable extends CacheObservable {
    private DiskLruCache mDiskLruCache;
    private Context mContext;
    private int maxSize = 20*1024*1024;

    public DiskCacheObservable(Context context){
        Log.d("DiskCacheObservable","public DiskCacheObservable");
        this.mContext = context;
        Log.d("DiskCacheObservable","public DiskCacheObservable-initDiskLruCache()");
        initDiskLruCache();
    }
    @Override
    public Image getDataFromCache(String url) {
        Log.d("DiskCacheObservable","getDataFromCache-getDataFromDiskLruCache");
//      mDiskLruCache = DiskLruCache.open();
//        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(url);
//        BitmapFactory.decodeStream(snapshot.getInputStream(0));
        Bitmap bitmap = getDataFromDiskLruCache(url);
        if (bitmap!=null) {
            return new Image(url, bitmap);
        }
        return null;
    }

    @Override
    public void putDataIntoCache(final Image image) {
        Observable.create(new ObservableOnSubscribe<Image>() {
            @Override
            public void subscribe(ObservableEmitter<Image> e) throws Exception {
                Log.d("DiskCacheObservable","putDataIntoCache-putDataToDiskLruCache");
                putDataToDiskLruCache(image);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
        //subscribe()进行订阅
    }

    /**
     * 初始化DiskLrucache
     */
    private void initDiskLruCache(){
        Log.d("DiskCacheObservable","initDiskLruCache()");
        try {
            File cacheDir=DiskCacheUtil.getDiskCacheDir(this.mContext,"imge_cache");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            int versionCode=DiskCacheUtil.getAppVersionCode(mContext);
            mDiskLruCache=DiskLruCache.open(cacheDir, versionCode, 1, maxSize);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * DiskLrucache将url转换成Bitmap
     */
    private Bitmap getDataFromDiskLruCache(String url){
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        DiskLruCache.Snapshot snapshot =null;
        //此处为何要final
        final String key = DiskCacheUtil.getMd5String(url);
        try {
            snapshot = mDiskLruCache.get(key);
            if (snapshot!=null){
                fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                //fileInputStream.getFD()的作用是什么？
                // 通过FileInputStream拿到FileDescriptor
                //fileDescriptor是文件描述符
                fileDescriptor = fileInputStream.getFD();
            }

            Bitmap bitmap = null;
            if (fileDescriptor != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fileDescriptor == null && fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private void putDataToDiskLruCache(Image img){
        Log.d("DiskCacheObservable","putDataToDiskLruCache(Image img)");
        try {
            //第一步:获取将要缓存的图片的对应唯一key值.
            String key = DiskCacheUtil.getMd5String(img.getUrl());
            //第二步:获取DiskLruCache的Editor
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);

            if (editor!=null) {
                //第三步:从Editor中获取OutputStream
                OutputStream outputStream=editor.newOutputStream(0);
                //第四步:下载网络图片且保存至DiskLruCache图片缓存中

                boolean isSuccessfull=download(img.getUrl(), outputStream);

                if (isSuccessfull) {
                    editor.commit();
                }else{
                    editor.abort();
                }
                mDiskLruCache.flush();
            }
        } catch (Exception e) {

        }
    }


    private boolean  download(String urlString, OutputStream outputStream){
        Log.d("DiskCacheObservable","download(String urlString, OutputStream outputStream)");
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

        } catch (final IOException e) {
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

}
