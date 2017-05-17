package fcih.babyapp;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String genderArray[] = {"male", "female"};
    private static final String ARG_PARAM = "USERID";
    private static boolean WAITINGFORIMAGE = false;
    private static String UPDATE = null;
    private static ProfileFragment fragment;
    FirebaseRecyclerAdapter<FireBaseHelper.Children, babyviewholder> mAdapter = null;
    private StorageReference mStorage;
    private RecyclerView recyclerView;
    private Button Addbaby;
    private LinearLayout addbabyform;
    private RoundedImageView baby_image;
    private EditText baby_name;
    private TextView baby_birth;
    private Spinner baby_gender;
    private Button baby_add_button;
    private Button baby_cancel_button;
    private ProgressDialog progressDialog;
    private String UserID;
    private Uri ImageURI;
    private Uri imageUri;
    private Context context;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String uid) {
        if (fragment == null)
            fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, uid);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Show hidden view by animation
     *
     * @param view The View You Want Show
     */
    public static void expand(final View view) {
        view.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        animation.setDuration((int) (targetHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(animation);
    }

    /**
     * Hide view by animation
     *
     * @param view The View You Want Hide
     */
    public static void collapse(final View view) {
        final int initialHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        animation.setDuration((int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(animation);
    }

    /**
     * Get a diff between two dates
     *
     * @param date1    the oldest date
     * @param date2    the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorage = FirebaseStorage.getInstance().getReference();
        if (getArguments() != null) {
            UserID = getArguments().getString(ARG_PARAM);
        }
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("loading");
        ImageURI = null;
    }

    private boolean isFullnameValid(EditText fullnameview) {
        fullnameview.setError(null);
        String fullname = fullnameview.getText().toString();
        if (TextUtils.isEmpty(fullname)) {
            fullnameview.setError(getString(R.string.error_field_required));
        } else if (fullname.length() < 3) {
            fullnameview.setError(getString(R.string.error_invalid_fullname));
        } else if (!fullname.matches("^[a-zA-Z- ]+$")) {
            fullnameview.setError(getString(R.string.error_invalid_fullname));
        } else return true;
        fullnameview.requestFocus();
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        Addbaby = (Button) v.findViewById(R.id.add_baby);
        addbabyform = (LinearLayout) v.findViewById(R.id.addbabyform);
        baby_image = (RoundedImageView) v.findViewById(R.id.baby_add_image);
        baby_name = (EditText) v.findViewById(R.id.baby_add_name);
        baby_birth = (TextView) v.findViewById(R.id.baby_add_birth);
        baby_gender = (Spinner) v.findViewById(R.id.baby_add_gender);
        baby_add_button = (Button) v.findViewById(R.id.add_baby_btn);
        baby_cancel_button = (Button) v.findViewById(R.id.Cancel);
        context = getContext();

        progressDialog.show();
        baby_image.setOnClickListener(v1 -> {
            if (CropImage.isExplicitCameraPermissionRequired(context)) {
                WAITINGFORIMAGE = true;
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
            } else {
                WAITINGFORIMAGE = true;
                CropImage.startPickImageActivity(getActivity());
            }
        });

        if (UserID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            Addbaby.setOnClickListener(v1 -> {
                expand(addbabyform);
                collapse(recyclerView);
                collapse(Addbaby);
            });
        } else Addbaby.setVisibility(View.GONE);

        baby_cancel_button.setOnClickListener(v1 -> {
            collapse(addbabyform);
            expand(recyclerView);
            expand(Addbaby);
            ImageURI = null;
            UPDATE = null;
            baby_name.setText("");
            baby_birth.setText("Select Date");
            baby_add_button.setText("Add baby");
        });

        baby_add_button.setOnClickListener(v1 -> {
            try {
                FireBaseHelper.Children children = new FireBaseHelper.Children();
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                final Date date = df.parse(baby_birth.getText().toString());
                if (!isFullnameValid(baby_name)) ;
                else if (date == null) ;
                else if (UPDATE != null) {
                    progressDialog.show();
                    children.Findbykey(UPDATE, Data -> {
                        Data.name = baby_name.getText().toString();
                        Data.birth = baby_birth.getText().toString();
                        Data.gender = baby_gender.getSelectedItem().toString();
                        if (ImageURI != null) {
                            Uri file = ImageURI;
                            StorageReference riversRef = mStorage.child("PostImages/" + file.getLastPathSegment());
                            UploadTask uploadTask = riversRef.putFile(file);
                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Data.image = downloadUrl.toString();
                                Data.Update();
                                collapse(addbabyform);
                                expand(recyclerView);
                                expand(Addbaby);
                                mAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            });
                        } else {
                            Data.Update();
                            collapse(addbabyform);
                            expand(recyclerView);
                            expand(Addbaby);
                            progressDialog.dismiss();
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    progressDialog.show();
                    children.name = baby_name.getText().toString();
                    children.birth = baby_birth.getText().toString();
                    children.gender = baby_gender.getSelectedItem().toString();
                    children.parent = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (ImageURI != null) {
                        Uri file = ImageURI;
                        StorageReference riversRef = mStorage.child("ChildrenImages/" + file.getLastPathSegment());
                        UploadTask uploadTask = riversRef.putFile(file);
                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                            @SuppressWarnings("VisibleForTests") final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            children.image = downloadUrl.toString();
                            children.Add();
                            mAdapter.notifyDataSetChanged();
                            collapse(addbabyform);
                            expand(recyclerView);
                            expand(Addbaby);
                            progressDialog.dismiss();
                        });
                    } else {
                        children.image = "";
                        children.Add();
                        collapse(addbabyform);
                        expand(recyclerView);
                        expand(Addbaby);
                        progressDialog.dismiss();
                        mAdapter.notifyDataSetChanged();
                    }
                }
            } catch (ParseException e) {
                baby_birth.setError("Birth Required");
                baby_birth.requestFocus();
            }
        });

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, genderArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        baby_gender.setAdapter(spinnerArrayAdapter);
        baby_gender.setPrompt("Select Gender");

        baby_birth.setOnClickListener(v1 -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int year = mcurrentTime.get(Calendar.YEAR);
            int month = mcurrentTime.get(Calendar.MONTH);
            int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mDatePicker;
            mDatePicker = new DatePickerDialog(getActivity(), (view, year1, month1, dayOfMonth) -> baby_birth.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
            mDatePicker.setTitle("Select Date");
            mDatePicker.show();
        });

        Query mQuery = FireBaseHelper.Children.Ref.orderByChild(FireBaseHelper.Children.Table.Parent.text).equalTo(UserID);
        mAdapter = new FirebaseRecyclerAdapter<FireBaseHelper.Children, babyviewholder>(
                FireBaseHelper.Children.class, R.layout.child_item, babyviewholder.class, mQuery) {
            @Override
            protected void populateViewHolder(babyviewholder viewHolder, FireBaseHelper.Children model, int position) {
                model.Findbykey(mAdapter.getRef(position).getKey(), Data -> {
                    viewHolder.mnameView.setText(Data.name);
                    viewHolder.mgenderView.setText(Data.gender);
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    try {
                        Date date = df.parse(Data.birth);
                        int diff = (int) getDateDiff(date, new Date(), TimeUnit.DAYS);
                        int year = diff / 365;
                        int Month = (diff % 365) / 30;
                        int day = (diff % 365) % 30;
                        String years = year == 0 ? "" : year + " year(s) ";
                        String Months = Month == 0 ? "" : Month + " Month(s) ";
                        String Days = day == 0 ? "" : day + " Day(s) ";
                        viewHolder.mageView.setText(years + Months + Days);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (!Data.image.isEmpty()) {
                        Picasso.with(getContext()).load(Data.image).fit().into(viewHolder.mImageView);
                    }
                    if (UserID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        viewHolder.mImageButton.setOnClickListener(v1 -> {
                            PopupMenu popup = new PopupMenu(getContext(), viewHolder.mImageButton);
                            MenuInflater inflater1 = popup.getMenuInflater();
                            inflater1.inflate(R.menu.pop_menu, popup.getMenu());
                            popup.setOnMenuItemClickListener(item -> {
                                int id = item.getItemId();
                                if (id == R.id.item_edit) {
                                    expand(addbabyform);
                                    collapse(recyclerView);
                                    collapse(Addbaby);
                                    Picasso.with(getContext()).load(Data.image).fit().into(baby_image);
                                    baby_birth.setText(Data.birth);
                                    baby_name.setText(Data.name);
                                    baby_gender.setSelection(Data.gender.equals(genderArray[0]) ? 0 : 1);
                                    baby_add_button.setText("Update Baby");
                                    UPDATE = Data.Key;
                                } else if (id == R.id.item_delete) {
                                    Data.Remove(Data.Key);
                                    mAdapter.notifyDataSetChanged();
                                }
                                return true;
                            });
                            popup.show();
                        });
                    } else {
                        viewHolder.mImageButton.setVisibility(View.GONE);
                    }
                    viewHolder.mView.setOnClickListener(v1 -> {

                        //TODO: Children hena
                        Intent intent = new Intent(getActivity(), BabyNeeds.class);
                        intent.putExtra("BABYID", Data.Key);
                        intent.putExtra("BABYPARENT", Data.parent);
                        startActivity(intent);
                    });
                    if (mAdapter.getItemCount() - 1 == position)
                        progressDialog.dismiss();
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        new FireBaseHelper.Children().Where(FireBaseHelper.Children.Table.Parent, UserID, Data -> {
            if (Data.size() == 0) {
                progressDialog.dismiss();
            }
        });
        return v;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setFixAspectRatio(true)
                        .start(getContext(), this);
            } else {
                Toast.makeText(getContext(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setFixAspectRatio(true)
                        .start(getContext(), this);
            } else {
                Toast.makeText(getContext(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK && WAITINGFORIMAGE) {
            imageUri = CropImage.getPickImageResultUri(context, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(context, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                //CropImage.activity(imageUri).start(getActivity());
                baby_image.setImageURI(imageUri);
                ImageURI = imageUri;
                WAITINGFORIMAGE = false;
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && WAITINGFORIMAGE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                baby_image.setImageURI(resultUri);
                ImageURI = resultUri;
                WAITINGFORIMAGE = false;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(context, result.getError().getMessage(), Toast.LENGTH_LONG).show();
                WAITINGFORIMAGE = false;
            }
        } else if (requestCode == 0) {
            WAITINGFORIMAGE = false;
        }
    }

    public static class babyviewholder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mnameView;
        public final TextView mgenderView;
        public final TextView mageView;
        public final ImageView mImageView;
        public final ImageButton mImageButton;

        public babyviewholder(View view) {
            super(view);
            mView = view;
            mnameView = (TextView) view.findViewById(R.id.baby_name);
            mgenderView = (TextView) view.findViewById(R.id.baby_gender);
            mageView = (TextView) view.findViewById(R.id.baby_age);
            mImageView = (ImageView) view.findViewById(R.id.baby_image);
            mImageButton = (ImageButton) view.findViewById(R.id.item_menu);
        }
    }
}
