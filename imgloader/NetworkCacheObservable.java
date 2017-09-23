package com.example.android.myrxjava.imgloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by LuYu on 2017/9/14.
 */
public class NetworkCacheObservable extends CacheObservable {
    @Override
    public Image getDataFromCache(String url) {
        Log.d("NetworkCacheObservable","getDataFromCache-downloadImage");
        Bitmap bitmap = downloadImage(url);
        if (bitmap != null)
        return new Image(url,bitmap);
        else
            return  null;
    }

    @Override
    public void putDataIntoCache(Image image) {

    }

    private Bitmap downloadImage(String url){
        Log.d("NetworkCacheObservable","downloadImage");
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            //为什么要弄成final ?
            final URLConnection con = new URL(url).openConnection();
            inputStream = con.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        Bitmap bitmap = BitmapFactory.decodeStream();
        return bitmap;
    }
}
