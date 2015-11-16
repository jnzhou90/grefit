package grefitcom.grefit;

import android.app.Activity;
import android.os.Bundle;

import android.webkit.WebSettings;
import android.webkit.WebView;


public class GhsWebActivity extends Activity {

    private WebView webViewGhs;
    private  WebSettings webSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghs_web);

        webViewGhs = (WebView)findViewById(R.id.webViewGhs);
        webViewGhs.loadUrl("http://app.chinaopen.com.cn/ghs/");
        webSettings = webViewGhs.getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);//自适应屏幕
        webSettings.setJavaScriptEnabled(true);//启用JS脚本
        webSettings.setSupportZoom(true); //支持缩放
        webSettings.setBuiltInZoomControls(true);//启用内置缩放装置
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局




    }


}
