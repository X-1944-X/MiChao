package com.example.android.myrxjava.imgloader;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by LuYu on 2017/9/14.
 */
public abstract class CacheObservable {

    public Observable<Image> getImage(final String url){
        Log.d("CacheObservable","getImage");
        return Observable.create(new ObservableOnSubscribe<Image>() {
            @Override
            public void subscribe(ObservableEmitter<Image> e) throws Exception {
//                if (!e.isDisposed()){
                Log.d("CacheObservable","Image image = getDataFromCache");
                    Image image = getDataFromCache(url);
                if (image != null){
                    Log.d("CacheObservable","image != null");
                    e.onNext(image);
                    e.onComplete();
                }
                else
                    Log.d("CacheObservable","image = null");
                    e.onComplete();

//                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public abstract Image getDataFromCache(String url);

    public abstract void putDataIntoCache(Image image);
}
