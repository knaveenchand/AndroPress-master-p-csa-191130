package ir.parishkaar.wordpressjsonclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class SampleDelete extends AppCompatActivity implements SearchView.OnQueryTextListener {

    WebView webView;
    String html, mdata, mtitle;
    ProgressBar pbar;
    FragmentManager fragmentManager;
    int selectedCat = 0;
    String selectedCatslug, searchingquery;
    MenuItem searchItem;
    boolean isReadyToExit = false, isPostPage = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npm_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent bundle = getIntent();
        mdata = bundle.getStringExtra("data");
        mtitle = bundle.getStringExtra("npmtitle");

//        Toast.makeText(getApplicationContext(), "Hello "+mdata, Toast.LENGTH_SHORT).show();

        webView = (WebView) findViewById(R.id.web);
        pbar = (ProgressBar)findViewById(R.id.progressBar1);

        setTitle(mtitle);


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

        //local file loading

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/jquery.html");


//        webView.setWebViewClient(new WebViewClient(){
//            public void onPageFinished(WebView view, String url){
//                webView.loadUrl("javascript:init('" + mdata + "')");
//            }
//        });

        //local file loading end

//        webView.loadUrl("http://pestoscope.ekrishi.net/api/get_page/?id=2131");


//        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);



//                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    public class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {

            // TODO Auto-generated method stub

            webView.loadUrl("javascript:init('" + mdata + "')");

            super.onPageFinished(view, url);
            pbar.setVisibility(View.GONE);

        }

    }


    public static String searchRequest(String searchString)
            throws IOException {

        String jsonurl =
                "http://pestoscope.ekrishi.net/api/get_page/?id=";

        String newFeed=jsonurl+searchString;
        StringBuilder response = new StringBuilder();
        Log.v("gsearch","gsearch url:"+newFeed);
        URL url = new URL(newFeed);

        HttpURLConnection httpconn
                = (HttpURLConnection) url.openConnection();


        if(httpconn.getResponseCode()==HttpURLConnection.HTTP_OK) {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(httpconn.getInputStream()),
                    8192);
            String strLine = null;
            while ((strLine = input.readLine()) != null) {
                response.append(strLine);
            }
            input.close();
        }
        Log.v("responseToString","responseToString:"+response.toString());

        return response.toString();
    }

    public String processResponse(String resp) throws IllegalStateException,
            IOException, JSONException, NoSuchAlgorithmException {
//        StringBuilder sb = new StringBuilder();
        Log.v("gsearch","gsearch result:"+resp);
        JSONObject mResponseObject = new JSONObject(resp);
        JSONObject responObject
                = mResponseObject.getJSONObject("page");
        JSONArray array = responObject.getJSONArray("content");
        //Log.v("gsearch","number of results:"+array.length());

        String content = array.getJSONObject(0).getString("content");

        return content;

    }

//    public void loadPostFragment(String catslug, int cat, String query) {
//        isPostPage = false;
//        if (searchItem != null) {
//            searchItem.setVisible(true);
//        }
//        PostFragment postFragment = new PostFragment();
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        Bundle bundle = new Bundle();
//        bundle.putString("slug", catslug);
//        bundle.putInt("cat", cat);
//        bundle.putString("query", query);
//        postFragment.setArguments(bundle);
//        fragmentManager.beginTransaction().replace(R.id.frame, postFragment).commit();
//    }


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
//            String body = String.format(getResources().getString(R.string.share_body), postModel.title, postModel.url);
//            Util.shareData(SampleDelete.this, getResources().getString(R.string.share_title), body);
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

    public String htmlifyText(String txt) {

        String squaretext = txt.toString().replaceAll("(^\\[|\\]$)", "");

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
        Intent mainsearch = new Intent(SampleDelete.this, MainActivity.class);
        mainsearch.putExtra("Description","");
        mainsearch.putExtra("searchingquery", query);
        startActivity(mainsearch);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
