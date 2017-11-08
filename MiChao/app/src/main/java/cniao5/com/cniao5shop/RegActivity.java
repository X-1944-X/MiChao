package cniao5.com.cniao5shop;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.mob.MobSDK;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.smssdk.SMSSDK;
import cn.smssdk.EventHandler;
import cn.smssdk.gui.IdentifyNumPage;
import cn.smssdk.gui.RegisterPage;
import cn.smssdk.gui.SmartVerifyPage;
import cn.smssdk.utils.SMSLog;
import cniao5.com.cniao5shop.utils.ManifestUtil;
import cniao5.com.cniao5shop.utils.ToastUtils;
import cniao5.com.cniao5shop.widget.CNiaoToolBar;
import cniao5.com.cniao5shop.widget.ClearEditText;
import dmax.dialog.SpotsDialog;

public class RegActivity extends Activity {
    // 默认使用中国区号
    private static final String DEFAULT_COUNTRY_ID = "42";

    @ViewInject(R.id.txtCountry)
    private TextView mTxtCountry;

    @ViewInject(R.id.txtCountryCode)
    private TextView mTxtCountryCode;

    @ViewInject(R.id.edittxt_phone)
    private ClearEditText mEtxtPhone;


    @ViewInject(R.id.edittxt_pwd)
    private ClearEditText mEtxtPwd;

    private SpotsDialog dialog;


    private  SMSEvenHanlder evenHanlder;

    @ViewInject(R.id.toolbar)
    private CNiaoToolBar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        initToolBar();

        evenHanlder=new SMSEvenHanlder();
        SMSSDK.registerEventHandler(evenHanlder);

        String[] country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        if (country != null) {

            mTxtCountryCode.setText("+"+country[1]);

            mTxtCountry.setText(country[0]);

        }
//        RegisterPage registerPage = new RegisterPage();
//        registerPage.setRegisterCallback(new EventHandler(){
//            public void afterEvent(int event,int result,Object data){
//                if (result == SMSSDK.RESULT_COMPLETE){
//                    @SuppressWarnings("unchecked")
//                    HashMap<String,Object> phoneMap = (HashMap<String,Object>) data;
//                    String country = (String)phoneMap.get("country");
//                    String phone   = (String) phoneMap.get("phone");
//
//                    Log.d(country,phone);
//                }
//            }
//        });
//        registerPage.show(this);

    }

    class SMSEvenHanlder extends EventHandler{
        @Override
        public void afterEvent(final int event,final int result,final Object data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {


                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            // 请求验证码后，跳转到验证码填写页面

                            afterVerificationCodeRequested((Boolean) data);

                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                        }
                    } else {

                        // 根据服务器返回的网络错误，给toast提示
                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                ToastUtils.show(RegActivity.this, des);
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                        }

                    }
                }
            });
        }
    }

    private void initToolBar(){


        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SMSSDK.getVerificationCode();

               getCode();

            }
        });
    }

    private  void getCode(){
        String phone = mEtxtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String code = mTxtCountryCode.getText().toString().trim();
        String pwd = mEtxtPwd.getText().toString().trim();

        SMSSDK.getVerificationCode(code, phone);
    }

    /** 请求验证码后，跳转到验证码填写页面 */
    private void afterVerificationCodeRequested(boolean smart) {



        String phone = mEtxtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String code = mTxtCountryCode.getText().toString().trim();
        String pwd = mEtxtPwd.getText().toString().trim();

        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        Intent intent = new Intent(this,RegSecondActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("pwd",pwd);
        intent.putExtra("countryCode",code);

//        startActivity(intent);
    }

    private String[] getCurrentCountry() {
        String mcc = this.getMCC();
        String[] country = null;
        if(!TextUtils.isEmpty(mcc)) {
            country = SMSSDK.getCountryByMCC(mcc);
        }

        if(country == null) {
            SMSLog.getInstance().d("no country found by MCC: " + mcc, new Object[0]);
            country = SMSSDK.getCountry("42");
        }

        return country;
    }

    private String getMCC() {
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tm.getNetworkOperator();
        return !TextUtils.isEmpty(networkOperator)?networkOperator:tm.getSimOperator();
    }

protected void onDestroy(){
    super.onDestroy();
    SMSSDK.unregisterEventHandler(evenHanlder);
}

}
