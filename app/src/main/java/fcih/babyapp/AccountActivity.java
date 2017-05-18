package fcih.babyapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String genderArray[] = {"Male", "Female"};
    private static boolean WAITINGFORIMAGE = false;
    private ImageView account_user_image;
    private TextView account_email;
    private TextView account_password;
    private EditText account_username;
    private EditText account_fullname;
    private EditText account_city;
    private EditText account_country;
    private TextView account_birthdate;
    private Spinner account_gender;
    private Uri imageUri;
    private Uri ImageURI;
    private AlertDialog dialog;
    private FirebaseAuth mAuth;
    private AlertDialog Edialog;
    private ProgressDialog progressDialog;
    private FirebaseStorage mStorage;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        progressDialog = new ProgressDialog(AccountActivity.this);
        progressDialog.setMessage("loading");
        ImageURI = null;
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        account_user_image = (ImageView) findViewById(R.id.toolbar_user_image);
        account_email = (TextView) findViewById(R.id.email_account);
        account_password = (TextView) findViewById(R.id.password_account);
        account_username = (EditText) findViewById(R.id.username_account);
        account_fullname = (EditText) findViewById(R.id.fullname_account);
        account_city = (EditText) findViewById(R.id.city_account);
        account_country = (EditText) findViewById(R.id.country_account);
        account_birthdate = (TextView) findViewById(R.id.birthday_account);
        account_gender = (Spinner) findViewById(R.id.gender_account);

        account_user_image.setOnClickListener(this);
        account_email.setOnClickListener(this);
        account_password.setOnClickListener(this);
        account_birthdate.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.account_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, genderArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account_gender.setAdapter(spinnerArrayAdapter);
        account_gender.setPrompt("Select Gender");

        progressDialog.show();
        new FireBaseHelper.Users().Findbykey(mAuth.getCurrentUser().getUid(), Data -> {
            account_email.setText(Data.email);
            account_username.setText(Data.username);
            account_country.setText(Data.country);
            account_fullname.setText(Data.name);
            account_birthdate.setText(Data.birth);
            account_gender.setSelection(Data.gender == genderArray[0] ? 0 : 1);
            account_city.setText(Data.city);
            if (!Data.image.isEmpty()) {
                Picasso.with(getApplicationContext()).load(Data.image).fit().into(account_user_image);
            }
            progressDialog.dismiss();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            try {
                FireBaseHelper.Users users = new FireBaseHelper.Users();
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                final Date date = df.parse(account_birthdate.getText().toString());
                if (!isFullnameValid(account_fullname)) ;
                if (!isUsernameValid(account_username)) ;
                if (!isCityorCountryValid(account_city)) ;
                if (!isCityorCountryValid(account_country)) ;
                else if (date == null) ;
                else {
                    progressDialog.show();
                    new FireBaseHelper.Users().Where(FireBaseHelper.Users.Table.Username, account_username.getText().toString(), Data1 -> {
                        if (Data1.size() == 0 || (Data1.size() == 1 && Data1.get(0).Key.equals(mAuth.getCurrentUser().getUid()))) {
                            users.Findbykey(mAuth.getCurrentUser().getUid(), Data -> {
                                Data.name = account_fullname.getText().toString();
                                Data.username = account_username.getText().toString();
                                Data.city = account_city.getText().toString();
                                Data.country = account_country.getText().toString();
                                Data.birth = account_birthdate.getText().toString();
                                Data.gender = account_gender.getSelectedItem().toString();
                                if (ImageURI != null) {
                                    Uri file = ImageURI;
                                    StorageReference riversRef = mStorage.getReference("ProfileImage/" + file.getLastPathSegment());
                                    UploadTask uploadTask = riversRef.putFile(file);
                                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                                        @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        Data.image = downloadUrl.toString();
                                        Data.Update();
                                        mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setPhotoUri(downloadUrl).setDisplayName(Data.username).build());
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(AccountActivity.this, BaseActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    });
                                } else {
                                    Data.Update();
                                    mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(Data.username).build());
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(AccountActivity.this, BaseActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            account_username.setError("Username Already used");
                            account_username.requestFocus();
                        }
                    });
                }
            } catch (ParseException e) {
                account_birthdate.setError("Birth Required");
                account_birthdate.requestFocus();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setFixAspectRatio(true)
                        .start(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setFixAspectRatio(true)
                        .start(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    //region Validation

    private boolean isUsernameValid(EditText usernameview) {
        usernameview.setError(null);
        String username = usernameview.getText().toString();
        if (TextUtils.isEmpty(username)) {
            usernameview.setError(getString(R.string.error_field_required));
        } else if (username.length() < 6) {
            usernameview.setError(getString(R.string.error_invalid_username));
        } else if (!username.matches("^[a-zA-Z0-9]+$")) {
            usernameview.setError(getString(R.string.error_invalid_username));
        } else return true;
        usernameview.requestFocus();
        return false;
    }

    private boolean isFullnameValid(EditText fullnameview) {
        fullnameview.setError(null);
        String fullname = fullnameview.getText().toString();
        if (TextUtils.isEmpty(fullname)) {
            fullnameview.setError(getString(R.string.error_field_required));
        } else if (fullname.length() < 6) {
            fullnameview.setError(getString(R.string.error_invalid_fullname));
        } else if (!fullname.matches("^[a-zA-Z- ]+$")) {
            fullnameview.setError(getString(R.string.error_invalid_fullname));
        } else return true;
        fullnameview.requestFocus();
        return false;
    }

    private boolean isCityorCountryValid(EditText citynameview) {
        citynameview.setError(null);
        String city = citynameview.getText().toString();
        if (TextUtils.isEmpty(city)) {
            citynameview.setError(getString(R.string.error_field_required));
        } else if (city.length() < 2) {
            citynameview.setError(getString(R.string.error_invalid_cityorcountry));
        } else if (!city.matches("^[a-zA-Z- ]+$")) {
            citynameview.setError(getString(R.string.error_invalid_cityorcountry));
        } else return true;
        citynameview.requestFocus();
        return false;
    }

    //endregion

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK && WAITINGFORIMAGE) {
            imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setFixAspectRatio(true)
                        .start(this);
                //baby_image.setImageURI(imageUri);
                ImageURI = imageUri;
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && WAITINGFORIMAGE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                account_user_image.setImageURI(resultUri);
                ImageURI = resultUri;
                WAITINGFORIMAGE = false;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_LONG).show();
                WAITINGFORIMAGE = false;
            }
        } else if (requestCode == 0) {
            WAITINGFORIMAGE = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_user_image:
                if (CropImage.isExplicitCameraPermissionRequired(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    WAITINGFORIMAGE = true;
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                } else {
                    WAITINGFORIMAGE = true;
                    CropImage.startPickImageActivity(this);
                }
                break;
            case R.id.email_account:
                //region email form
                AlertDialog.Builder mEBuilder = new AlertDialog.Builder(this);
                View mEView = getLayoutInflater().inflate(R.layout.editemail, null);
                Button mEButton = (Button) mEView.findViewById(R.id.doeditemail);
                mEButton.setOnClickListener(this);
                mEBuilder.setView(mEView);
                Edialog = mEBuilder.create();
                Edialog.show();
                //endregion
                break;
            case R.id.password_account:
                //region password form
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                View mView = getLayoutInflater().inflate(R.layout.editpassword, null);
                Button mButton = (Button) mView.findViewById(R.id.doeditpassword);
                mButton.setOnClickListener(this);
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
                //endregion
                break;
            case R.id.birthday_account:
                Calendar mcurrentTime = Calendar.getInstance();
                int year = mcurrentTime.get(Calendar.YEAR);
                int month = mcurrentTime.get(Calendar.MONTH);
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> account_birthdate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
                break;
            case R.id.doeditpassword:
                //region change password
                EditText mpassword = (EditText) dialog.findViewById(R.id.password1);
                EditText mrepassword = (EditText) dialog.findViewById(R.id.repassword1);
                if (!mpassword.getText().toString().isEmpty() && !mrepassword.getText().toString().isEmpty()) {
                    if (mpassword.getText().toString().equals(mrepassword.getText().toString())) {
                        mAuth.getCurrentUser().updatePassword(mpassword.getText().toString()).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AccountActivity.this, "Password Changed Successfully", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            } else
                                Toast.makeText(AccountActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        });
                    } else
                        Toast.makeText(AccountActivity.this, "Password Not Matched", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(AccountActivity.this, "Password Field is Empty", Toast.LENGTH_LONG).show();
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
                            if (task.isSuccessful()) {
                                FireBaseHelper.Users USER = new FireBaseHelper.Users();
                                USER.Findbykey(user.getUid(), Data -> {
                                    Data.email = memail.getText().toString();
                                    Data.Update(Data.Key);
                                    Toast.makeText(AccountActivity.this,
                                            "Email Changed Successfully",
                                            Toast.LENGTH_LONG).show();
                                    Edialog.dismiss();
                                });
                            } else
                                Toast.makeText(AccountActivity.this,
                                        task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                        });


                    } else
                        Toast.makeText(AccountActivity.this, "Email Not Matched", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(AccountActivity.this, "Email Field is Empty", Toast.LENGTH_LONG).show();
                //endregion
                break;
        }
    }
}
