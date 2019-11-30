package ir.parishkaar.wordpressjsonclient;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ir.parishkaar.wordpressjsonclient.model.PostModel;
import ir.parishkaar.wordpressjsonclient.util.Util;
import ir.parishkaar.wordpressjsonclient.view.SwipeBaseActivity;

public class NPMDetailActivity extends SwipeBaseActivity {

    PostModel postModel;
    WebView webView, webViewMgmt, webViewcausedby, webViewproblem, webViewcomments;
    ImageView imageView;
    TextView txtAuthorDate;
    FloatingActionButton fab_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npm_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (WebView) findViewById(R.id.web);

        imageView = (ImageView) findViewById(R.id.image);

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

//        String is_archive = bundle.getStringExtra("is_archive");
//        if (is_archive.equals("true")) {
//            fab_comment.setVisibility(View.GONE);
//            fab_comment.setEnabled(false);
//        }

//        setTitle(postModel.title);

        String html = htmlifyText(postModel.content);
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);

//        String htmlMgmt = htmlifyText(postModel.management);
//        webViewMgmt.loadDataWithBaseURL("file:///android_asset/", htmlMgmt, "text/html", "UTF-8", null);
//
//        String htmlcausedby = htmlifyText(postModel.scientific_name);
//        webViewcausedby.loadDataWithBaseURL("file:///android_asset/", htmlcausedby, "text/html", "UTF-8", null);
//
//        String htmlproblem = htmlifyText(postModel.problem_category);
//        webViewproblem.loadDataWithBaseURL("file:///android_asset/", htmlproblem, "text/html", "UTF-8", null);
//
//        String htmlcomments = htmlifyText(postModel.customcomments);
//        webViewcomments.loadDataWithBaseURL("file:///android_asset/", htmlcomments, "text/html", "UTF-8", null);

//        Picasso.with(getBaseContext())
//                .load(postModel.img)
//                .placeholder(R.drawable.placeholder)
//                .into(imageView);

//        txtAuthorDate.setText(
//                Html.fromHtml(getString(R.string.written_by) + "<b> " + postModel.author +
//                        " </b> " + getString(R.string.on_date) + " <b> " + postModel.date + "</b>")
//        );

//        && !(postModel.comment_count > 0)
//        if (!(postModel.comment_status.equals("open"))) {
//            fab_comment.setVisibility(View.GONE);
//        }

//        fab_comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), CommentsActivity.class);
//                JSONArray jsonArray = new JSONArray();
//                for (int i = 0; i < postModel.comments.size(); i++) {
//                    jsonArray.put(postModel.comments.get(i).toJson());
//                }
//                intent.putExtra("data", jsonArray.toString());
//                intent.putExtra("postId", postModel.id);
//                intent.putExtra("commentStatus", postModel.comment_status);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Util.shareData(NPMDetailActivity.this, getResources().getString(R.string.share_title), body);
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
}
