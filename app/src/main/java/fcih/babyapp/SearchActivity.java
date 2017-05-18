package fcih.babyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String USERIDKEY = "useridkey";
    private List<String> Usernames;
    private String UserID;
    private HomeFragment home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Usernames = new ArrayList<>();
        new FireBaseHelper.Users().Tolist(Data -> {
            for (FireBaseHelper.Users user : Data) {
                Usernames.add(user.username);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserID = getIntent().getStringExtra(USERIDKEY);
        home = HomeFragment.newInstance(UserID);
        getSupportFragmentManager().beginTransaction().replace(R.id.Content, home).commit();
    }
}
