package ir.parishkaar.wordpressjsonclient;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.SliderLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ir.parishkaar.wordpressjsonclient.model.PostModel;
import ir.parishkaar.wordpressjsonclient.util.Util;
import ir.parishkaar.wordpressjsonclient.view.SwipeBaseActivity;


public class DetailActivity extends SwipeBaseActivity implements SearchView.OnQueryTextListener{

    PostModel postModel;
    ProgressBar pbar;
    WebView webView, webViewMgmt, webViewcausedby, webViewproblem, webViewcomments, webViewimages;
    ImageView imageView, imageOneView, imageTwoView, imageThreeView, imageFourView;
    TextView txtAuthorDate, textViewCausedBy, textViewProblem, textViewMgmt,textViewSymptoms, textViewComments;
    FloatingActionButton fab_comment;
    String selectedCatslug, imageone, imagetwo, imagethree, imagefour;
    MenuItem searchItem;
    ListView listView;
    TextView textView;
    RequestQueue mRequestQueue;
    JSONArray myimageslist;
    RelativeLayout myimglayout;
    String path, pathescaped, postname;

    SliderLayout sliderLayout;
    HashMap<String,String> Hash_file_maps ;
    JSONObject jsonFormattedStringJson;
    Integer webviewimagespostid;
    String webviewimagesurl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewCausedBy = (TextView) findViewById(R.id.textcausedby);
        textViewProblem = (TextView) findViewById(R.id.textproblemcategory);
        textViewSymptoms = (TextView) findViewById(R.id.textsymptoms);
        textViewMgmt = (TextView) findViewById(R.id.textmgmt);
        textViewComments = (TextView) findViewById(R.id.textcomments);

//        webView = (WebView) findViewById(R.id.web);
//        webViewMgmt = (WebView) findViewById(R.id.webmgmt);
//        webViewcausedby = (WebView) findViewById(R.id.webcausedby);
//        webViewproblem = (WebView) findViewById(R.id.webproblemcategory);
//        webViewcomments = (WebView) findViewById(R.id.webcomments);

        imageView = (ImageView) findViewById(R.id.image);
        txtAuthorDate = (TextView) findViewById(R.id.txt_date_author);
        fab_comment = (FloatingActionButton) findViewById(R.id.fab_comments);

//        imageOneView = (ImageView) findViewById(R.id.imageone);
//        imageTwoView = (ImageView) findViewById(R.id.imagetwo);
//        imageThreeView = (ImageView) findViewById(R.id.imagethree);
//        imageFourView = (ImageView) findViewById(R.id.imagefour);

        pbar = (ProgressBar)findViewById(R.id.progressBar1);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent bundle = getIntent();
        postModel = new PostModel();
        try {
            postModel.fromJson(new JSONObject(bundle.getStringExtra("data")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String is_archive = bundle.getStringExtra("is_archive");
        if (is_archive.equals("true")) {
            fab_comment.setVisibility(View.GONE);
            fab_comment.setEnabled(false);
        }

        setTitle(postModel.title);

//        carouselView = (CarouselView) findViewById(R.id.carouselView);
//        carouselView.setPageCount(sampleImages.length);
//
//        carouselView.setImageListener(imageListener);

        textViewCausedBy.setText(unSquareUnQuotedtext(postModel.scientific_name));
        textViewProblem.setText(unSquareUnQuotedtext(postModel.problem_category));
        textViewSymptoms.setText(unSquareUnQuotedtext(postModel.symptoms));
        textViewMgmt.setText(unSquareUnQuotedtext(postModel.management));
        textViewComments.setText(unSquareUnQuotedtext(postModel.customcomments));


//        String html = htmlifyText(postModel.symptoms);
//        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
//
//        String htmlMgmt = htmlifyText(postModel.management);
//        webViewMgmt.loadDataWithBaseURL("file:///android_asset/", htmlMgmt, "text/html", "UTF-8", null);
//
//        String htmlcausedby = htmlifyText(postModel.scientific_name);
////        webViewcausedby.loadDataWithBaseURL("file:///android_asset/", htmlcausedby, "text/html", "UTF-8", null);
//
//        String htmlproblem = htmlifyText(postModel.problem_category);
//        webViewproblem.loadDataWithBaseURL("file:///android_asset/", htmlproblem, "text/html", "UTF-8", null);
//
//        String htmlcomments = htmlifyText(postModel.customcomments);
//        webViewcomments.loadDataWithBaseURL("file:///android_asset/", htmlcomments, "text/html", "UTF-8", null);



        Picasso.with(getBaseContext())
                .load(postModel.img)
                .placeholder(R.drawable.placeholder)
                .into(imageView);

        txtAuthorDate.setText(
                Html.fromHtml(getString(R.string.written_by) + "<b> " + postModel.author +
                        " </b> " + getString(R.string.on_date) + " <b> " + postModel.date + "</b>")
        );


        webViewimages = (WebView) findViewById(R.id.webimages);
//        pbar = (ProgressBar)findViewById(R.id.progressBar1);

//        setTitle(mtitle);


//        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
//
//
//        if(mdata != null) {
//            try{
//
//                 html = htmlifyText(processResponse(
//                        searchRequest(mdata)));
//
//            } catch(Exception e) {
//                Log.v("Exception Google search",
//                        "Exception:"+e.getMessage());
//            }
//        }

        webviewimagespostid = 0;
        webviewimagespostid = postModel.id;
        webviewimagesurl = "http://salesapp.matenek.com/salesmatenek/navtests/jqueryimage.html?androidpostid="+webviewimagespostid;
        //local file loading
        webViewimages.getSettings().setJavaScriptEnabled(true);
        webViewimages.loadUrl(webviewimagesurl);


//        && !(postModel.comment_count > 0)
        if (!(postModel.comment_status.equals("open"))) {
            fab_comment.setVisibility(View.GONE);
        }

        fab_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CommentsActivity.class);
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < postModel.comments.size(); i++) {
                    jsonArray.put(postModel.comments.get(i).toJson());
                }
                intent.putExtra("data", jsonArray.toString());
                intent.putExtra("postId", postModel.id);
                intent.putExtra("commentStatus", postModel.comment_status);
                startActivity(intent);
            }
        });

        pbar.setVisibility(View.GONE);


    }


    public JSONObject getImageUrls(){


        Response.Listener<String> response_listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response",response);
                String jsonFormattedString = response.replaceAll("\\\\", "");
                Log.e("jsonFormattedResponse",jsonFormattedString);

                try {
                    jsonFormattedStringJson = new JSONObject(jsonFormattedString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Hash_file_maps = new HashMap<String, String>();
//
//                sliderLayout = (SliderLayout)findViewById(R.id.slider);
//

//
//                for(String name : Hash_file_maps.keySet()){
//
//                    TextSliderView textSliderView = new TextSliderView(DetailActivity.this);
//                    textSliderView
//                            .description(name)
//                            .image(Hash_file_maps.get(name))
//                            .setScaleType(BaseSliderView.ScaleType.Fit)
//                            .setOnSliderClickListener(DetailActivity.this);
//                    textSliderView.bundle(new Bundle());
//                    textSliderView.getBundle()
//                            .putString("extra",name);
//                    sliderLayout.addSlider(textSliderView);
//                }
//                sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
//                sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//                sliderLayout.setCustomAnimation(new DescriptionAnimation());
//                sliderLayout.setDuration(3000);
//                sliderLayout.addOnPageChangeListener(DetailActivity.this);

            }
        };


        Response.ErrorListener response_error_listener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //TODO
                } else if (error instanceof AuthFailureError) {
                    //TODO
                } else if (error instanceof ServerError) {
                    //TODO
                } else if (error instanceof NetworkError) {
                    //TODO
                } else if (error instanceof ParseError) {
                    //TODO
                }
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://pestoscope.ekrishi.net/api/korkmaz/get_attachmenturls_by_post/?androidpostid="+postModel.id,
                response_listener,response_error_listener);
        getRequestQueue().add(stringRequest);

        return jsonFormattedStringJson;

    }

    public RequestQueue getRequestQueue() {
        //requestQueue is used to stack your request and handles your cache.
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }



//    ImageListener imageListener = new ImageListener() {
//        @Override
//        public void setImageForPosition(int position, ImageView imageView) {
//            imageView.setImageResource(sampleImages[position]);
//        }
//    };

//
//    @Override
//    protected void onStop() {
//
//        sliderLayout.stopAutoCycle();
//
//        super.onStop();
//    }
//
//    @Override
//    public void onSliderClick(BaseSliderView slider) {
//
//        Toast.makeText(this,slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
//
//    @Override
//    public void onPageSelected(int position) {
//
//        Log.d("Slider Demo", "Page Changed: " + position);
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
//                loadPostFragment(selectedCatslug,0, "");
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_share) {
            String body = String.format(getResources().getString(R.string.share_body), postModel.title, postModel.url);
            Util.shareData(DetailActivity.this, getResources().getString(R.string.share_title), body);
        }
        if (id == R.id.action_favorite) {

            Intent pIntent = new Intent(DetailActivity.this, LoginActivity.class);
            startActivity(pIntent);
        }

//        else if (id == R.id.action_favorite) {
//            List<PostModel> temp = PostModel.find(PostModel.class, "title = ?", postModel.title);
//            if (temp.size() <= 0) {
//                postModel.save();
//                Snackbar.make(findViewById(android.R.id.content),
//                        getString(R.string.added_to_favorites),
//                        Snackbar.LENGTH_SHORT).show();
//            }
//            else {
//                if (temp.get(0).delete())
//                    Snackbar.make(findViewById(android.R.id.content),
//                            getString(R.string.removed_from_favorites),
//                            Snackbar.LENGTH_SHORT).show();
//            }

//        }

        return super.onOptionsItemSelected(item);
    }

    public String unSquareUnQuotedtext(String txt) {
        String squaretext = (txt.toString().replaceAll("(^\\[|\\]$)", "")).trim();
        String unquotedtext = squaretext.toString().substring(1,squaretext.toString().length()-1);

        return  unquotedtext;
    }

    public String htmlifyText(String txt) {

        String squaretext = (txt.toString().replaceAll("(^\\[|\\]$)", "")).trim();

        String html = "";
        html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>\n" +
                "<style> \n" +
                "img{display: inline; max-width:100%; width:auto; height:auto;}" +
                "table{border-collapse: collapse !important;width:auto !important; }" +
                "table, th, td {\n" +
                "    border: 1px solid black; width:100% !important; \n" +
                "}" +
                "body{text-align:justify;color:#212121 !important;}" +
                "</style>\n" +
                "</head>\n" +
                "<body style=\"\">"
                + squaretext + "</body></html>";
        return html;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        selectedCatslug = "";
        Intent mIntent = new Intent(DetailActivity.this, MainActivity.class);
        mIntent.putExtra("Description", selectedCatslug);
        mIntent.putExtra("searchingquery", query);

        startActivity(mIntent);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
