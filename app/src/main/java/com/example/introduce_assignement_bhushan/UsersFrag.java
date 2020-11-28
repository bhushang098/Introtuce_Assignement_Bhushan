package com.example.introduce_assignement_bhushan;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.introduce_assignement_bhushan.Adapters.UserItemAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ProgressBar loader;
    RecyclerView recyclerView;

    FirebaseFirestore db;
    List<User> dataList = new ArrayList<>();

    UserItemAdapter adapter;
    Boolean secondtime = false;
    TextView tvNoSuer;

    public UsersFrag() {
        // Required empty public constructor
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser&&secondtime) {
           getUsersList();
        } else {

        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFrag newInstance(String param1, String param2) {
        UsersFrag fragment = new UsersFrag();
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_users, null);
        loader = root.findViewById(R.id.pb_user_list);
        recyclerView = root.findViewById(R.id.rec_user_list);
        tvNoSuer = root.findViewById(R.id.tv_no_user);
        db = FirebaseFirestore.getInstance();
        getUsersList();
        return  root;
    }

    private void getUsersList() {
        secondtime = true;
        if(dataList.size()>0)
        {
            dataList.clear();
        }
        loader.setVisibility(View.VISIBLE);
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            //Toast.makeText(getContext(), "Successfull", Toast.LENGTH_SHORT).show();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                adduserInList(document);
                                Log.d("DOCS>>", document.getId() + " => " + document.getData());
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            adapter = new UserItemAdapter(dataList,getContext());
                            recyclerView.setAdapter(adapter);
                            loader.setVisibility(View.GONE);
                            if(dataList.size()<1)
                                tvNoSuer.setVisibility(View.VISIBLE);
                            else
                                tvNoSuer.setVisibility(View.GONE);


                        } else {
                            loader.setVisibility(View.GONE);
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void adduserInList(QueryDocumentSnapshot document) {
        User user = new User();
        user.setName(document.getData().get("first_name").toString());
        user.setSurname(document.getData().get("last_name").toString());
        user.setGender(document.getData().get("gender").toString());
        user.setDob(document.getData().get("dob").toString());
        user.setPhNo(document.getData().get("pn_no").toString());
        user.setProfile(document.getData().get("profile").toString());
        user.setTown(document.getData().get("town").toString());
        user.setDocid(document.getId());
        dataList.add(user);
    }
}