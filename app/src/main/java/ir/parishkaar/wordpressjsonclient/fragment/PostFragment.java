package ir.parishkaar.wordpressjsonclient.fragment;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ir.parishkaar.wordpressjsonclient.DetailActivity;
import ir.parishkaar.wordpressjsonclient.R;
import ir.parishkaar.wordpressjsonclient.adapter.PostAdapter;
import ir.parishkaar.wordpressjsonclient.model.PostModel;
import ir.parishkaar.wordpressjsonclient.util.NetUtil;
import ir.parishkaar.wordpressjsonclient.util.RecyclerItemClickListener;
import ir.parishkaar.wordpressjsonclient.util.Util;

public class PostFragment extends Fragment {

    RecyclerView mRecyclerView;
    JSONArray posts;
    View rootView;
    String adress, search, selectedCatslug = "";
    RecyclerView postList;
    List<PostModel> postArray;
    PostAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    CardView noDataCard, noArchiveCard;
    Button btnReload;
    int page = 1, selectedCat = 0, previousTotal = 0,
            visibleThreshold = 5, firstVisibleItem, visibleItemCount,
            totalItemCount;
    boolean loading = true;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_post, container, false);

        Bundle bundle = this.getArguments();
        selectedCatslug = bundle.getString("slug", "");
        selectedCat = bundle.getInt("cat", 0);
        search = bundle.getString("query", "");

        postList = (RecyclerView) rootView.findViewById(R.id.post_list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);

        btnReload = (Button) rootView.findViewById(R.id.btn_reload);
        noDataCard = (CardView) rootView.findViewById(R.id.no_data);
        noArchiveCard = (CardView) rootView.findViewById(R.id.no_archive);

//        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
//        postList.setLayoutManager(mGridLayoutManager);

        postArray = new ArrayList<>();
        adapter = new PostAdapter(getActivity(), postArray);
        postList.setLayoutManager(mGridLayoutManager);
        postList.setAdapter(adapter);

        refreshPosts();

//        if (selectedCatslug == "") {
//            refreshPosts();
//        } else {
//            loadArchive();
//        }

        postList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("data", postArray.get(position).toJson().toString());
                    if (selectedCat == -1) {
                        intent.putExtra("is_archive", "true");
                    } else {
                        intent.putExtra("is_archive", "false");
                    }
                    startActivity(intent);
                }
            }
        }));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPosts();
            }
        });

//        postList.addOnScrollListener(scrollListener);

        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                noDataCard.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(true);
                refreshPosts();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedCat == -1) {
            loadArchive();
        }
    }

    public void loadArchive() {
        postArray = new ArrayList<>();
        postArray = PostModel.listAll(PostModel.class);
        adapter.update(postArray);

        if (postArray.size() <= 0) {
            noArchiveCard.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        }
    }

    public void refreshPosts() {
        if (Util.isNetworkAvailable(getActivity())) {
            swipeRefreshLayout.setRefreshing(true);
            postArray = new ArrayList<>();
            loading = false;
            page = 1;
            getPosts(selectedCat, selectedCatslug);
        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    getResources().getString(R.string.no_internet),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    public void getPosts(int cat, String selectedCatslug) {
        if ((cat == 0) & (selectedCatslug == "")) {
            adress = "get_recent_posts/?page=" + page + "&order=ASC&order_by=slug&count=" + NetUtil.postCount + "&post_type=product";

        } else if (cat > 0) {
            adress = "get_category_posts/?id=" + cat + "&count=" + NetUtil.postCount + "&order=ASC&order_by=slug&page=" + page + "&post_type=product";
        } else {
//                adress = "get_posts/?product_cat=" + selectedCatslug +"&order=ASC&order_by=slug";
            adress = "korkmaz/get_taxonomy_posts/?taxonomy=product_cat&slug=" + selectedCatslug + "&orderby=title&order=ASC&count=35";

//            http://pestoscope.ekrishi.net/api/korkmaz/get_taxonomy_posts/?taxonomy=product_cat&slug=brinjal
        }


        if (!search.equals("")) {
            adress = "get_search_results/?search=" + search + "&order=ASC&orderby=title&page=" + page + "&count=" + NetUtil.postCount;
        }

        if (Util.isNetworkAvailable(getActivity())) {
            NetUtil.get(adress, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        posts = response.getJSONArray("posts");

                        PostModel postModel;
                        for (int i = 0; i < posts.length(); i++) {
                            postModel = new PostModel();
                            postModel.fromJson(posts.getJSONObject(i));
                            postArray.add(postModel);
                        }
                        adapter.update(postArray);
                        swipeRefreshLayout.setRefreshing(false);

                        if (!(postArray.size() > 0)) {
                            swipeRefreshLayout.setVisibility(View.GONE);
                            noDataCard.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    noDataCard.setVisibility(View.VISIBLE);
                }

            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setVisibility(View.GONE);
            noDataCard.setVisibility(View.VISIBLE);
        }
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = linearLayoutManager.getItemCount();
            firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                page++;
                getPosts(selectedCat, selectedCatslug);
                loading = true;
            }

        }
    };

    public void showDataUi() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        noDataCard.setVisibility(View.GONE);
    }

}
