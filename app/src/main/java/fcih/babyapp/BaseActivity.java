package fcih.babyapp;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String USERIDKEY = "useridkey";
    private static boolean WAITINGFORIMAGE = false;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FireBaseHelper.Users CurrentUser;
    private TextView barusername;
    private List<String> Usernames;
    private String UserID;
    private HomeFragment home;
    private Menu MENU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        } else {
            mAuthListener = firebaseAuth -> {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();

                }

            };
            Usernames = new ArrayList<>();
            new FireBaseHelper.Users().Tolist(Data -> {
                for (FireBaseHelper.Users user : Data) {
                    Usernames.add(user.username);
                }
            });
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            UserID = mAuth.getCurrentUser().getUid();
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_menu, menu);

        MENU = menu;
        SearchSetup(menu);

        UserSetup();

        return true;
    }

    public void UserSetup() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        RoundedImageView navuserimage = (RoundedImageView) findViewById(R.id.nav_user_image);
        TextView navusername = (TextView) findViewById(R.id.nav_user_name);
        TextView navuserhome = (TextView) findViewById(R.id.nav_user_home);
        RoundedImageView baruserimage = (RoundedImageView) findViewById(R.id.toolbar_user_image);
        barusername = (TextView) findViewById(R.id.toolbar_user_name);
        TextView barfullusername = (TextView) findViewById(R.id.toolbar_full_user_name);


        if (user != null) {
            new FireBaseHelper.Users().Findbykey(mAuth.getCurrentUser().getUid(), Data -> {
                CurrentUser = Data;

                if (!Data.image.isEmpty()) {
                    Picasso.with(getApplicationContext())
                            .load(user.getPhotoUrl())
                            .fit()
                            .into(navuserimage);
                }
                navusername.setText(user.getDisplayName());
                barfullusername.setText(user.getDisplayName());
                barusername.setText(CurrentUser.username);
                if (!CurrentUser.city.isEmpty() && !CurrentUser.city.isEmpty()) {
                    navuserhome.setText(CurrentUser.city + "," + CurrentUser.country);
                } else
                    navuserhome.setText(CurrentUser.city + CurrentUser.country);
            });
        }
    }

    public void SearchSetup(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, BaseActivity.class)
        ));
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                barusername.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                barusername.setVisibility(View.VISIBLE);
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new FireBaseHelper.Users().Where(FireBaseHelper.Users.Table.Username, query, Data -> {
                    if (Data.size() == 0) {
                        Toast.makeText(getApplicationContext(), "User Not Found", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(BaseActivity.this, SearchActivity.class);
                        intent.putExtra(USERIDKEY, Data.get(0).Key);
                        startActivity(intent);
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //loadHistory(newText);
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // History
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadHistory(String query) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            // Cursor
            String[] columns = new String[]{"_id", "text"};
            Object[] temp = new Object[]{0, "default"};

            MatrixCursor cursor = new MatrixCursor(columns);

            for (int i = 0; i < Usernames.size(); i++) {

                temp[0] = i;
                temp[1] = Usernames.get(i);

                cursor.addRow(temp);

            }

            // SearchView
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            final SearchView search = (SearchView) MENU.findItem(R.id.action_search).getActionView();

            search.setSuggestionsAdapter(new SearchAdapter(this, cursor, Usernames));

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK && WAITINGFORIMAGE) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setFixAspectRatio(true)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && WAITINGFORIMAGE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Intent intent = new Intent(BaseActivity.this, PostActivity.class);
                intent.setData(resultUri);
                startActivity(intent);
                WAITINGFORIMAGE = false;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getApplicationContext(), result.getError().getMessage(), Toast.LENGTH_LONG).show();
                WAITINGFORIMAGE = false;
            }
        } else if (requestCode == 0) {
            WAITINGFORIMAGE = false;
        } else {
            ProfileFragment fragment = home.profile;
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            home = HomeFragment.newInstance(UserID);
            getSupportFragmentManager().beginTransaction().replace(R.id.Content, home).commit();
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(BaseActivity.this, AccountActivity.class));
        } else if (id == R.id.nav_notifications) {

        } else if (id == R.id.nav_camera) {
            CropImage.startPickImageActivity(this);
            WAITINGFORIMAGE = true;
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
