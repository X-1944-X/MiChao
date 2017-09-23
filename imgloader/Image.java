package com.example.android.myrxjava.imgloader;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by LuYu on 2017/9/14.
 */
public class Image {
    private String url;
    private Bitmap bitmap;

    public Image(String url, Bitmap bitmap) {
        Log.d("Image","public Image");
        this.url = url;
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
