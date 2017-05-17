package fcih.babyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddNeeds extends AppCompatActivity {
    private static final int GALLARY_REQUEST = 0;
    private ImageView needimage;
    private EditText needname;
    private EditText needdescription;
    private Button addneed;
    private Uri ImageUri = null;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_needs);
        needimage = (ImageView) findViewById(R.id.needimage);
        needname = (EditText) findViewById(R.id.needname);
        needdescription = (EditText) findViewById(R.id.needdescription);
        addneed = (Button) findViewById(R.id.needadd);
        mProgress = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Needs_Table");
        mStorage = FirebaseStorage.getInstance().getReference();
        String baby_id = getIntent().getExtras().getString("BABYID");
        String baby_parent = getIntent().getExtras().getString("BABYPARENT");


        needimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, GALLARY_REQUEST);
            }
        });

        addneed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.setMessage("Posting to Blog ...");
                mProgress.show();
                //  startaddneed();
                FireBaseHelper.Needs needs = new FireBaseHelper.Needs();
                needs.uid = baby_parent;
                needs.childid = baby_id;
                needs.status = "0";
                needs.description = needdescription.getText().toString();
                Uri file = ImageUri;
                StorageReference riversRef = storageRef.child("Needs_Images/" + file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    needs.image = downloadUrl.toString();
                    needs.Add(baby_id);
                    mProgress.dismiss();
                    Intent intent = new Intent(AddNeeds.this, BabyNeeds.class);
                    intent.putExtra("BABYID", baby_id);
                    intent.putExtra("BABYPARENT", baby_parent);
                    startActivity(intent);

                });


            }
        });

        needimage.setImageURI(ImageUri);


    }

    /*
        private void startaddneed() {
            String need_name = needname.getText().toString().trim();
            String need_description = needdescription.getText().toString().trim();
            if (!TextUtils.isEmpty(need_name) && ImageUri != null && !TextUtils.isEmpty(need_description)) {
                mProgress.setMessage("Posting to Blog ...");
                String baby_id=getIntent().getExtras().getString("BABYID");
                String baby_parent=getIntent().getExtras().getString("BABYPARENT");
                mProgress.show();
                StorageReference filepath = mStorage.child("Needs_Images").child(ImageUri.getLastPathSegment());
                filepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri downloadUri = taskSnapshot.getDownloadUrl();
                        final DatabaseReference newPost = mDatabase.child(baby_id).push();
                                newPost.child("name").setValue(need_name);
                                newPost.child("image").setValue(downloadUri.toString());
                                newPost.child("description").setValue(need_description);
                                newPost.child("state").setValue("0");
                        mProgress.dismiss();
                        Intent intent = new Intent(AddNeeds.this,BabyNeeds.class);
                        intent.putExtra("BABYID",baby_id);
                        intent.putExtra("BABYPARENT",baby_parent);
                        startActivity(intent);

                    }
                });








            }

        }
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    ImageUri = data.getData();
                    needimage.setImageURI(ImageUri);
                }
                break;
        }
    }
}
