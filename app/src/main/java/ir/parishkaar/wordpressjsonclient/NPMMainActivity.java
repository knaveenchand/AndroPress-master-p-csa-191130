package ir.parishkaar.wordpressjsonclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;
import cz.msebera.android.httpclient.util.EntityUtils;
import ir.parishkaar.wordpressjsonclient.adapter.NavExpandableListAdapter;
import ir.parishkaar.wordpressjsonclient.adapter.PostAdapter;
import ir.parishkaar.wordpressjsonclient.fragment.AboutFragment;
import ir.parishkaar.wordpressjsonclient.fragment.PostFragment;
import ir.parishkaar.wordpressjsonclient.model.CategoryModel;
import ir.parishkaar.wordpressjsonclient.model.DataModel;
import ir.parishkaar.wordpressjsonclient.model.ExpandedMenuModel;
import ir.parishkaar.wordpressjsonclient.util.NetUtil;
import ir.parishkaar.wordpressjsonclient.util.Util;



public class NPMMainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemListener, SearchView.OnQueryTextListener {


    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    List<FlowerData> mFlowerList;
    FlowerData mFlowerData;

    NavigationView navigationView;
    DrawerLayout drawer;
    Toolbar toolbar;
    ExpandableListView navMenuList;
    List<ExpandedMenuModel> navListHeader;
    HashMap<ExpandedMenuModel, List<CategoryModel>> navListChild;
    NavExpandableListAdapter navAdapter;
    FragmentManager fragmentManager;
    List<CategoryModel> categorylist;
    List<String> npmlist;
    List<String> npmListChild;
    int selectedCat = 0;
    String selectedCatslug;
    MenuItem searchItem;
    boolean isReadyToExit = false, isPostPage = true;
    List<String> listdata;
    PostAdapter adapter;


    RecyclerView recyclerView, recyclerView2, recyclerView3, recyclerView4;
    ArrayList<DataModel> arrayList, arrayList2, arrayList3, arrayList4;

    TextView npmTitleView, insectTitleView, trapsTitleView, solTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npmmain);


        npmTitleView = (TextView) findViewById(R.id.recyclerTitle);
        npmTitleView.setText("Non-Pesticide Management");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();
        arrayList.add(new DataModel("NPM", 0, "#173F5F"));
        arrayList.add(new DataModel("NPM Basics", 0, "#3E51B1"));

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, arrayList, this);
        recyclerView.setAdapter(adapter);

        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 500);
        recyclerView.setLayoutManager(layoutManager);

        insectTitleView = (TextView) findViewById(R.id.recyclerTitle2);
        insectTitleView.setText("Insect Biology and Behavior");

        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        arrayList2 = new ArrayList<>();
        arrayList2.add(new DataModel("Understanding Insect biology and behavior", 0, "#673BB7"));

        RecyclerViewAdapter adapter2 = new RecyclerViewAdapter(this, arrayList2, this);
        recyclerView2.setAdapter(adapter2);

        AutoFitGridLayoutManager layoutManager2 = new AutoFitGridLayoutManager(this, 1000);
        recyclerView2.setLayoutManager(layoutManager2);


        trapsTitleView = (TextView) findViewById(R.id.recyclerTitle3);
        trapsTitleView.setText("Traps");

        recyclerView3 = (RecyclerView) findViewById(R.id.recyclerView3);
        arrayList3 = new ArrayList<>();
        arrayList3.add(new DataModel("Traps", 0, "#F94336"));
        arrayList3.add(new DataModel("Pheromone Traps", 0, "#0A9B88"));
        arrayList3.add(new DataModel("Sticky Traps", 0, "#3CAEA3"));
        arrayList3.add(new DataModel("Light Traps", 0, "#369BB7"));


        RecyclerViewAdapter adapter3 = new RecyclerViewAdapter(this, arrayList3, this);
        recyclerView3.setAdapter(adapter3);

        AutoFitGridLayoutManager layoutManager3 = new AutoFitGridLayoutManager(this, 500);
        recyclerView3.setLayoutManager(layoutManager3);


        solTitleView = (TextView) findViewById(R.id.recyclerTitle4);
        solTitleView.setText("Solutions");

        recyclerView4 = (RecyclerView) findViewById(R.id.recyclerView4);
        arrayList4 = new ArrayList<>();
        arrayList4.add(new DataModel("Solutions", 0, "#9BCFB8"));
        arrayList4.add(new DataModel("Microbial Solutions", 0, "#7FB174"));
        arrayList4.add(new DataModel("Jeevamrut", 0, "#689C97"));


        RecyclerViewAdapter adapter4 = new RecyclerViewAdapter(this, arrayList4, this);
        recyclerView4.setAdapter(adapter4);

        AutoFitGridLayoutManager layoutManager4 = new AutoFitGridLayoutManager(this, 300);
        recyclerView4.setLayoutManager(layoutManager4);

//        arrayList.add(new DataModel("Traps", 0, "#4BAA50"));
//        arrayList.add(new DataModel("Solutions", 0, "#F94336"));
//        arrayList.add(new DataModel("Item 6", R.drawable.terraria, "#0A9B88"));




        /**
         AutoFitGridLayoutManager that auto fits the cells by the column width defined.
         **/



        /**
         Simple GridLayoutManager that spans two columns
         **/
        /*GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);*/

        //copied from mainactivity
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navMenuList = (ExpandableListView) findViewById(R.id.nav_menu);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        fragmentManager = getSupportFragmentManager();
        navMenuList.setGroupIndicator(null);
        prepareNavMenuData();
        navAdapter = new NavExpandableListAdapter(this, navListHeader, navListChild);
        navMenuList.setAdapter(navAdapter);

        navMenuList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (groupPosition == 0) {
                    selectedCat = groupPosition;
                    Intent i = new Intent(getApplicationContext(),MainGridActivity.class);
                    startActivity(i);

                    //loadPostFragment(selectedCatslug,0, "");
                } else if (groupPosition == 1) {
                    navListHeader.get(groupPosition).toggle();
                    return false;
                } else if (groupPosition == 2) {
//                    selectedCat = -1;
//                    loadPostFragment(selectedCatslug,0, "");
                    Intent npm = new Intent(getApplicationContext(),NPMMainActivity.class);
                    startActivity(npm);

                } else if (groupPosition == 3) {
//                    selectedCat = -1;
//                    loadPostFragment(selectedCatslug,0, "");
                    Intent queryphoto = new Intent(getApplicationContext(),CameraGalleryActivity.class);
                    startActivity(queryphoto);

                } else if (groupPosition == 4) {
//                    selectedCat = -1;
//                    loadPostFragment(selectedCatslug,0, "");
                    Intent loginlogout = new Intent(getApplicationContext(), Welcome.class);
                    startActivity(loginlogout);

                } else if (groupPosition == 5) {
                    searchItem.setVisible(false);
                    isPostPage = false;
                    fragmentManager.beginTransaction().replace(R.id.frame, new AboutFragment()).addToBackStack(null).commit();
                }
                onBackPressed();
                return false;
            }
        });

        navMenuList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //selectedCat = categorylist.get(childPosition).id;
                selectedCatslug = categorylist.get(childPosition).slug;
                Intent mIntent = new Intent(NPMMainActivity.this, MainActivity.class);
                mIntent.putExtra("Description", selectedCatslug);
                mIntent.putExtra("searchingquery", "");

                startActivity(mIntent);
                //loadPostFragment(selectedCatslug, 0,"");
                onBackPressed();
                return true;
            }
        });
        //below is from maingrid.
    }

    @Override
    public void onItemClick(DataModel item) throws IOException, JSONException {
//            Toast.makeText(getApplicationContext(), item.text + " is clicked", Toast.LENGTH_SHORT).show();

        if (item.text == "NPM") {
            Intent npmdetailact = new Intent(NPMMainActivity.this, SampleDelete.class);
            npmdetailact.putExtra("data", "2131");
            npmdetailact.putExtra("npmtitle", "NPM");
            startActivity(npmdetailact);
        }

        if (item.text == "NPM Basics") {
            Intent npmdetailact = new Intent(NPMMainActivity.this, SampleDelete.class);
            npmdetailact.putExtra("data", "2134");
            npmdetailact.putExtra("npmtitle", "NPM Basics");
            startActivity(npmdetailact);
        }

        if (item.text == "Understanding Insect biology and behavior") {
            Intent npmdetailact = new Intent(NPMMainActivity.this, SampleDelete.class);
            npmdetailact.putExtra("data", "2139");
            npmdetailact.putExtra("npmtitle", "Insect biology & behavior");
            startActivity(npmdetailact);
        }

        if (item.text == "Traps") {
            Intent npmdetailact = new Intent(NPMMainActivity.this, SampleDelete.class);
            npmdetailact.putExtra("data", "2146");
            npmdetailact.putExtra("npmtitle", "Traps");
            startActivity(npmdetailact);
        }

        if (item.text == "Pheromone Traps") {
            Intent npmdetailact = new Intent(NPMMainActivity.this, SampleDelete.class);
            npmdetailact.putExtra("data", "2149");
            npmdetailact.putExtra("npmtitle", "Pheromone Traps");
            startActivity(npmdetailact);
        }

        if (item.text == "Sticky Traps") {
            Intent npmdetailact = new Intent(NPMMainActivity.this, SampleDelete.class);
            npmdetailact.putExtra("data", "2156");
            npmdetailact.putExtra("npmtitle", "Sticky Traps");
            startActivity(npmdetailact);
        }

        if (item.text == "Light Traps") {
            Intent npmdetailact = new Intent(NPMMainActivity.this, SampleDelete.class);
            npmdetailact.putExtra("data", "2168");
            npmdetailact.putExtra("npmtitle", "Light Traps");
            startActivity(npmdetailact);
        }

        if (item.text == "Solutions") {
            Intent npmdetailact = new Intent(NPMMainActivity.this, SampleDelete.class);
            npmdetailact.putExtra("data", "2128");
            npmdetailact.putExtra("npmtitle", "Solutions");
            startActivity(npmdetailact);
        }

        if (item.text == "Microbial Solutions") {
            Intent npmdetailact = new Intent(NPMMainActivity.this, SampleDelete.class);
            npmdetailact.putExtra("data", "1626");
            npmdetailact.putExtra("npmtitle", "Microbial Solutions");
            startActivity(npmdetailact);
        }

    }

    public JSONObject jsonObjectfromUrl (String url) throws IOException, JSONException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpContext localContext = new BasicHttpContext();

        HttpGet httpGet = new HttpGet(url);

        HttpResponse response = httpClient.execute(httpGet, localContext);
        HttpEntity entity = response.getEntity();
        Object content = EntityUtils.toString(entity);
        JSONObject jsonObj = new JSONObject(content.toString());


//        String value = jsonObj.getString("content"); //String value = jsonObj.getString("one");

        return jsonObj;
    }

    public void loadPostFragment(String catslug, int cat, String query) {
        isPostPage = true;
        if (searchItem != null) {
            searchItem.setVisible(true);
        }
        PostFragment postFragment = new PostFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Bundle bundle = new Bundle();
        bundle.putString("slug", catslug);
        bundle.putInt("cat", cat);
        bundle.putString("query", query);
        postFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.frame, postFragment).commit();
    }

    private void prepareNavMenuData() {
        navListHeader = new ArrayList<>();
        navListChild = new HashMap<>();
        ExpandedMenuModel item = new ExpandedMenuModel();
        item.iconName = getResources().getString(R.string.home);
        item.iconImg = R.drawable.ic_nav_home;
        navListHeader.add(item);
        item = new ExpandedMenuModel();
        item.iconName = getResources().getString(R.string.categories);
        item.iconImg = R.drawable.ic_nav_category;
        navListHeader.add(item);
        categorylist = new ArrayList<>();
        getCategories();
        navListChild.put(navListHeader.get(1), categorylist);
//        item = new ExpandedMenuModel();
//        item.iconName = getResources().getString(R.string.archive);
//        item.iconImg = R.drawable.ic_nav_archive;
//        navListHeader.add(item);
//        item = new ExpandedMenuModel();
//        item.iconName = getResources().getString(R.string.about);
//        item.iconImg = R.drawable.ic_nav_about;
//        navListHeader.add(item);
        item = new ExpandedMenuModel();
        item.iconName = getResources().getString(R.string.npm);
        item.iconImg = R.drawable.ic_nav_archive;
        navListHeader.add(item);
        item = new ExpandedMenuModel();
        item.iconName = getResources().getString(R.string.queryphoto);
        item.iconImg = R.drawable.ic_menu_camera;
        navListHeader.add(item);
        item = new ExpandedMenuModel();
        item.iconName = getResources().getString(R.string.loginlogout);
        item.iconImg = R.drawable.ic_menu_loginlogout;
        navListHeader.add(item);

        //npmlist = new ArrayList<>();
//        npmListChild.add(0,"NPM Basic");

    }

    public void getCategories() {
        if (Util.isNetworkAvailable(getBaseContext())) {
            NetUtil.get("taxonomy/get_taxonomy_index/?taxonomy=product_cat", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        Util.saveData(getBaseContext(), "terms", response.toString());
                        JSONArray categories = response.getJSONArray("terms");
                        for (int i = 0; i < categories.length(); i++) {
                            CategoryModel headeritem = new CategoryModel();
                            headeritem.fromJson(categories.getJSONObject(i));
                            categorylist.add(i, headeritem);
                            navAdapter.updated();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            try {
                JSONObject response = new JSONObject(Util.loadData(getBaseContext(), "terms"));
                JSONArray categories = response.getJSONArray("terms");
                for (int i = 0; i < categories.length(); i++) {
                    CategoryModel headeritem = new CategoryModel();
                    headeritem.fromJson(categories.getJSONObject(i));
                    categorylist.add(i, headeritem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isPostPage) {
                if (isReadyToExit) {
                    super.onBackPressed();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.close_message), Toast.LENGTH_LONG).show();
                    isReadyToExit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isReadyToExit = false;
                        }
                    }, 3000);
                }
            } else {
                searchItem.setVisible(true);
                super.onBackPressed();
            }
        }
    }

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
                loadPostFragment(selectedCatslug,0, "");
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == android.R.id.home) {
//            finish();
//        }
//        if (id == R.id.action_share) {
//            String body = String.format(getResources().getString(R.string.share_body), postModel.title, postModel.url);
//            Util.shareData(MainGridActivity.this, getResources().getString(R.string.share_title), body);
//        }
        if (id == R.id.action_favorite) {

            Intent pIntent = new Intent(NPMMainActivity.this, LoginActivity.class);
            startActivity(pIntent);
        }
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        selectedCatslug = "";
        Intent mIntent = new Intent(NPMMainActivity.this, MainActivity.class);
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



