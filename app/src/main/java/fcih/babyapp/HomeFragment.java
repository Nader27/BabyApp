package fcih.babyapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM = "USERID";
    private static HomeFragment fragment;
    public ProfileFragment profile;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String UserID;
    private ProgressDialog progressDialog;


    public HomeFragment() {
    }

    public static HomeFragment newInstance(String uid) {
        if (fragment == null)
            fragment = new HomeFragment();
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
        if (savedInstanceState == null) {
            View view = inflater.inflate(R.layout.fragment_home, container, false);
            getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
            ViewPager mViewPager = (ViewPager) view.findViewById(R.id.container);
            mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
            // Set up the ViewPager with the sections adapter.
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(1);
            TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
            tabLayout.setVisibility(View.VISIBLE);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setupWithViewPager(mViewPager);
            return view;
        }
        return null;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return AboutFragment.newInstance(UserID);
                case 1:
                    return GalleryFragment.newInstance(UserID);
                case 2:
                    profile = ProfileFragment.newInstance(UserID);
                    return profile;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "About";
                case 1:
                    return "Gallery";
                case 2:
                    return "Children";
            }
            return null;
        }
    }
}
