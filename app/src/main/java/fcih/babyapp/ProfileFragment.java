package fcih.babyapp;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int GALLARY_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    FirebaseAuth mAuth;
    FirebaseListAdapter<FireBaseHelper.Children> mAdapter = null;
    private Uri imageuri = null;
    private StorageReference mStorage;
    private AlertDialog dialog;


    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        Button add_papy_main = (Button) v.findViewById(R.id.Add_button);
        add_papy_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog(null);
            }
        });
        ListView list = (ListView) v.findViewById(R.id.Main_List);
        Query mQuery = FireBaseHelper.Children.Ref.orderByChild(FireBaseHelper.Children.Table.Parent.text).equalTo(mAuth.getCurrentUser().getUid());
        mAdapter = new FirebaseListAdapter<FireBaseHelper.Children>(getActivity(), FireBaseHelper.Children.class
                , android.R.layout.simple_list_item_1, mQuery) {
            @Override
            protected void populateView(View v, FireBaseHelper.Children model, int position) {
                TextView text_name = (TextView) v.findViewById(android.R.id.text1);
                text_name.setText(model.name);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        model.Findbykey(mAdapter.getRef(position).getKey(), Data -> ShowDialog(Data));
                    }
                });

            }
        };
        list.setAdapter(mAdapter);
        return v;
    }


    private void ShowDialog(FireBaseHelper.Children updatetour) {
        AlertDialog.Builder mEBuilder = new AlertDialog.Builder(getActivity());
        View mEView = getActivity().getLayoutInflater().inflate(R.layout.addbaby, null);
        final EditText baby_name = (EditText) mEView.findViewById(R.id.babyname);
        final TextView baby_birth = (TextView) mEView.findViewById(R.id.babybirth);
        final EditText baby_gender = (EditText) mEView.findViewById(R.id.babygender);
        Button browse = (Button) mEView.findViewById(R.id.browse_button);
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        //   baby_image = (ImageView)mEView.findViewById(R.id.babyimage);
        Button baby_add = (Button) mEView.findViewById(R.id.babyadd);

        Button baby_delete = (Button) mEView.findViewById(R.id.babydelete);
        baby_birth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int year = mcurrentTime.get(Calendar.YEAR);
                int month = mcurrentTime.get(Calendar.MONTH);
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        baby_birth.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, year, month, day);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();

            }
        });


        baby_add.setOnClickListener(v -> {

            FireBaseHelper.Children children = new FireBaseHelper.Children();
            String u_id = mAuth.getCurrentUser().getUid();
            children.name = baby_name.getText().toString();
            children.birth = baby_birth.getText().toString();
            children.gender = baby_gender.getText().toString();
            children.parent = u_id;
            children.image = "";
            //children.image= imageuri.toString();

            children.Add();

            dialog.dismiss();


        });
        mEBuilder.setView(mEView);
        dialog = mEBuilder.create();
        dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Uri file = Uri.parse(picturePath);
            StorageReference riversRef = mStorage.child("Baby_Images/" + file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //    mimage.setText(downloadUrl.toString());
                    imageuri = downloadUrl;

                }
            });

        }


    }
}
