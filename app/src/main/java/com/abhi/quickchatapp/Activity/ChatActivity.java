package com.abhi.quickchatapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abhi.quickchatapp.Adapter.MessagesAdater;
import com.abhi.quickchatapp.ModelClass.Messages;
import com.abhi.quickchatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReciverImage,ReciverUID,ReciverName,SenderUID;
    CircleImageView profileImage;
    TextView reciverName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public static String sImage;
    public static String rImage;

    CardView sendBtn;
    EditText edtMessage;

    String senderRoom,reciverRoom;

    RecyclerView messageAdater;
    //defining arraylist with typecasting model class Messages
    ArrayList<Messages> messagesArrayList;

    MessagesAdater adater;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        ReciverName=getIntent().getStringExtra("name");
        ReciverImage=getIntent().getStringExtra("ReciverImage");
        ReciverUID=getIntent().getStringExtra("uid");

        messagesArrayList = new ArrayList<>();

        profileImage=findViewById(R.id.profile_image);
        reciverName = findViewById(R.id.receiverName);

        messageAdater = findViewById(R.id.messageAdater);
        //making linearlayoutmanager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //provide recent messages at bottom
        linearLayoutManager.setStackFromEnd(true);
        adater = new MessagesAdater(ChatActivity.this,messagesArrayList);
        messageAdater.setAdapter(adater);
        messageAdater.setLayoutManager(linearLayoutManager);

        sendBtn = findViewById(R.id.sendBtn);
        edtMessage = findViewById(R.id.edtMessage);

        Picasso.get().load(ReciverImage).into(profileImage);
        reciverName.setText(""+ReciverName);

        SenderUID = firebaseAuth.getUid();

        senderRoom = SenderUID+ReciverUID;
        reciverRoom = ReciverUID+SenderUID;

        //storing data
        //getting uid of sender
        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatRefrece= database.getReference().child("chats").child(senderRoom).child("messages");


        chatRefrece.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adater.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                sImage= snapshot.child("imageUri").getValue().toString();
                rImage = ReciverImage;
            }
            @Override
            public  void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting message and converting into string
                String message = edtMessage.getText().toString();
                if(message.isEmpty())
                {
                    Toast.makeText(ChatActivity.this, "Please Enter Valid Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                //message box keeping empty
                edtMessage.setText("");
                Date date = new Date();
                Messages messages = new Messages(message,SenderUID,date.getTime());

                database = FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats")
                                        .child(reciverRoom)
                                        .child("messages")
                                        .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });

            }
        });








    }
}