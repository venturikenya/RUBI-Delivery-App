package ke.co.venturisys.rubideliveryapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.others.WebUtils;

import static ke.co.venturisys.rubideliveryapp.others.Constants.EXTRA_POST_URL;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setUpActionBar;

public class BrowserActivity extends AppCompatActivity {

    String activity_title;
    String postUrl;
    WebView webView;
    float m_downX;
    ProgressBar progressBar; // shown while web page is being loaded

    public static Intent newIntent(Context context, String postUrl) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(EXTRA_POST_URL, postUrl);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        initialiseWidgets();
        activity_title = getString(R.string.activity_title_browser);

        setUpActionBar(activity_title, this);

        // initialise widgets
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorProgressBar), PorterDuff.Mode.SRC_IN);

        // set up web view by setting its settings and web client
        initWebView();
        // load url
        renderPost();
    }

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    private void initWebView() {
        webView.setWebChromeClient(new MyWebChromeClient(this));
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*
                 * Check for the url, if the url is from same domain
                 * open the url in the same activity as new intent
                 * else pass the url to browser activity
                 * */
                if (WebUtils.isSameDomain(postUrl, url)) {
                    Intent intent = BrowserActivity.newIntent(BrowserActivity.this,
                            url);
                    intent.putExtra("postUrl", url);
                    startActivity(intent);
                } else {
                    // launch in-app browser i.e BrowserActivity
                    openInAppBrowser(url);
                }

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                BrowserActivity.this.activity_title = view.getTitle();
                setUpActionBar(BrowserActivity.this.activity_title, BrowserActivity.this);
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        // Enabling zoom-in controls
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getPointerCount() > 1) {
                    //Multi touch detected
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // save the x
                        m_downX = event.getX();
                    }
                    break;

                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.getY());
                    }
                    break;

                }

                return false;
            }
        });
    }

    protected void initialiseWidgets() {
        if (getIntent() != null) {
            postUrl = getIntent().getStringExtra(EXTRA_POST_URL);
        }
    }

    private void openInAppBrowser(String url) {
        startActivity(newIntent(this, url));
    }

    private void renderPost() {
        webView.loadUrl(postUrl);
    }

    @Override
    public void onBackPressed() {
        exitToTargetActivity(this, MainActivity.class);
        super.onBackPressed();
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
    }
}
