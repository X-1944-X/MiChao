package cniao5.com.cniao5shop.adapter;

import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by <a href="http://www.cniao5.com">菜鸟窝</a>
 * 一个专业的Android开发在线教育平台
 */
public abstract class SimpleAdapter<T> extends BaseAdapter<T,BaseViewHolder> {

    public SimpleAdapter(Context context, int layoutResId) {


        super(context, layoutResId);
        Log.d("SA", "2");
    }

    public SimpleAdapter(Context context, int layoutResId, List<T> datas) {
        super(context, layoutResId, datas);
        Log.d("SA", "3");
    }


}
