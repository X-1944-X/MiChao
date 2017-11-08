package cniao5.com.cniao5shop;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebViewClient;

public class WebView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent myintent = getIntent();
        String url = myintent.getStringExtra("Url_Data");
        android.webkit.WebView webView = (android.webkit.WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

}
