package fcih.babyapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class AboutFragment extends Fragment {
    private TextView about_username, about_email, about_gender, about_child_num, about_country, about_birthofdate;
    private FirebaseAuth mAuth;

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        about_username = (TextView) v.findViewById(R.id.nameabout);
        about_email=(TextView)v.findViewById(R.id.emailabout);
        about_gender=(TextView)v.findViewById(R.id.genderabout);
        about_child_num=(TextView)v.findViewById(R.id.childnumabout);
        about_country=(TextView)v.findViewById(R.id.countryabout);
        about_birthofdate=(TextView)v.findViewById(R.id.birthabout);


        mAuth=FirebaseAuth.getInstance();
        String uid=mAuth.getCurrentUser().getUid();

        new FireBaseHelper.Users().Findbykey(uid, Data -> {
            about_username.setText(Data.name);
            about_email.setText(Data.email);
            about_country.setText(Data.country);
            about_birthofdate.setText(Data.birth);
            about_gender.setText(Data.gender);
        });


        return v;
    }

}
