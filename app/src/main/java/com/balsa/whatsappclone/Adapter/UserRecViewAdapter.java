package com.balsa.whatsappclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balsa.whatsappclone.MessageActivity;
import com.balsa.whatsappclone.Model.User;
import com.balsa.whatsappclone.R;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserRecViewAdapter extends RecyclerView.Adapter<UserRecViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private boolean isItChat;

    public UserRecViewAdapter(Context context, ArrayList<User> users, boolean isItChat) {
        this.context = context;
        this.users = users;
        this.isItChat = isItChat;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserRecViewAdapter.ViewHolder holder, int position) {
        holder.txtUserName.setText(users.get(position).getUsername());
        if (users.get(position).getImageUrl() != null && users.get(position).getImageUrl().equalsIgnoreCase("default")) {
            Glide.with(context)
                    .load(R.mipmap.ic_launcher)
                    .into(holder.image);
        } else {
            Glide.with(context)
                    .load(users.get(position).getImageUrl())
                    .into(holder.image);
        }
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 3.6.21. navigate user to another activity
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userID", users.get(position).getId());
                context.startActivity(intent);
            }
        });
        //checking is it chat fragment if it is showing user status
        if (isItChat) {
            holder.txtStatus.setVisibility(View.VISIBLE);
            if (users.get(position).getStatus().equals("online")) {
                holder.txtStatus.setText("Online");
                holder.txtStatus.setTextColor(context.getResources().getColor(R.color.custom));
            } else {
                holder.txtStatus.setText("Offline");
            }
        } else {
            holder.txtStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public MaterialCardView parent;
        public ImageView image;
        public TextView txtUserName, txtStatus;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            //initializing
            parent = itemView.findViewById(R.id.parent);
            image = itemView.findViewById(R.id.userImageView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
