package cniao5.com.cniao5shop.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.util.List;

import cniao5.com.cniao5shop.Contants;
import cniao5.com.cniao5shop.R;
import cniao5.com.cniao5shop.adapter.BaseAdapter;
import cniao5.com.cniao5shop.adapter.BaseViewHolder;
import cniao5.com.cniao5shop.adapter.DividerItemDecoration;
import cniao5.com.cniao5shop.adapter.HWAdatper;
import cniao5.com.cniao5shop.adapter.HotWaresAdapter;
import cniao5.com.cniao5shop.adapter.SimpleAdapter;
import cniao5.com.cniao5shop.bean.Page;
import cniao5.com.cniao5shop.bean.Wares;
import cniao5.com.cniao5shop.http.OkHttpHelper;
import cniao5.com.cniao5shop.http.SpotsCallBack;


public class HotFragment extends Fragment {



    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    private int currPage=1;
    private int totalPage=1;
    private int pageSize=10;

    private List<Wares> datas;
    private HotWaresAdapter sedAdatper;
    private HWAdatper mAdatper;

    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.refresh_view)
    private MaterialRefreshLayout mRefreshLaout;



    private  static final int STATE_NORMAL=0;
    private  static final int STATE_REFREH=1;
    private  static final int STATE_MORE=2;

    private int state=STATE_NORMAL;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_hot,container,false);

        ViewUtils.inject(this,view);


        initRefreshLayout();
        getData();

        return view ;

    }



    private  void initRefreshLayout(){

        mRefreshLaout.setLoadMore(true);
        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                refreshData();

            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if(currPage <=totalPage)
                    loadMoreData();
                else{
//                    Toast.makeText()
                    mRefreshLaout.finishRefreshLoadMore();
                }
            }
        });
    }




    private  void refreshData(){

        currPage =1;

        state=STATE_REFREH;
        getData();

    }

    private void loadMoreData(){

        currPage = ++currPage;
        state = STATE_MORE;

        getData();

    }


    private void getData(){



        String url = Contants.API.WARES_HOT+"?curPage="+currPage+"&pageSize="+pageSize;
        httpHelper.get(url, new SpotsCallBack<Page<Wares>>(getContext()) {


            @Override
            public void onSuccess(Response response, Page<Wares> waresPage) {


                Log.d("TEST","onSuccess()");
                datas = waresPage.getList();
                currPage = waresPage.getCurrentPage();
                totalPage =waresPage.getTotalPage();

                showData();
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });


    }




    private  void showData(){



        switch (state){

            case  STATE_NORMAL:
                mAdatper = new HWAdatper(getContext(),datas);
//                mAdatper.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
//                    @Override
//                    public void OnClick(View view, int position) {
//                    }
//                });

                mAdatper.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("HF","onclick");
                    }
                });
                mRecyclerView.setAdapter(mAdatper);
//                sedAdatper = new HotWaresAdapter(datas);
//                mRecyclerView.setAdapter(sedAdatper);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(),DividerItemDecoration.VERTICAL_LIST));
//                mRecyclerView.setAdapter(new SimpleAdapter<Wares>(getContext(),datas,R.layout.template_hot_wares) {
//
//                    @Override
//                    public void bindData(BaseViewHolder viewHolder, Wares wares) {
//                        SimpleDraweeView draweeView = (SimpleDraweeView) viewHolder.getView(R.id.drawee_view);
//                        draweeView.setImageURI(Uri.parse(wares.getImgUrl()));
//
//                        viewHolder.getTextView(R.id.text_title).setText(wares.getName());
//                    }
//                });
                break;

            case STATE_REFREH:
                mAdatper.clear();
                mAdatper.addData(datas);

                mRecyclerView.scrollToPosition(0);
                mRefreshLaout.finishRefresh();
                break;

            case STATE_MORE:
                mAdatper.addData(mAdatper.getDatas().size(),datas);
                mRecyclerView.scrollToPosition(mAdatper.getDatas().size());
                mRefreshLaout.finishRefreshLoadMore();
                break;





        }



    }




}
