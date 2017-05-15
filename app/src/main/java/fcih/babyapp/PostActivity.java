package fcih.babyapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    private TextView Next;
    private ImageView Close;
    private ImageView Post_Image;
    private Spinner Post_Spinner;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private EditText Post_Description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_post);

        Next = (TextView) findViewById(R.id.Next);
        Close = (ImageView) findViewById(R.id.Close);
        Post_Image = (ImageView) findViewById(R.id.post_image);
        Post_Description = (EditText) findViewById(R.id.post_description);
        Post_Image.getLayoutParams().height = Post_Image.getLayoutParams().width;
        findViewById(R.id.frame).getLayoutParams().height = Post_Image.getLayoutParams().width;
        Next.setOnClickListener(v -> {
            FireBaseHelper.Posts post = new FireBaseHelper.Posts();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
            Date date = new Date();
            post.date = dateFormat.format(date);
            post.description = Post_Description.getText().toString();
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            post.uid = UID;
            Uri file = getIntent().getData();
            StorageReference riversRef = storageRef.child("PostImages/" + file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                post.image = downloadUrl.toString();
                post.Add();
            });
        });
        Close.setOnClickListener(v -> {

        });
        Post_Image.setImageURI(getIntent().getData());

    }
}
