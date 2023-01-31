package com.abhi.quickchatapp.Adapter;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.abhi.quickchatapp.Activity.ChatActivity;
import com.abhi.quickchatapp.Activity.HomeActivity;
import com.abhi.quickchatapp.ModelClass.Users;
import com.abhi.quickchatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {
    //variables
    Context homeActivity;
    ArrayList<Users> usersArrayList;
    public UserAdapter(HomeActivity homeActivity, ArrayList<Users> usersArrayList) {
        this.homeActivity = homeActivity;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(homeActivity).inflate(R.layout.item_user_row,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        Users users = usersArrayList.get(position);

        //turning off own chat useritemview in homepage
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(users.getUid())){
            holder.itemView.setVisibility(View.GONE);
        }
        //setting user name and status
        holder.user_name.setText(users.getName());
        holder.user_status.setText(users.getStatus());

        //picasso used for gettingimageuri  into userprofile
        Picasso.get().load(users.getImageUri()).placeholder(R.drawable.profile).into(holder.user_profile);
        //when clicked in useritem go to chat activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity, ChatActivity.class);
                //getting name ,RAeciverimage,unique uid
                intent.putExtra("name",users.getName());
                intent.putExtra("ReciverImage",users.getImageUri());
                intent.putExtra("uid",users.getUid());
                //start activity by passing intent
                homeActivity.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {return usersArrayList.size();
    }
    static class Viewholder extends RecyclerView.ViewHolder {
        //creating variables
        CircleImageView user_profile;
        TextView user_name;
        TextView user_status;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            //finding variables id
            user_profile=itemView.findViewById(R.id.user_image);
            user_name=itemView.findViewById(R.id.user_name);
            user_status=itemView.findViewById(R.id.user_status);
        }
    }



}
