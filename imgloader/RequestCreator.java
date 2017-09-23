package com.example.android.myrxjava.imgloader;

import android.content.Context;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by LuYu on 2017/9/14.
 */
public class RequestCreator {

    private MemoryCacheObservable memoryCacheObservable;
    private DiskCacheObservable diskCacheObservable;
    private NetworkCacheObservable networkCacheObservable;

    public RequestCreator(Context context){
        memoryCacheObservable = new MemoryCacheObservable();
        diskCacheObservable = new DiskCacheObservable(context);
        networkCacheObservable = new NetworkCacheObservable();
    }

    public Observable<Image> getImageFromMemory(String url){
        Log.d("RequestCreator", "getImageFromMemory");
        return memoryCacheObservable.getImage(url)
                .filter(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) throws Exception {
                        Log.d("RequestCreator","after memoryCacheObservable.getImage(url)");
                        return image != null;
                    }
                })
                .doOnNext(new Consumer<Image>() {
                    @Override
                    public void accept(Image image) throws Exception {
                        Log.d("RequestCreator","get it from memory");
                    }
                });
    }

    public Observable<Image> getImageFromDisk(String url){
        Log.d("RequestCreator", "getImageFromDisk");
        return diskCacheObservable.getImage(url)
                .filter(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) throws Exception {
                        Log.d("RequestCreator","after diskCacheObservable.getImage(url)");
                        return image!=null;
                    }
                })
                .doOnNext(new Consumer<Image>() {
                    @Override
                    public void accept(Image image) throws Exception {
                        Log.d("RequestCreator","get it from Disk");
                        memoryCacheObservable.putDataIntoCache(image);
                    }
                });
    }

    public Observable<Image> getImageFromNetwork(String url){
        Log.d("RequestCreator", "getImageFromNetWork");
        return networkCacheObservable.getImage(url)
                .filter(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) throws Exception {
                        Log.d("RequestCreator","after neteorkCacheObservable.getImage(url)");
                        return (image!=null);
                    }
                })
                .doOnNext(new Consumer<Image>() {
                    @Override
                    public void accept(Image image) throws Exception {
                        Log.d("RequestCreator","get it from network");
                        diskCacheObservable.putDataIntoCache(image);
                        Log.d("RequestCreator", "after diskCacheObservable.putDataIntoCache(image);");
                        memoryCacheObservable.putDataIntoCache(image);
                        Log.d("RequestCreator", "after memoryCacheObservable.putDataIntoCache(image);");
                    }
                });
    }
}
