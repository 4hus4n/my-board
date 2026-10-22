package mv.clickandcraft.myboard;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.webkit.WebViewAssetLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/** My Board: the web app in a full-screen, landscape WebView. Crafted by clickandcraft in Milandhoo. */
public class MainActivity extends Activity {

    private static final String HOST = "appassets.androidplatform.net";
    private WebView web;
    private volatile boolean locked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= 28) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        web = new WebView(this);
        web.setBackgroundColor(Color.rgb(0x10, 0x13, 0x1f));
        setContentView(web);
        hideSystemBars();

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setTextZoom(100);

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .setDomain(HOST)
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                if (HOST.equals(url.getHost())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, url)); } catch (Exception ignored) { }
                return true;
            }
        });

        web.addJavascriptInterface(new Bridge(), "MyBoardApp");

        if (Build.VERSION.SDK_INT < 29
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (savedInstanceState != null) web.restoreState(savedInstanceState);
        else web.loadUrl("https://" + HOST + "/assets/index.html");
    }

    @SuppressWarnings("deprecation")
    private void hideSystemBars() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemBars();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
        hideSystemBars();
    }

    @Override
    protected void onPause() {
        web.onPause();
        super.onPause();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        if (locked) return;            // child lock: Back does nothing
        super.onBackPressed();
    }

    /** Called from the page as window.MyBoardApp. */
    private class Bridge {
        @JavascriptInterface
        public void setLocked(boolean value) {
            locked = value;
        }

        @JavascriptInterface
        public boolean savePng(String name, String base64) {
            try {
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                String file = (name == null || name.isEmpty()) ? "my-board.png" : name.replaceAll("[^A-Za-z0-9._-]", "_");
                if (Build.VERSION.SDK_INT >= 29) {
                    ContentValues v = new ContentValues();
                    v.put(MediaStore.Images.Media.DISPLAY_NAME, file);
                    v.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                    v.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/My Board");
                    ContentResolver cr = getContentResolver();
                    Uri uri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
                    if (uri == null) return false;
                    try (OutputStream out = cr.openOutputStream(uri)) {
                        if (out == null) return false;
                        out.write(bytes);
                    }
                } else {
                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "My Board");
                    if (!dir.exists() && !dir.mkdirs()) return false;
                    File f = new File(dir, file);
                    try (FileOutputStream out = new FileOutputStream(f)) {
                        out.write(bytes);
                    }
                    MediaScannerConnection.scanFile(MainActivity.this, new String[]{f.getAbsolutePath()}, new String[]{"image/png"}, null);
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
