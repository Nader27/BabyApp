package fcih.babyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddPost extends AppCompatActivity {

    private static final int GALLARY_REQUEST = 0;
    int PLACE_PICKER_REQUEST = 1;
    private ImageView post_image;
    private EditText post_desc;
    private Button submit;
    private Uri imageuri = null;
    private ProgressDialog mProgress;


    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentuser;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        post_image = (ImageView) findViewById(R.id.imageView);
        post_desc = (EditText) findViewById(R.id.editText);
        submit = (Button) findViewById(R.id.button2);
        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mCurrentuser = mAuth.getCurrentUser();

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Posts_Table");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentuser.getUid());


        //Add Post_image
        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, GALLARY_REQUEST);

            }
        });


        //Submit Adding Post
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    private void startPosting() {
        // mProgress.setMessage("Posting to Blog ...");
        final String Post_Desc = post_desc.getText().toString().trim();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        final String formattedDate = df.format(c.getTime());

        if (!TextUtils.isEmpty(Post_Desc) && imageuri != null) {
            mProgress.setMessage("Posting to Blog ...");
            mProgress.show();
            StorageReference filepath = mStorage.child("Posts_Images").child(imageuri.getLastPathSegment());
            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUri = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newPost = mDatabase.push();
                    mDatabaseUser.addValueEventListener(new ValueEventListener() { // to retrive user name
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("description").setValue(Post_Desc);
                            newPost.child("image").setValue(downloadUri.toString());
                            newPost.child("uid").setValue(mCurrentuser.getUid());
                            newPost.child("date").setValue(formattedDate);
                            newPost.child("username").setValue(dataSnapshot.child("username").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgress.dismiss();
                                        Toast.makeText(AddPost.this, "Post Added", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }); //datasnapshot retrive all data


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });


        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    imageuri = data.getData();
                    post_image.setImageURI(imageuri);
                }
                break;
        }
    }
}
