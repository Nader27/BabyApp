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
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class BabyNeeds extends AppCompatActivity {
    private FloatingActionButton Fab;
    private FirebaseAuth mAuth;
    private Query mquery;
    private Button mNeedbtn;
    private Button mHavebtn;

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        Needs_list.setHasFixedSize(true);
        Needs_list.setLayoutManager(new LinearLayoutManager(this));
        mquery = FireBaseHelper.Needs.Ref.orderByChild(FireBaseHelper.Needs.Table.Childid.text).equalTo(baby_id);
        FirebaseRecyclerAdapter<FireBaseHelper.Needs, NeedsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FireBaseHelper.Needs, NeedsViewHolder>(
                FireBaseHelper.Needs.class,
                R.layout.needs_row,
                NeedsViewHolder.class,
                mquery
        ) {
            @Override
            protected void populateViewHolder(NeedsViewHolder viewHolder, FireBaseHelper.Needs model, int position) {
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
            }
        };

        Needs_list.setAdapter(firebaseRecyclerAdapter);

    }

    public static class NeedsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public NeedsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDescription(String description) {
            TextView need_desc = (TextView) mView.findViewById(R.id.desc_uk);
            need_desc.setText(description);

        }

        public void setImage(Context ctx, String image) {
            final ImageView need_image = (ImageView) mView.findViewById(R.id.image_ku);
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
    }
}
