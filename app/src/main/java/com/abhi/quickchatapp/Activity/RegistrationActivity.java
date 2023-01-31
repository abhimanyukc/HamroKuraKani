package com.abhi.quickchatapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.abhi.quickchatapp.R;
import com.abhi.quickchatapp.ModelClass.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {
//defining variables
    TextView txt_signin, btn_SignUp;
    CircleImageView profile_image;
    EditText reg_name,reg_email,reg_password,reg_cPassword;
    FirebaseAuth auth; //variable for firebase authentication
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    String imageURI;
    ProgressDialog progressDialog;
    //creating firebase database
    FirebaseDatabase database;
    //creating firebase storage
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //getting instance
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        //finding its id
        profile_image = findViewById(R.id.profile_image);
        reg_email = findViewById(R.id.reg_email);
        reg_name = findViewById(R.id.reg_name);
        reg_password = findViewById(R.id.reg_pass);
        reg_cPassword = findViewById(R.id.reg_cPass);
        txt_signin = findViewById(R.id.txt_signin);
        btn_SignUp = findViewById(R.id.btn_SignUp);
        //click event pf signupbutton
        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            //then user will be create so for authenticate to homepage we need to create firebase auth object
            @Override
            public void onClick(View v) {
                progressDialog.show();
                //getting name in input field and stored into string variable
                String name=reg_name.getText().toString();
                String email=reg_email.getText().toString();
                String password=reg_password.getText().toString();
                String cPassword=reg_cPassword.getText().toString();
                String status="Hey There I'm Using This Application";

                if(TextUtils.isEmpty(name) ||TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword))
                {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this,"Please Enter Valid Data",Toast.LENGTH_SHORT);
                }
                else if(!email.matches(emailPattern))
                {
                    progressDialog.dismiss();
                    //Error in email edittext
                    reg_email.setError("Please Enter Valid Email");
                    Toast.makeText(RegistrationActivity.this, "Please Enter valid Email", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(cPassword))
                {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this,"Password does not Match",Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6)
                {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Enter At least six character password" , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //if all the sign up field is in correct format user will be create
                    //creating user passing parameter email and pw
                    //adding complete listener we know task is sucess or not
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {

                                //after user created data stored in database
                                //make db reference
                                DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference= storage.getReference().child("upload").child(auth.getUid());

                                //for image adding in storage and its url in database
                                if(imageUri!=null)
                                {
                                    //if imageUri is added,add in storagerefrence in uplod folder
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                //downloadable
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        //convert imguri into stirng
                                                        imageURI=uri.toString();
                                                        //get authofuid,name,email,,imageuri,status into user variable
                                                        Users users = new Users(auth.getUid(), name,email,imageURI,status);
                                                        //finally setting user
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    progressDialog.dismiss();
                                                                    startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                                                                }else
                                                                {
                                                                    Toast.makeText(RegistrationActivity.this, "Error in Creating a new User", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else
                                //for not selecting imageuri
                                {
                                    String status="Hey There I'm Using This Application";
                                    //default image for not picking image
                                    imageURI="https://firebasestorage.googleapis.com/v0/b/quickchatapp-4bfe3.appspot.com/o/profile.jpg?alt=media&token=f9a842e6-72ec-4a32-b5bc-d3fb924315bb";
                                    //getting uid,name,email,imageuri,status
                                    Users users = new Users(auth.getUid(),name,email,imageURI,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                                            }else
                                            {
                                                Toast.makeText(RegistrationActivity.this, "Error in Creating a new User", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else{
                                progressDialog.dismiss();

                                Toast.makeText(RegistrationActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        //using image picker
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image from gallery
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),1999);
            }
        });



        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

    }
    //image passed from gallery to method onactivityresult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1999)
        {
            if(data!=null)
            {
                //getting data in imageUri variable of string type
                imageUri=data.getData();
                //fix the imageUri as profile image
                profile_image.setImageURI(imageUri);
            }
        }
    }
}