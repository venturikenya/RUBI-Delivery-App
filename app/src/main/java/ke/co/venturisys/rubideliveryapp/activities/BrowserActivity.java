package ke.co.venturisys.rubideliveryapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import ke.co.venturisys.rubideliveryapp.R;
import ke.co.venturisys.rubideliveryapp.others.WebUtilities;

import static ke.co.venturisys.rubideliveryapp.others.Constants.EXTRA_POST_URL;
import static ke.co.venturisys.rubideliveryapp.others.Constants.TAG;
import static ke.co.venturisys.rubideliveryapp.others.Extras.exitToTargetActivity;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setImageViewDrawableColor;
import static ke.co.venturisys.rubideliveryapp.others.Extras.setUpActionBar;
import static ke.co.venturisys.rubideliveryapp.others.WebUtilities.bookmarkUrl;
import static ke.co.venturisys.rubideliveryapp.others.WebUtilities.tintMenuIcon;

public class BrowserActivity extends AppCompatActivity {

    String activity_title;
    String postUrl;
    WebView webView;
    ImageView forwardBtn, backBtn, bookmarkBtn;
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
        forwardBtn = findViewById(R.id.forwardBtn);
        backBtn = findViewById(R.id.backBtn);
        setImageViewDrawableColor(backBtn.getDrawable(), getResources()
                .getColor(R.color.black));
        bookmarkBtn = findViewById(R.id.bookmarkBtn);
        progressBar = findViewById(R.id.progressBar);
        setImageViewDrawableColor(progressBar.getIndeterminateDrawable(), getResources()
                .getColor(R.color.colorProgressBar));

        // continuously check if web view can move forward/backwards & change color accordingly
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (webView.canGoBack()) {
                            backBtn.setEnabled(true);
                            tintMenuIcon(BrowserActivity.this, backBtn, R.color.colorApp);
                        } else {
                            backBtn.setEnabled(false);
                            tintMenuIcon(BrowserActivity.this, backBtn, R.color.black);
                        }

                        if (webView.canGoForward()) {
                            forwardBtn.setEnabled(true);
                            tintMenuIcon(BrowserActivity.this, forwardBtn, R.color.colorApp);
                        } else {
                            forwardBtn.setEnabled(false);
                            tintMenuIcon(BrowserActivity.this, forwardBtn, R.color.black);
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);

        // set up web view by setting its settings and web client
        initWebView();
        // load url
        renderPost(postUrl);
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
                if (WebUtilities.isSameDomain(postUrl, url))
                    Log.e(TAG, "Web page of same domain visited");
                renderPost(url);
                postUrl = url;

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

    private void renderPost(String postUrl) {
        webView.loadUrl(postUrl);
    }

    @Override
    public void onBackPressed() {
        exitToTargetActivity(this, MainActivity.class);
        // super.onBackPressed();
    }

    public void back(View view) {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            Toast.makeText(BrowserActivity.this, "First page reached",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void forward(View view) {
        if (webView.canGoForward()) {
            webView.goForward();
        }
    }

    public void bookmark(View view) {
        boolean bookmark = bookmarkUrl(this, postUrl);
        if (bookmark) tintMenuIcon(this, bookmarkBtn, R.color.colorApp);
        else tintMenuIcon(this, bookmarkBtn, R.color.black);
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
    }
}
