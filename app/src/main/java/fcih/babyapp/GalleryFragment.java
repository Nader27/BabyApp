package fcih.babyapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    private static final String ARG_PARAM = "USERID";
    final DatabaseReference mDatabaselike = FirebaseDatabase.getInstance().getReference().child("Likes");
    private Query mquery;
    private String UserID;
    private ProgressDialog progressDialog;
    private FirebaseRecyclerAdapter<FireBaseHelper.Posts, PostViewHolder> mAdapter;

    public GalleryFragment() {
        // Required empty public constructor
    }

    public static GalleryFragment newInstance(String uid) {
        GalleryFragment fragment = new GalleryFragment();
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
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        RecyclerView mPost = (RecyclerView) v.findViewById(R.id.Post_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mPost.setHasFixedSize(true);
        mPost.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDatabaselike.keepSynced(true);

        mquery = FireBaseHelper.Posts.Ref.orderByChild(FireBaseHelper.Posts.Table.Uid.text).equalTo(UserID);

        mAdapter = new FirebaseRecyclerAdapter<FireBaseHelper.Posts, PostViewHolder>(
                FireBaseHelper.Posts.class, R.layout.post_row, PostViewHolder.class, mquery) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, FireBaseHelper.Posts model, int position) {
                new FireBaseHelper.Posts().Findbykey(mAdapter.getRef(position).getKey(), Data -> {
                    viewHolder.postDescription.setText(Data.description);
                    viewHolder.postTime.setText(model.date);
                    Picasso.with(getContext()).load(model.image).fit().into(viewHolder.postImage);
                    new FireBaseHelper.Likes().Where(FireBaseHelper.Likes.Table.Postid, Data.Key, Data1 -> {
                        viewHolder.numberoflikes.setText(String.format(Locale.ENGLISH, "%d", Data1.size()));
                        for (int i = 0; i < Data1.size(); i++) {
                            FireBaseHelper.Likes like = Data1.get(i);
                            if (like.uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                viewHolder.mlikebtn.setImageResource(R.drawable.like);
                                viewHolder.mlikebtn.setOnClickListener(v1 -> {
                                    new FireBaseHelper.Likes().Remove(like.Key);
                                    mAdapter.notifyDataSetChanged();
                                });
                                break;
                            } else if (i == Data1.size() - 1) {
                                viewHolder.mlikebtn.setImageResource(R.drawable.likee);
                                viewHolder.mlikebtn.setOnClickListener(v1 -> {
                                    FireBaseHelper.Likes l = new FireBaseHelper.Likes();
                                    l.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    l.postid = Data.Key;
                                    l.Add();
                                    mAdapter.notifyDataSetChanged();
                                });
                            }
                        }
                        if (Data1.size() == 0) {
                            viewHolder.mlikebtn.setImageResource(R.drawable.likee);
                            viewHolder.mlikebtn.setOnClickListener(v1 -> {
                                FireBaseHelper.Likes l = new FireBaseHelper.Likes();
                                l.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                l.postid = Data.Key;
                                l.Add();
                                mAdapter.notifyDataSetChanged();
                            });
                        }
                    });
                });
            }
        };
        mPost.setAdapter(mAdapter);
        return v;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView postTime;
        public ImageView postImage;
        public View mView;
        public ImageButton mlikebtn;
        public TextView numberoflikes;
        public TextView postDescription;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            postDescription = (TextView) mView.findViewById(R.id.pt_description);
            postTime = (TextView) mView.findViewById(R.id.pt_time);
            mlikebtn = (ImageButton) mView.findViewById(R.id.likebtn);
            numberoflikes = (TextView) mView.findViewById(R.id.numberoflike);
            postImage = (ImageView) mView.findViewById(R.id.pt_image);
        }
    }
}

