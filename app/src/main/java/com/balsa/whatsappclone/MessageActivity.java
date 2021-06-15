package com.balsa.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.balsa.whatsappclone.Adapter.MessageAdapter;
import com.balsa.whatsappclone.Model.Chat;
import com.balsa.whatsappclone.Model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    private TextView txtUserName;
    private ImageView image;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private RecyclerView recyclerView;
    private EditText editTextMessages;
    private ImageButton btnSend;
    private String userId;

    private MessageAdapter adapter;
    private ArrayList<Chat> chats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        txtUserName = findViewById(R.id.txtUserNameMessage);
        image = findViewById(R.id.imageMessage);
        recyclerView = findViewById(R.id.recViewMessages);
        editTextMessages = findViewById(R.id.editTextMessage);
        btnSend = findViewById(R.id.btnSend);

        //recycler
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("userID");
            if (userId != null) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            txtUserName.setText(user.getUsername());
                            if (user.getImageUrl().equalsIgnoreCase("default")) {
                                Glide.with(MessageActivity.this)
                                        .asBitmap()
                                        .load(R.mipmap.ic_launcher)
                                        .into(image);
                            } else {
                                Glide.with(MessageActivity.this)
                                        .asBitmap()
                                        .load(user.getImageUrl())
                                        .into(image);
                            }
                            readMessages(firebaseUser.getUid(), userId, user.getImageUrl());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessages.getText().toString();
                if (message != null) {
                    sendMessage(firebaseUser.getUid(), userId, message);
                } else {
                    Toast.makeText(MessageActivity.this, "You cannot send empty message!", Toast.LENGTH_SHORT).show();
                }
                editTextMessages.setText("");
            }
        });


    }

    //method for getting status of user
    private void setStatus(String status) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        myRef.child("status").setValue(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //calling set status method on resume
        setStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("offline");
    }

    private void readMessages(String myid, String userId, String imgUrl) {
        chats = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);
                    if (chat.getReceiver().equalsIgnoreCase(userId) && chat.getSender().equalsIgnoreCase(myid)
                            || chat.getSender().equalsIgnoreCase(userId) && chat.getReceiver().equalsIgnoreCase(myid)) {
                        chats.add(chat);
                    }
                }
                adapter = new MessageAdapter(MessageActivity.this, imgUrl);
                adapter.setUsers(chats);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String currentUserId, String receiverId, String message) {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> data = new HashMap<>();
        data.put("sender", currentUserId);
        data.put("receiver", receiverId);
        data.put("message", message);

        myRef.child("Chat").push().setValue(data);
        //setting when send message to createa chatlist in database for storing previous conv.
        final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);

        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatReference.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}