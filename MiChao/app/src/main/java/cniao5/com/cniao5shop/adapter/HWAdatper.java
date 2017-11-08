package cniao5.com.cniao5shop.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cniao5.com.cniao5shop.R;
import cniao5.com.cniao5shop.bean.Wares;

/**
 * Created by <a href="http://www.cniao5.com">菜鸟窝</a>
 * 一个专业的Android开发在线教育平台
 */
public class HWAdatper extends SimpleAdapter<Wares> {


    public HWAdatper(Context context, List<Wares> datas) {
        super(context, R.layout.template_hot_wares, datas);
        Log.d("HWA", "HWA");
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Wares wares) {
        Log.d("HWA", "convert");
        SimpleDraweeView draweeView = (SimpleDraweeView) viewHolder.getView(R.id.drawee_view1);
        draweeView.setImageURI(Uri.parse(wares.getImgUrl()));

        viewHolder.getTextView(R.id.text_title).setText(wares.getName());
//                        TextView textView = (TextView) viewHolder.getView(R.id.text_title);
    }
}
