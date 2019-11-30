package ir.parishkaar.wordpressjsonclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ir.parishkaar.wordpressjsonclient.adapter.NavExpandableListAdapter;
import ir.parishkaar.wordpressjsonclient.fragment.AboutFragment;
import ir.parishkaar.wordpressjsonclient.fragment.PostFragment;
import ir.parishkaar.wordpressjsonclient.helper.SQLiteHandler;
import ir.parishkaar.wordpressjsonclient.helper.SessionManager;
import ir.parishkaar.wordpressjsonclient.model.CategoryModel;
import ir.parishkaar.wordpressjsonclient.model.ExpandedMenuModel;
import ir.parishkaar.wordpressjsonclient.util.NetUtil;
import ir.parishkaar.wordpressjsonclient.util.Util;


public class FragmentMainActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

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

    ImageView imageviewLeafParent, imageviewStemParent;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }


        // Locate the viewpager in activity_main.xml
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        tabs.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        String myImageURI = intent.getStringExtra("myImageURI");
        String myleaftakenImage = intent.getStringExtra("leaftakenImage");
        String myplantimage = intent.getStringExtra("plantimage");

        Toast.makeText(FragmentMainActivity.this,myplantimage,Toast.LENGTH_SHORT);

//        if(myplantimage == "stem") {
//
//            imageviewLeafParent = (ImageView) findViewById(R.id.imgStem);
//
//            imageviewLeafParent.setImageURI(Uri.parse(myImageURI));
//
//        }
//
//        if(myplantimage == "leaf") {
//
//            imageviewStemParent = (ImageView) findViewById(R.id.imgLeaf);
//
//            imageviewStemParent.setImageURI(Uri.parse(myImageURI));
//
//        }


//        imageviewF1 = (ImageView) findViewById(R.id.imageview);


//        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
//        // it's PagerAdapter set.
//        mSlidingTabLayout = (SlidingTabLayout) viewPager.findViewById(R.id.sliding_tabs);
//        mSlidingTabLayout.setDistributeEvenly(true);
//        mSlidingTabLayout.setViewPager(viewPager);

//        sliding_tabs.setDistributeEvenly(true);


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
                    Intent queryphoto = new Intent(getApplicationContext(),FragmentMainActivity.class);
                    startActivity(queryphoto);

                } else if (groupPosition == 4) {
//                    selectedCat = -1;
//                    loadPostFragment(selectedCatslug,0, "");
                    Intent loginlogout = new Intent(getApplicationContext(), Welcome.class);
                    loginlogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginlogout);
                    finish();

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
                Intent mIntent = new Intent(FragmentMainActivity.this, MainActivity.class);
                mIntent.putExtra("Description", selectedCatslug);
                mIntent.putExtra("searchingquery", "");

                startActivity(mIntent);
                //loadPostFragment(selectedCatslug, 0,"");
                onBackPressed();
                return true;
            }
        });
    }//end of onCreate

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
//        if (id == R.id.action_favorite) {
//
//            Intent pIntent = new Intent(MainGridActivity.this, FragmentMainActivity.class);
//            pIntent.putExtra("formId", "0");
//            startActivity(pIntent);
//        }
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
        Intent mIntent = new Intent(FragmentMainActivity.this, MainActivity.class);
        mIntent.putExtra("Description", selectedCatslug);
        mIntent.putExtra("searchingquery", query);

        startActivity(mIntent);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(FragmentMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
