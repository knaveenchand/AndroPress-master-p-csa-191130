package ir.parishkaar.wordpressjsonclient;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ir.parishkaar.wordpressjsonclient.fragment.PostFragment;
import ir.parishkaar.wordpressjsonclient.model.PostModel;

/**
 * Created by admin on 21/07/19.
 */

public class DetailGridActivity extends AppCompatActivity {

    Toolbar mToolbar;
    ImageView mFlower;
    TextView mDescription;
    String selectedCatslug;
    FragmentManager fragmentManager;
    PostModel postModel;
    WebView webView, webViewMgmt;
    ImageView imageView;
    TextView txtAuthorDate;
    FloatingActionButton fab_comment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        webView = (WebView) findViewById(R.id.web);
//        webViewMgmt = (WebView) findViewById(R.id.webmgmt);

        imageView = (ImageView) findViewById(R.id.image);
        txtAuthorDate = (TextView) findViewById(R.id.txt_date_author);
        fab_comment = (FloatingActionButton) findViewById(R.id.fab_comments);

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

//        String html = htmlifyText(postModel.symptoms);
//        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
//
//        String htmlMgmt = htmlifyText(postModel.management);
//        webViewMgmt.loadDataWithBaseURL("file:///android_asset/", htmlMgmt, "text/html", "UTF-8", null);

        Picasso.with(getBaseContext())
                .load(postModel.img)
                .placeholder(R.drawable.placeholder)
                .into(imageView);

        txtAuthorDate.setText(
                Html.fromHtml(getString(R.string.written_by) + "<b> " + postModel.author +
                        " </b> " + getString(R.string.on_date) + " <b> " + postModel.date + "</b>")
        );

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
    }

    public void loadPostFragment(String catslug, int cat, String query) {

        PostFragment postFragment = new PostFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Bundle bundle = new Bundle();
        bundle.putString("slug", catslug);
        bundle.putInt("cat", cat);
        bundle.putString("query", query);
        postFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.frame, postFragment).commit();
    }

    public String htmlifyText(String txt) {

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
                + txt + "</body></html>";
        return html;

    }
}
