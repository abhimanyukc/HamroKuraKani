package com.abhi.quickchatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abhi.quickchatapp.R;
import com.abhi.quickchatapp.Adapter.UserAdapter;
import com.abhi.quickchatapp.ModelClass.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase database;
    //creating arraylistofusers
    ArrayList<Users>  usersArrayList;
    ImageView imgLogout;
    ImageView imgSetting;
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //getting user
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //allocating memory of arraylist
        usersArrayList = new ArrayList<>();

        if(auth.getCurrentUser()==null)
        {
            startActivity(new Intent(HomeActivity.this,RegistrationActivity.class));
        }

        DatabaseReference reference = database.getReference().child("user");

        //applying valueeventlistener to refrence(comsisting userslist)
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            //for new user
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    //getting new users
                    Users users = dataSnapshot.getValue(Users.class);
                    //adding user to usersarraylist
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imgLogout = findViewById(R.id.img_logOut);
        imgSetting = findViewById(R.id.img_Setting);

        mainUserRecyclerView=findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //creating adapter
        adapter = new UserAdapter(HomeActivity.this,usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);


        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //creating dialog ,
                Dialog dialog = new Dialog(HomeActivity.this,R.style.Dialoge);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setContentView(R.layout.dialog_layout);
                TextView yesBtn, noBtn;
                yesBtn = dialog.findViewById(R.id.yesBtn);
                noBtn = dialog.findViewById(R.id.noBtn);
                yesBtn.setOnClickListener(new View.OnClickListener() {
                    //providing user logout
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    }
                });
                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        //click event of setting button
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,SettingActivity.class));
            }
        });

    }


    @Override
    public  void onBackPressed() {
        if(doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, "Please click Back again to exit", Toast.LENGTH_SHORT).show();
        doubleBackToExitPressedOnce = true;
    }

}