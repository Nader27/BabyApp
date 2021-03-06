package fcih.babyapp;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import me.grantland.widget.AutofitTextView;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private static int RESULT_LOAD_IMAGE = 1;
    private static AccountFragment fragment;
    private TextView mNameview;
    private boolean profilechange = false;
    private String picturePath;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private EditText account_username, account_gender, account_country, account_birthofdate, account_name, account_city;
    private AutofitTextView account_email, account_password;
    private AlertDialog dialog;
    private AlertDialog Edialog;
    private FirebaseAuth mAuth;
    private RoundedImageView account_imageView;
    private BaseActivity activity;
    private ProgressDialog progressDialog;

    public AccountFragment() {
        mAuth = FirebaseAuth.getInstance();
        activity = (BaseActivity) getActivity();
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        mNameview = (TextView) view.findViewById(R.id.nameedit);
        mNameview.setText(mAuth.getCurrentUser().getDisplayName());
        account_imageView = (RoundedImageView) view.findViewById(R.id.userimage);
        activity.findViewById(R.id.tabs).setVisibility(View.GONE);
        activity.findViewById(R.id.toolbar).setVisibility(View.GONE);
        TextView mEmailview = (TextView) view.findViewById(R.id.emailedit);
        mEmailview.setText(mAuth.getCurrentUser().getEmail());
        mEmailview.setEnabled(false);
        TextView mPasswordview = (TextView) view.findViewById(R.id.passwordedit);
        mPasswordview.setEnabled(false);
        RoundedImageView mPictureview = (RoundedImageView) view.findViewById(R.id.userimage);
        Picasso.with(getContext())
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .resize(100, 100)//TODO:Fix later
                .into(mPictureview);
        //view.findViewById(R.id.backimg).setOnClickListener(this);
        view.findViewById(R.id.savetext).setOnClickListener(this);
        view.findViewById(R.id.editpassword).setOnClickListener(this);
        view.findViewById(R.id.editemail).setOnClickListener(this);
        view.findViewById(R.id.Browsebutton).setOnClickListener(this);
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = activity.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            profilechange = true;

            account_imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.backimg:
                //region Back
                //activity.onNavigationItemSelected(activity.navigationView.getMenu().getItem(0));
                //endregion
                //break;
            case R.id.savetext:
                //region Save
                if (profilechange) {
                    Uri file = Uri.parse(picturePath);
                    StorageReference riversRef = storageRef.child("ProfileImage/" + file.getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(file);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(mNameview.getText().toString())
                                .setPhotoUri(downloadUrl)
                                .build();
                        FirebaseUser user = mAuth.getCurrentUser();
                        user.updateProfile(profileUpdates);
                        FireBaseHelper.Users USER = new FireBaseHelper.Users();
                        USER.Findbykey(user.getUid(), Data -> {
                            Data.name = mNameview.getText().toString();
                            Data.image = downloadUrl.toString();
                            Data.Update(Data.Key);
                           // activity.onNavigationItemSelected(activity.navigationView.getMenu().getItem(0));
                        });
                    });
                } else {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(mNameview.getText().toString())
                            .build();
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.updateProfile(profileUpdates);
                    FireBaseHelper.Users USER = new FireBaseHelper.Users();
                    USER.Findbykey(user.getUid(), Data -> {
                        Data.name = mNameview.getText().toString();
                        Data.Update(Data.Key);
                       // activity.onNavigationItemSelected(activity.navigationView.getMenu().getItem(0));
                    });
                }
                //endregion
                break;
            case R.id.editpassword:
                //region password form
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
                View mView = activity.getLayoutInflater().inflate(R.layout.editpassword, null);
                Button mButton = (Button) mView.findViewById(R.id.doeditpassword);
                mButton.setOnClickListener(this);
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
                //endregion
                break;
            case R.id.editemail:
                //region email form
                AlertDialog.Builder mEBuilder = new AlertDialog.Builder(activity);
                View mEView = activity.getLayoutInflater().inflate(R.layout.editemail, null);
                Button mEButton = (Button) mEView.findViewById(R.id.doeditemail);
                mEButton.setOnClickListener(this);
                mEBuilder.setView(mEView);
                Edialog = mEBuilder.create();
                Edialog.show();
                //endregion
                break;
            case R.id.doeditpassword:
                //region change password
                EditText mpassword = (EditText) dialog.findViewById(R.id.password1);
                EditText mrepassword = (EditText) dialog.findViewById(R.id.repassword1);
                if (!mpassword.getText().toString().isEmpty() && !mrepassword.getText().toString().isEmpty()) {
                    if (mpassword.getText().toString().equals(mrepassword.getText().toString())) {
                        mAuth.getCurrentUser().updatePassword(mpassword.getText().toString()).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(),
                                        "Password Changed Successfully",
                                        Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            } else
                                Toast.makeText(getContext(),
                                        task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                        });
                    } else
                        Toast.makeText(getContext(),
                                "Password Not Matched",
                                Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(),
                            "Password Field is Empty",
                            Toast.LENGTH_LONG).show();
                //endregion
                break;
            case R.id.doeditemail:
                //region Change Email
                final EditText memail = (EditText) Edialog.findViewById(R.id.email1);
                EditText mreemail = (EditText) Edialog.findViewById(R.id.reemail1);
                if (!memail.getText().toString().isEmpty() && !mreemail.getText().toString().isEmpty()) {
                    if (memail.getText().toString().equals(mreemail.getText().toString())) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        user.updateEmail(memail.getText().toString()).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                FireBaseHelper.Users USER = new FireBaseHelper.Users();
                                USER.Findbykey(user.getUid(), Data -> {
                                    Data.email = memail.getText().toString();
                                    Data.Update(Data.Key);
                                    Toast.makeText(getContext(),
                                            "Email Changed Successfully",
                                            Toast.LENGTH_LONG).show();
                                    Edialog.dismiss();
                                });
                            }else
                                Toast.makeText(getContext(),
                                        task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                        });


                    } else
                        Toast.makeText(getContext(),
                                "Email Not Matched",
                                Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(),
                            "Email Field is Empty",
                            Toast.LENGTH_LONG).show();
                //endregion
                break;
            case R.id.Browsebutton:
                //region Browse Button
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
                //endregion
                break;

        }
    }
}