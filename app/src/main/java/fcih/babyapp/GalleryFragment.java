package fcih.babyapp;


import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    private static final String ARG_PARAM = "USERID";
    private static GalleryFragment fragment;
    final DatabaseReference mDatabaselike = FirebaseDatabase.getInstance().getReference().child("Likes");
    boolean mProcesslike = false;
    private FirebaseAuth mAuth;
    private Query mquery;
    private String UserID;
    private ProgressDialog progressDialog;

    public GalleryFragment() {
        // Required empty public constructor
    }

    public static GalleryFragment newInstance(String uid) {
        if (fragment == null)
            fragment = new GalleryFragment();
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
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        RecyclerView mPost = (RecyclerView) v.findViewById(R.id.Post_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mPost.setHasFixedSize(true);
        mPost.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDatabaselike.keepSynced(true);

        mquery = FireBaseHelper.Posts.Ref.orderByChild(FireBaseHelper.Posts.Table.Uid.text).equalTo(mAuth.getCurrentUser().getUid());

        FirebaseRecyclerAdapter<FireBaseHelper.Posts, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FireBaseHelper.Posts, PostViewHolder>(
                FireBaseHelper.Posts.class,
                R.layout.post_row,
                PostViewHolder.class,
                mquery
        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, FireBaseHelper.Posts model, int position) {
                String post_key = getRef(position).getKey();
                viewHolder.setDescription(model.getDescription());
                viewHolder.setDate(model.getDate());
                viewHolder.setImage(getContext(), model.getImage());
                viewHolder.setLikebtn(post_key);
                viewHolder.mlikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcesslike = true;
                        mDatabaselike.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcesslike) {
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mDatabaselike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcesslike = false;

                                    } else {
                                        mDatabaselike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RendomValue");
                                        mProcesslike = false;

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });

            }


            @Override
            public FireBaseHelper.Posts getItem(int position) {
                return super.getItem(getItemCount() - 1 - position);

            }
        };


        mPost.setAdapter(firebaseRecyclerAdapter);
        return v;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton mlikebtn;
        DatabaseReference mDatabaselike;
        FirebaseAuth mAuth;
        TextView numberoflikes;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mDatabaselike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaselike.keepSynced(true);
            mlikebtn = (ImageButton) mView.findViewById(R.id.likebtn);
            numberoflikes = (TextView) mView.findViewById(R.id.numberoflike);
            mAuth = FirebaseAuth.getInstance();
        }

        public void setDescription(String description) {
            TextView postDescription = (TextView) mView.findViewById(R.id.pt_description);
            postDescription.setText(description);
        }

        public void setDate(String date) {
            TextView postTime = (TextView) mView.findViewById(R.id.pt_time);
            postTime.setText(date);
        }

        public void setImage(final Context ctx, String image) {
            final ImageView postImage = (ImageView) mView.findViewById(R.id.pt_image);
            // Picasso.with(ctx).load(image).into(post_image);
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(postImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(postImage);


                }
            });


        }

        public void setLikebtn(final String post_key) {
            mDatabaselike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (mAuth.getCurrentUser() != null) {

                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                            mlikebtn.setImageResource(R.drawable.like);
                            DatabaseReference postlikes = mDatabaselike.child(post_key);
                            postlikes.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long num = dataSnapshot.getChildrenCount();
                                    int nums = (int) num;
                                    numberoflikes.setText(Integer.toString(nums));

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            mlikebtn.setImageResource(R.drawable.likee);
                            DatabaseReference postlikes = mDatabaselike.child(post_key);
                            postlikes.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long num = dataSnapshot.getChildrenCount();
                                    int nums = (int) num;
                                    numberoflikes.setText(Integer.toString(nums));

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


}

