package fcih.babyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {

    private TextView Next;
    private ImageView Close;
    private ImageView Post_Image;
    private Spinner Post_Spinner;
    private EditText Post_Description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Next = (TextView) findViewById(R.id.Next);
        Close = (ImageView) findViewById(R.id.Close);
        Post_Image = (ImageView) findViewById(R.id.post_image);
        Post_Spinner = (Spinner) findViewById(R.id.post_spinner);
        Post_Description = (EditText) findViewById(R.id.post_description);

        Next.setOnClickListener(v -> {

        });
        Close.setOnClickListener(v -> {

        });
        Post_Image.setImageURI(getIntent().getData());

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        new FireBaseHelper.Children().Where(FireBaseHelper.Children.Table.Parent, UID,Data -> {
            ArrayList<String> ChildrenNames = new ArrayList<>();
            for (FireBaseHelper.Children child: Data) {
                ChildrenNames.add(child.name);
            }
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, ChildrenNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Post_Spinner.setAdapter(spinnerAdapter);
        } );

    }
}
