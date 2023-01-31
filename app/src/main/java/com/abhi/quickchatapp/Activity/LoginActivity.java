package com.abhi.quickchatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abhi.quickchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    //defining object of ui elements
       TextView txt_signup;
       EditText login_email, login_password;
       TextView signIn_btn;
       //defining progressdialog
    ProgressDialog progressDialog;
    //creating authentication object
       FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getting firebaseauthentication
        auth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        //finding respective id
        txt_signup=findViewById(R.id.txt_signup);
        login_password= findViewById(R.id.login_password);
        login_email=findViewById(R.id.login_email);//finding id
        signIn_btn= findViewById(R.id.signin_btn);

        //email validation in android regex
        //defining email pattern(variable) of string type
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        //clicklistener to defined object
        signIn_btn.setOnClickListener(new View.OnClickListener() {
            //onclick method
            @Override
            public void onClick(View v) {
                progressDialog.show();
                //defining variables of string and getting  email and password  in it from edittext
                String email=login_email.getText().toString();
                String password=login_password.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Enter Valid Data", Toast.LENGTH_SHORT).show();
                }else if(!email.matches(emailPattern))
                {
                    progressDialog.dismiss();
                    login_email.setError("Invalid Email");
                    Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6)
                {
                    progressDialog.dismiss();
                    login_password.setError("Invalid password");
                    Toast.makeText(LoginActivity.this, "Please Enter Valid Password", Toast.LENGTH_SHORT).show();
                }else
                {
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {

                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            }
                            else
                            {

                                progressDialog.dismiss();//it goes in same page
                                Toast.makeText(LoginActivity.this, "Error in login in", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


                //complete listener for signing in

            }
        });


        txt_signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
    }
}