package fcih.babyapp;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FireBaseHelper.Users CurrentUser;
    private TextView barusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }else {
            mAuthListener = firebaseAuth -> {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();

                }

            };

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
                            .resize(navuserimage.getWidth(), navuserimage.getWidth())
                            .into(navuserimage);
                    Picasso.with(getApplicationContext())
                            .load(user.getPhotoUrl())
                            .resize(baruserimage.getWidth(), baruserimage.getWidth())
                            .into(baruserimage);
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
            getSupportFragmentManager().beginTransaction().replace(R.id.Content, HomeFragment.newInstance()).commit();
        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_notifications) {

        } else if (id == R.id.nav_camera) {
            getSupportFragmentManager().beginTransaction().replace(R.id.Content, CameraFragment.newInstance()).commit();
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
