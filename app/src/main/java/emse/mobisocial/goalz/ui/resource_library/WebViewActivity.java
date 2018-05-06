package emse.mobisocial.goalz.ui.resource_library;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import emse.mobisocial.goalz.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(getIntent().getStringExtra("url"));

        /*webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });*/


        //webView.loadUrl(
        //        "http://egy-tech-droid.blogspot.com.eg/search/label/%D8%AA%D8%B7%D8%A8%D9%8A%D9%82%D8%A7%D8%AA%20%D8%AD%D8%B5%D8%B1%D9%8A%D8%A9");
    }
}
