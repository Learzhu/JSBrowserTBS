package cn.lzzykj.jsbrowser.tbs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;
import com.tencent.smtt.utils.TbsLogClient;

import java.net.URL;

import cn.lzzykj.jsbrowser.tbs.utils.X5WebView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /**
     * 作为一个浏览器的示例展示出来，采用android+web的模式!
     */
    private X5WebView mWebView;
    //webView的容器
    private ViewGroup mViewParent;
    private static final int MAX_LENGTH = 14;

    private ValueCallback<Uri> uploadFile;

    private URL mIntentUrl;

    private static final String mHomeUrl = "http://app.html5.qq.com/navi/index";
    private boolean mNeedTestPage = false;
    private Button mGo;
    private EditText mUrl;
    private ImageButton mRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);
        mViewParent = (ViewGroup) findViewById(R.id.webview_fl);
        initBtnListenser();
        this.webViewTransportTest();

        mTestHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);
        intiTBS();
    }

    private void intiTBS() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                Log.e("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub

            }
        };
        QbSdk.setTbsLogClient(new TbsLogClient(getApplicationContext()));
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Log.d("app", "onDownloadFinish is " + i);
            }

            @Override
            public void onInstallFinish(int i) {
                Log.d("app", "onInstallFinish is " + i);
            }

            @Override
            public void onDownloadProgress(int i) {
                Log.d("app", "onDownloadProgress:" + i);
            }
        });

        QbSdk.initX5Environment(this, cb);
    }

    private void initBtnListenser() {
        mGo = (Button) findViewById(R.id.btnGo1);
        mUrl = (EditText) findViewById(R.id.editUrl1);
        mRefresh = (ImageButton) findViewById(R.id.btnRefresh1);


        mGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mUrl.getText().toString();
                mWebView.loadUrl(url);
                mWebView.requestFocus();
            }
        });


        mUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                                          @Override
                                          public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mGo.setVisibility(View.VISIBLE);
//                    mRefresh.setVisibility(View.GONE);
//                    if (null == mWebView.getUrl()) return;
//                    if (mWebView.getUrl().equalsIgnoreCase(mHomeUrl)) {
//                        mUrl.setText("");
//                        mGo.setText("首页");
//                        mGo.setTextColor(0X6F0F0F0F);
//                    } else {
//                        mUrl.setText(mWebView.getUrl());
//                        mGo.setText("进入");
//                        mGo.setTextColor(0X6F0000CD);
//                    }
//                } else {
//                    mGo.setVisibility(View.GONE);
//                    mRefresh.setVisibility(View.VISIBLE);
//                    String title = mWebView.getTitle();
//                    if (title != null && title.length() > MAX_LENGTH)
//                        mUrl.setText(title.subSequence(0, MAX_LENGTH) + "...");
//                    else
//                        mUrl.setText(title);
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
                                          }
                                      }
        );

        mUrl.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                String url = null;
                if (mUrl.getText() != null) {
                    url = mUrl.getText().toString();
                }

                if (url == null
                        || mUrl.getText().toString().equalsIgnoreCase("")) {
                    mGo.setText("请输入网址");
                    mGo.setTextColor(0X6F0F0F0F);
                } else {
                    mGo.setText("进入");
                    mGo.setTextColor(0X6F0000CD);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

        });
    }

    /**
     * 初始化WebView
     */
    private void initWebView() {
        //
        //mWebView = new DemoWebView(this);

        mWebView = new X5WebView(this, null);

        Log.w("grass", "Current SDK_INT:" + Build.VERSION.SDK_INT);

        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));

//        initProgressBar();

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                // TODO Auto-generated method stub

                Log.e("should", "request.getUrl().toString() is " + request.getUrl().toString());

                return super.shouldInterceptRequest(view, request);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                moreMenuClose();
//                // mTestHandler.sendEmptyMessage(MSG_OPEN_TEST_URL);
//                mTestHandler.sendEmptyMessageDelayed(MSG_OPEN_TEST_URL, 5000);// 5s?
//                if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16)
//                    changGoForwardButton(view);
//                /* mWebView.showLog("test Log"); */

            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsConfirm(WebView arg0, String arg1, String arg2, JsResult arg3) {
                return super.onJsConfirm(arg0, arg1, arg2, arg3);
            }

            View myVideoView;
            View myNormalView;
            IX5WebChromeClient.CustomViewCallback callback;

            ///////////////////////////////////////////////////////////
            //

            /**
             * 全屏播放配置
             */
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
                FrameLayout normalView = (FrameLayout) findViewById(R.id.web_filechooser);
                ViewGroup viewGroup = (ViewGroup) normalView.getParent();
                viewGroup.removeView(normalView);
                viewGroup.addView(view);
                myVideoView = view;
                myNormalView = normalView;
                callback = customViewCallback;
            }

            @Override
            public void onHideCustomView() {
                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
                if (myVideoView != null) {
                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                    viewGroup.removeView(myVideoView);
                    viewGroup.addView(myNormalView);
                }
            }

            @Override
            public boolean onShowFileChooser(WebView arg0,
                                             ValueCallback<Uri[]> arg1, FileChooserParams arg2) {
                // TODO Auto-generated method stub
                Log.e("app", "onShowFileChooser");
                return super.onShowFileChooser(arg0, arg1, arg2);
            }

            @Override
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String captureType) {

                MainActivity.this.uploadFile = uploadFile;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "test"), 0);
            }


            @Override
            public boolean onJsAlert(WebView arg0, String arg1, String arg2, JsResult arg3) {
                /**
                 * 这里写入你自定义的window alert
                 */
                // AlertDialog.Builder builder = new Builder(getContext());
                // builder.setTitle("X5内核");
                // builder.setPositiveButton("确定", new
                // DialogInterface.OnClickListener() {
                //
                // @Override
                // public void onClick(DialogInterface dialog, int which) {
                // // TODO Auto-generated method stub
                // dialog.dismiss();
                // }
                // });
                // builder.show();
                // arg3.confirm();
                // return true;
                Log.i("yuanhaizhou", "setX5webview = null");
                return super.onJsAlert(null, "www.baidu.com", "aa", arg3);
            }

            /**
             * 对应js 的通知弹框 ，可以用来实现js 和 android之间的通信
             */


            @Override
            public void onReceivedTitle(WebView arg0, final String arg1) {
                super.onReceivedTitle(arg0, arg1);
                Log.i("yuanhaizhou", "webpage title is " + arg1);

            }
        });

        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2,
                                        String arg3, long arg4) {
                TbsLog.d(TAG, "url: " + arg0);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("�Ƿ�����")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
//                                        Toast.makeText(
//                                                MainActivity.this,
//                                                "fake message: i'll download...",
//                                                1000).show();
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
//                                        Toast.makeText(
//                                                MainActivity.this,
//                                                "fake message: refuse download...",
//                                                1000).show();
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        // TODO Auto-generated method stub
//                                        Toast.makeText(
//                                                MainActivity.this,
//                                                "fake message: refuse download...",
//                                                1000).show();
                                    }
                                }).show();
            }
        });


        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        //webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        //webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        //webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();
        if (mIntentUrl == null) {
            mWebView.loadUrl(mHomeUrl);
        } else {
            mWebView.loadUrl(mIntentUrl.toString());
        }
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    private void webViewTransportTest() {
        X5WebView.setSmallWebViewEnabled(true);
    }

    public static final int MSG_OPEN_TEST_URL = 0;
    public static final int MSG_INIT_UI = 1;
    private final int mUrlStartNum = 0;
    private int mCurrentUrl = mUrlStartNum;
    private Handler mTestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OPEN_TEST_URL:
                    if (!mNeedTestPage) {
                        return;
                    }

                    String testUrl = "file:///sdcard/outputHtml/html/"
                            + Integer.toString(mCurrentUrl) + ".html";
                    if (mWebView != null) {
                        mWebView.loadUrl(testUrl);
                    }

                    mCurrentUrl++;
                    break;
                case MSG_INIT_UI:
                    initWebView();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || mWebView == null || intent.getData() == null)
            return;
        mWebView.loadUrl(intent.getData().toString());
    }
}
