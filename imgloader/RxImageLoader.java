package com.example.android.myrxjava.imgloader;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by LuYu on 2017/9/14.
 */
public class RxImageLoader {
    static  RxImageLoader singleton;
    private String imgurl;
    private RequestCreator mRequestCreater;

    private RxImageLoader(Builder builder) {
        //RequestCreator 初始化
        Log.d("RxImageLoader","RequestCreator 初始化");
        mRequestCreater = new RequestCreator(builder.mContext);
        Log.d("RxImageLoader","RequestCreator 初始化完毕");
    }

//    RxImageLoader rxImageLoader = new RxImageLoader();
//    RxImageLoader.with()

    public static RxImageLoader with(Context context){
        Log.d("RxImageLoader","with(context)");
        if (singleton == null){
            Log.d("RxImageLoader","RIL singleton is null");
            synchronized (RxImageLoader.class){
                if (singleton == null){
                    Log.d("RxImageLoader","after sync ,singleton is null");
                    singleton = new Builder(context).build();
                    Log.d("RxImageLoader",singleton.toString());
                }
            }
        }
        Log.d("RxImageLoader","return singleton");
        return singleton;
    }

    public RxImageLoader load(String url){
        Log.d("RxImageLoader","load()"+url);
        this.imgurl = url;
        Log.d("RxImageLoader",singleton.toString());
        return singleton;
    }

    public void into(final ImageView imageView){
        Log.d("RxImageLoader","into()");
//        Observable.concat(memoryObservable, diskObservable, networkObservable)
//        Observable.just(new Image())
        //concat 如何实现
        Observable.concat(mRequestCreater.getImageFromMemory(imgurl),
                mRequestCreater.getImageFromDisk(imgurl),
                mRequestCreater.getImageFromNetwork(imgurl))
        .filter(new Predicate<Image>(){

            @Override
            public boolean test(Image image) throws Exception {
                Log.d("RxImageLoader",image.toString());
                return image!=null;
            }
        })
                .firstElement()
                .subscribe(new Consumer<Image>() {

                    @Override
                    public void accept(Image image) throws Exception {
                        Log.d("RxImageLoader","before setImageBitmap");
                        imageView.setImageBitmap(image.getBitmap());
                        Log.d("RxImageLoader", "get it");
                    }
                });
    }

    public static class Builder{
        private Context mContext;
        public Builder(Context context){
            Log.d("RxImageLoader","Builder()");
            this.mContext = context;
        }

        public RxImageLoader build(){
            Log.d("RxImageLoader","build()");
            return new RxImageLoader(this);
        }
    }
}
