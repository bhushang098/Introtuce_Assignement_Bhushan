package com.example.introduce_assignement_bhushan.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.introduce_assignement_bhushan.R;
import com.example.introduce_assignement_bhushan.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UserItemAdapter extends RecyclerView.Adapter<UserItemAdapter.UserItemAdapterViewHolder> {

    List<User> dataList;
    Context context;

    public UserItemAdapter(List<User> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserItemAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_item, parent, false);
        return new UserItemAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserItemAdapterViewHolder holder, int position) {

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.start();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(("profile_images/" +
                dataList.get(position).getProfile()));

         storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context).load(uri).placeholder(circularProgressDrawable).into(holder.ivProfile);
            }
        });

        holder.name.setText(dataList.get(position).getName()+" "+dataList.get(position).getSurname());

        String details = dataList.get(position).getGender() + " | ";
        int age = 2020 -
                Integer.parseInt(dataList.get(position).getDob().split("/")[2]);
        details = details + String.valueOf(age) + " | " +
                dataList.get(position).getTown();
        holder.userdata.setText(details);


        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemRemoved(position);
                notifyDataSetChanged();
                deleteUser(dataList.get(position).getDocid());
                dataList.remove(position);

            }
        });
    }

    private void deleteUser(String docid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(docid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "User Deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class UserItemAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView name, userdata;
        ImageView ivDelete, ivProfile;

        public UserItemAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_user_name);
            userdata = itemView.findViewById(R.id.tv_user_details);
            ivDelete = itemView.findViewById(R.id.iv_delete_user);
            ivProfile = itemView.findViewById(R.id.iv_profile);
        }
    }
}
