package fcih.babyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class BabyNeeds extends AppCompatActivity {
    final DatabaseReference mchange = FirebaseDatabase.getInstance().getReference().child("Needs");
    String u_id;
    String parent_id;
    private FloatingActionButton Fab;
    private FirebaseAuth mAuth;
    private Query mquery;
    private Button mNeedbtn;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private Query mquery1;
    private Button mHavebtn;
    private Button getNeed;

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(BabyNeeds.this, BaseActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_needs);
        String baby_id = getIntent().getExtras().getString("BABYID");
        String baby_parent = getIntent().getExtras().getString("BABYPARENT");
        RecyclerView Needs_list = (RecyclerView) findViewById(R.id.neededRecycler);
        RecyclerView Haves_list = (RecyclerView) findViewById(R.id.haveRecycler);
        mNeedbtn = (Button) findViewById(R.id.needbtnn);
        mHavebtn = (Button) findViewById(R.id.havebtnn);
        getNeed = (Button) findViewById(R.id.get_need);


        mNeedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Haves_list.setVisibility(View.GONE);
                Needs_list.setVisibility(View.VISIBLE);

                mNeedbtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mHavebtn.setBackgroundColor(getResources().getColor(R.color.white_text));
                mNeedbtn.setTextColor(getResources().getColor(R.color.white_text));
                mHavebtn.setTextColor(getResources().getColor(R.color.colorPrimary));

            }
        });
        mHavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Needs_list.setVisibility(View.GONE);
                Haves_list.setVisibility(View.VISIBLE);
                mNeedbtn.setTextColor(getResources().getColor(R.color.colorPrimary));
                mHavebtn.setTextColor(getResources().getColor(R.color.white_text));
                mNeedbtn.setBackgroundColor(getResources().getColor(R.color.white_text));
                mHavebtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


            }
        });


        mAuth = FirebaseAuth.getInstance();
        u_id = mAuth.getCurrentUser().getUid();
        Fab = (FloatingActionButton) findViewById(R.id.fab);
        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BabyNeeds.this, AddNeeds.class);
                intent.putExtra("BABYID", baby_id);
                intent.putExtra("BABYPARENT", baby_parent);
                startActivity(intent);


            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase1 = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        Needs_list.setHasFixedSize(true);
        Needs_list.setLayoutManager(new LinearLayoutManager(this));

        Haves_list.setHasFixedSize(true);
        Haves_list.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FireBaseHelper.Needs.Ref.child(baby_id);
        mDatabase1 = FireBaseHelper.Needs.Ref.child(baby_id);
        mquery = mDatabase.orderByChild(FireBaseHelper.Needs.Table.Status.text).equalTo("0");
        mquery1 = mDatabase1.orderByChild(FireBaseHelper.Needs.Table.Status.text).equalTo("1");
        FirebaseRecyclerAdapter<FireBaseHelper.Needs, NeedsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FireBaseHelper.Needs, NeedsViewHolder>(
                FireBaseHelper.Needs.class,
                R.layout.needs_row,
                NeedsViewHolder.class,
                mquery
        ) {
            @Override
            protected void populateViewHolder(NeedsViewHolder viewHolder, FireBaseHelper.Needs model, int position) {
                String need_id = getRef(position).getKey();
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setstate(model.getStatus());
                viewHolder.getNeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference mchangestate = mchange.child(baby_id).child(need_id);
                        mchangestate.child("status").setValue("1");
                    }
                });

            }
        };

        Needs_list.setAdapter(firebaseRecyclerAdapter);

        FirebaseRecyclerAdapter<FireBaseHelper.Needs, NeedsViewHolder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<FireBaseHelper.Needs, NeedsViewHolder>(
                FireBaseHelper.Needs.class,
                R.layout.needs_row,
                NeedsViewHolder.class,
                mquery1


        ) {
            @Override
            protected void populateViewHolder(NeedsViewHolder viewHolder, FireBaseHelper.Needs model, int position) {
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setstate(model.getStatus());
            }
        };
        Haves_list.setAdapter(firebaseRecyclerAdapter1);
    }

    public static class NeedsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button getNeed;
        String baby_parent;
        FirebaseAuth mAuthh;


        public NeedsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            getNeed = (Button) mView.findViewById(R.id.get_need);
            mAuthh = FirebaseAuth.getInstance();

        }

        public void setDescription(String description) {
            TextView need_desc = (TextView) mView.findViewById(R.id.desc_uk);
            need_desc.setText(description);

        }


        public void setImage(Context ctx, String image) {
            //baby_parent = getIntent().getExtras().getString("BABYPARENT");

            final ImageView need_image = (ImageView) mView.findViewById(R.id.image_uk);
            // Picasso.with(ctx).load(image).into(post_image);
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(need_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(need_image);
                }
            });

        }

        public void setstate(String state) {

            if (state.equals("0")) {
                getNeed.setVisibility(View.VISIBLE);
                TextView need_have = (TextView) mView.findViewById(R.id.need_have);
                ImageView face = (ImageView) mView.findViewById(R.id.face_uk);
                face.setImageResource(R.drawable.sadd);
                need_have.setText("need");


            } else {
                TextView need_have = (TextView) mView.findViewById(R.id.need_have);
                ImageView face = (ImageView) mView.findViewById(R.id.face_uk);
                face.setImageResource(R.drawable.happy);
                need_have.setText("Have");
                getNeed.setVisibility(View.INVISIBLE);


            }
        }

    }
}
