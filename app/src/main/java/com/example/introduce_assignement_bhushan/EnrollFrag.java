package com.example.introduce_assignement_bhushan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.PatternPathMotion;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnrollFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnrollFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //All VAriabels
    ImageView ivProfile;
    TextView tvSelectProfile;
    Button btnAddUser;

    EditText etName, etSurName, etdob, etgender, etCountry, etState, etTown, etPhNo, etTeleNo;

    String name, surname, dob, gender, country, state, town, phNo, teleNO, uuid;

    Uri imageUri = null;


    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    FirebaseFirestore db;
    private List<User> dataList = new ArrayList<>();
    private boolean uniqueNo = true;
    ScrollView scrollView;

    public EnrollFrag() {
        // Required empty public constructor
    }

    
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnrollFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static EnrollFrag newInstance(String param1, String param2) {
        EnrollFrag fragment = new EnrollFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_enroll, null);

        progressDialog = new ProgressDialog(getContext());


        //Set Id's To Varabels
        etName = root.findViewById(R.id.et_first_name);
        etSurName = root.findViewById(R.id.et_last_name);
        etdob = root.findViewById(R.id.et_dob);
        etgender = root.findViewById(R.id.et_gender);
        etCountry = root.findViewById(R.id.et_country);
        etState = root.findViewById(R.id.et_state);
        etTown = root.findViewById(R.id.et_home_town);
        etPhNo = root.findViewById(R.id.et_ph_no);
        etTeleNo = root.findViewById(R.id.et_telephone_num);

        btnAddUser = root.findViewById(R.id.btn_add_user);
        tvSelectProfile = root.findViewById(R.id.tv_select_image);
        ivProfile = root.findViewById(R.id.iv_profile_selected);
        scrollView = root.findViewById(R.id.sc_v_enroll);
        //till

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();

        getUsersList();

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                surname = etSurName.getText().toString();
                dob = etdob.getText().toString();
                gender = etgender.getText().toString();
                country = etCountry.getText().toString();
                state = etState.getText().toString();
                town = etTown.getText().toString();
                phNo = etPhNo.getText().toString();
                teleNO = etTeleNo.getText().toString();

                if (imageUri == null) {
                    Toast.makeText(getContext(), "Select Profile First", Toast.LENGTH_SHORT).show();
                    scrollView.fullScroll(View.FOCUS_UP);
                } else {

                    if (name.isEmpty() || surname.isEmpty() || dob.isEmpty()
                            || gender.isEmpty() || country.isEmpty() || state.isEmpty()
                            || town.isEmpty() || phNo.isEmpty() || teleNO.isEmpty() || phNo.length() != 10) {

                        if(phNo.length() != 10)
                            Toast.makeText(getContext(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                        else
                        Toast.makeText(getContext(), " Invalid Fields ", Toast.LENGTH_SHORT).show();

                    } else {
                        if (new NetworkOperator().checknetConnection(getContext()) &&  dob.matches("\\d{2}/\\d{2}/\\d{4}")){
                            for (User user :
                                    dataList) {
                                if (user.getPhNo().equalsIgnoreCase(phNo)) {
                                    uniqueNo = false;
                                    Toast.makeText(getContext(), " Phone Already Exists", Toast.LENGTH_SHORT).show();
                                    break;
                                } else {
                                    uniqueNo = true;
                                }
                            }
                            if(uniqueNo)
                                uploadImage();
                        }


                        else {
                            if(dob.matches("\\d{2}/\\d{2}/\\d{4}"))
                            Toast.makeText(getContext(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getContext(), "Invalid Date Of Birth Format", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

            }
        });

        tvSelectProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });


        return root;
    }

    private void uploadImage() {
        progressDialog.setMessage("Adding New User....");
        progressDialog.show();
        uuid = UUID.randomUUID().toString();
        StorageReference ref
                = storageReference
                .child(
                        "profile_images/"
                                + uuid);

        ref.putFile(imageUri)
                .addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(
                                    UploadTask.TaskSnapshot taskSnapshot) {

                                // Image uploaded successfully
                                // Dismiss dialog
                                adduserData();
                                // progressDialog.dismiss();
//                                Toast
//                                        .makeText(getContext(),
//                                                "Image Uploaded!!",
//                                                Toast.LENGTH_SHORT)
//                                        .show();
                            }
                        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                                .makeText(getContext(),
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .addOnProgressListener(
                        new OnProgressListener<UploadTask.TaskSnapshot>() {

                            // Progress Listener for loading
                            // percentage on the dialog box
                            @Override
                            public void onProgress(
                                    UploadTask.TaskSnapshot taskSnapshot) {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Adding  "
                                                + (int) progress + "%...");
                            }
                        });

    }

    private void adduserData() {

        Map<String, Object> user = new HashMap<>();
        user.put("first_name", name);
        user.put("last_name", surname);
        user.put("dob", dob);
        user.put("gender", gender);
        user.put("country", country);
        user.put("state", state);
        user.put("town", town);
        user.put("pn_no", phNo);
        user.put("tele_no", teleNO);
        user.put("profile", uuid);


        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "New User Added Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.setMessage("User Added Successfully");
                        resetFiels();
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding User", e);
                        Toast.makeText(getContext(), "Failed ..." + e.toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

    }

    private void resetFiels() {
        ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_image_24));
        imageUri = null;
        etName.setText("");
        etSurName.setText("");
        etdob.setText("");
        etgender.setText("");
        etCountry.setText("");
        etTown.setText("");
        etState.setText("");
        etPhNo.setText("");
        etTeleNo.setText("");

    }



    private void getUsersList() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                           // Toast.makeText(getContext(), "Successfull", Toast.LENGTH_SHORT).show();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                adduserInList(document);
                                Log.d("DOCS>>", document.getId() + " => " + document.getData());
                            }


                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            imageUri = data.getData();
            ivProfile.setImageURI(imageUri);
        }
    }

    private void adduserInList(QueryDocumentSnapshot document) {
        User user = new User();
        user.setPhNo(document.getData().get("pn_no").toString());
        dataList.add(user);
    }
}