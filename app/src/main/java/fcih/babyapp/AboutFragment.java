package fcih.babyapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import me.grantland.widget.AutofitTextView;


public class AboutFragment extends Fragment {
    private static final String ARG_PARAM = "USERID";
    private static AboutFragment fragment;
    private AutofitTextView about_username, about_email, about_gender, about_child_num, about_country, about_birthofdate, about_name, about_city;
    private ProgressDialog progressDialog;
    private String UserID;

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance(String uid) {
        if (fragment == null)
            fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UserID = getArguments().getString(ARG_PARAM);
        }
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("loading");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressDialog.show();
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        about_name = (AutofitTextView) v.findViewById(R.id.nameabout);
        about_email = (AutofitTextView) v.findViewById(R.id.emailabout);
        about_gender = (AutofitTextView) v.findViewById(R.id.genderabout);
        about_child_num = (AutofitTextView) v.findViewById(R.id.childnumabout);
        about_country = (AutofitTextView) v.findViewById(R.id.countryabout);
        about_birthofdate = (AutofitTextView) v.findViewById(R.id.birthabout);
        about_username = (AutofitTextView) v.findViewById(R.id.usernameabout);
        about_city = (AutofitTextView) v.findViewById(R.id.cityabout);


        new FireBaseHelper.Users().Findbykey(UserID, Data -> {
            about_name.setText(Data.name);
            about_email.setText(Data.email);
            about_country.setText(Data.country);
            about_birthofdate.setText(Data.birth);
            about_gender.setText(Data.gender);
            about_username.setText(Data.username);
            about_city.setText(Data.city);
            about_child_num.setText(String.format(Locale.ENGLISH, "%d", Data.childrens.size()));
            progressDialog.dismiss();
        });


        return v;
    }

}
