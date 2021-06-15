package com.balsa.whatsappclone.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balsa.whatsappclone.Model.Chat;
import com.balsa.whatsappclone.Model.User;
import com.balsa.whatsappclone.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Chat> chats = new ArrayList<>();
    private String imageUrl;

    private FirebaseUser firebaseUser;
    //creating constants for knowing which layout to inflate
    public static final int LEFT_SIDE_MESSAGE = 0;
    public static final int RIGHT_SIDE_MESSAGE = 1;

    public MessageAdapter(Context context, String imageUrl) {
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == RIGHT_SIDE_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapter.ViewHolder holder, int position) {

        holder.txtDisplayedMessage.setText(chats.get(position).getMessage());
        if (imageUrl.equalsIgnoreCase("default")) {
            Glide.with(context)
                    .load(R.mipmap.ic_launcher)
                    .into(holder.image);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void setUsers(ArrayList<Chat> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equalsIgnoreCase(firebaseUser.getUid())) {
            return RIGHT_SIDE_MESSAGE;
        } else {
            return LEFT_SIDE_MESSAGE;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtDisplayedMessage;
        ImageView image;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txtDisplayedMessage = itemView.findViewById(R.id.txtdisplayedMessage);
            image = itemView.findViewById(R.id.profileImg);

        }
    }
}
