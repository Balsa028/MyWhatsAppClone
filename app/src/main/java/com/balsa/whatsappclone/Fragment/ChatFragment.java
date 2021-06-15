package com.balsa.whatsappclone.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balsa.whatsappclone.Adapter.UserRecViewAdapter;
import com.balsa.whatsappclone.Model.ChatList;
import com.balsa.whatsappclone.Model.User;
import com.balsa.whatsappclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private UserRecViewAdapter userAdapter;
    private ArrayList<User> users;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private RecyclerView previousRecViews;
    private ArrayList<ChatList> chatLists;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        previousRecViews = view.findViewById(R.id.recViewChat);
        previousRecViews.setHasFixedSize(true);
        previousRecViews.setLayoutManager(new LinearLayoutManager(getActivity()));
        getAllPreviousChats();
        return view;
    }

    private void getAllPreviousChats() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        users = new ArrayList<>();
        chatLists = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                chatLists.clear();
                //filling list with all chatlists
                for (DataSnapshot data : snapshot.getChildren()) {
                    ChatList chatList = data.getValue(ChatList.class);
                    chatLists.add(chatList);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        //getting now all users and compering userid with chatlisID getting like that all previous chats
        final DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    for (ChatList ch : chatLists) {
                        if (user.getId().equals(ch.getId())) {
                            users.add(user);
                        }
                    }
                }

                userAdapter = new UserRecViewAdapter(getActivity(), users, true);
                previousRecViews.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}