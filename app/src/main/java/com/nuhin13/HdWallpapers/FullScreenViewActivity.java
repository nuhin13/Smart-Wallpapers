package com.nuhin13.HdWallpapers;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nuhin13.HdWallpapers.app.AppController;
import com.nuhin13.HdWallpapers.picasa.model.Wallpaper;
import com.nuhin13.HdWallpapers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FullScreenViewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = FullScreenViewActivity.class
            .getSimpleName();
    public static final String TAG_SEL_IMAGE = "selectedImage";
    private Wallpaper selectedPhoto;
    private ImageView fullImageView;
    private LinearLayout llSetWallpaper, llDownloadWallpaper;
    private Utils utils;
    private ProgressBar pbLoader;
    private CoordinatorLayout coordinatorLayout;
    private int progressStatus = 0;
    private Handler handler = new Handler();



    private String licencseURL,licenseeURL = null;

    // Picasa JSON response node keys
    private static final String TAG_ENTRY = "entry",
            TAG_MEDIA_GROUP = "media$group",
            TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "url",
            TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen_image);

        fullImageView = (ImageView) findViewById(R.id.imgFullscreen);
        llSetWallpaper = (LinearLayout) findViewById(R.id.llSetWallpaper);
        llDownloadWallpaper = (LinearLayout) findViewById(R.id.llDownloadWallpaper);
        pbLoader = (ProgressBar) findViewById(R.id.pbLoader);
        pbLoader.setIndeterminate(true);
        pbLoader.setMax(100);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Hd Wallpapers");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);


        utils = new Utils(getApplicationContext());

        // layout click listeners
        llSetWallpaper.setOnClickListener(this);
        llDownloadWallpaper.setOnClickListener(this);

        // setting layout buttons alpha/opacity
        llSetWallpaper.getBackground().setAlpha(70);
        llDownloadWallpaper.getBackground().setAlpha(70);


        Intent i = getIntent();
        selectedPhoto = (Wallpaper) i.getSerializableExtra(TAG_SEL_IMAGE);

        // check for selected photo null
        if (selectedPhoto != null) {

            // fetch photo full resolution image by making another json request
            fetchFullResolutionImage();

        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT)
                    .show();
        }
    }


    /**
     * Fetching image fullresolution json
     * */


    private void fetchFullResolutionImage() {
        String url = selectedPhoto.getPhotoJson();

        // show loader before making request
        pbLoader.setVisibility(View.VISIBLE);
        llSetWallpaper.setVisibility(View.GONE);
        llDownloadWallpaper.setVisibility(View.GONE);


        getSupportActionBar().hide();


        // volley's json obj request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG,
                        "Image full resolution json: "
                                + response.toString());
                try {
                    // Parsing the json response
                    JSONObject entry = response
                            .getJSONObject(TAG_ENTRY);

                    JSONArray mediacontentArry = entry.getJSONObject(
                            TAG_MEDIA_GROUP).getJSONArray(
                            TAG_MEDIA_CONTENT);

                    JSONObject mediaObj = (JSONObject) mediacontentArry
                            .get(0);

                    String fullResolutionUrl = mediaObj
                            .getString(TAG_IMG_URL);

                    // image full resolution widht and height
                    final int width = mediaObj.getInt(TAG_IMG_WIDTH);
                    final int height = mediaObj.getInt(TAG_IMG_HEIGHT);

                    Log.d(TAG, "Full resolution image. url: "
                            + fullResolutionUrl + ", w: " + width
                            + ", h: " + height);

                    ImageLoader imageLoader = AppController
                            .getInstance().getImageLoader();

                    // We download image into ImageView instead of
                    // NetworkImageView to have callback methods
                    // Currently NetworkImageView doesn't have callback
                    // methods

                    ///
                    progressStatus = 0;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (progressStatus<100){
                                progressStatus += 1;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pbLoader.setProgress(progressStatus);
                                    }
                                });
                                try {
                                    Thread.sleep(100);
                                }catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();

                    ///

                    imageLoader.get(fullResolutionUrl,
                            new ImageLoader.ImageListener() {

                                @Override
                                public void onErrorResponse(
                                        VolleyError arg0) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            getString(R.string.msg_wall_fetch_error),
                                            Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onResponse(
                                        ImageLoader.ImageContainer response,
                                        boolean arg1) {
                                    if (response.getBitmap() != null) {
                                        // load bitmap into imageview

                                        fullImageView
                                                .setImageBitmap(response
                                                        .getBitmap());
                                        adjustImageAspect(width, height);


                                        // hide loader and show set &
                                        // download buttons
                                        pbLoader.setVisibility(View.GONE);
                                        llSetWallpaper
                                                .setVisibility(View.VISIBLE);
                                        llDownloadWallpaper
                                                .setVisibility(View.VISIBLE);

                                        getSupportActionBar().show();

                                    }
                                }
                            });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.msg_unknown_error),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                // unable to fetch wallpapers
                // either google username is wrong or
                // devices doesn't have internet connection
                Toast.makeText(getApplicationContext(),
                        getString(R.string.msg_wall_fetch_error),
                        Toast.LENGTH_LONG).show();

            }
        });

        // Remove the url from cache
        AppController.getInstance().getRequestQueue().getCache().remove(url);

        // Disable the cache for this url, so that it always fetches updated
        // json
        jsonObjReq.setShouldCache(false);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Adjusting the image aspect ration to scroll horizontally, Image height
     * will be screen height, width will be calculated respected to height
     * */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void adjustImageAspect(int bWidth, int bHeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if (bWidth == 0 || bHeight == 0)
            return;

        int sHeight = 0;

        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sHeight = size.y;
        } else {
            Display display = getWindowManager().getDefaultDisplay();
            sHeight = display.getHeight();
        }

        int new_width = (int) Math.floor((double) bWidth * (double) sHeight
                / (double) bHeight);
        params.width = new_width;
        params.height = sHeight;

        Log.d(TAG, "Fullscreen image new dimensions: w = " + new_width
                + ", h = " + sHeight);

        fullImageView.setLayoutParams(params);
    }

    /**
     * View click listener
     * */
    @Override
    public void onClick(View v) {
        Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable())
                .getBitmap();
        switch (v.getId()) {
            // button Download Wallpaper tapped
            case R.id.llDownloadWallpaper:
                utils.saveImageToSDCard(bitmap, coordinatorLayout);
                break;
            // button Set As Wallpaper tapped
            case R.id.llSetWallpaper:
                utils.setAsWallpaper(bitmap, coordinatorLayout);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
        return true;
    }

    private void gotoURL(String url)
    {
        Uri uri =  Uri.parse(url);
        Intent goToWebsite = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        goToWebsite.addFlags(flags);

        try {
            startActivity(goToWebsite);
        } catch (ActivityNotFoundException e) {
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable())
                .getBitmap();

        switch (id) {
            case android.R.id.home:

                finish();
                return true;
            case R.id.action_set_as_wallpaper:
                utils.setAsWallpaper(bitmap, coordinatorLayout);
                return true;
            case R.id.action_download:
                utils.saveImageToSDCard(bitmap, coordinatorLayout);
                return true;
            case R.id.action_share:
                utils.shareImage(bitmap, coordinatorLayout);
                return true;
            /*case R.id.action_report:
                utils.reportImage(bitmap);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}