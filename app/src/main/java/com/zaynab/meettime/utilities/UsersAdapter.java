package com.zaynab.meettime.utilities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.zaynab.meettime.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private Context mContext;
    private List<ParseUser> mUsers;
    private OnClickListener mClickListener;


    //communicate with activity
    public interface OnClickListener {
        void OnItemClicked(int position);
    }

    public UsersAdapter(Context context, List<ParseUser> users, OnClickListener clickListener) {
        this.mContext = context;
        this.mUsers = users;
        this.mClickListener = clickListener;

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIvPicture;
        private TextView mTvUsername;
        private TextView mTvFullName;
        private MaterialButton mBtnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvPicture = itemView.findViewById(R.id.ivUserPicture);
            mTvUsername = itemView.findViewById(R.id.tvUsername);
            mTvFullName = itemView.findViewById(R.id.tvFullName);
            mBtnAdd = itemView.findViewById(R.id.btnAdd);
            itemView.setOnClickListener(this);
        }

        public void bind(final ParseUser usr) {
            ParseFile profile_image = usr.getParseFile("profilePicture");
            if (profile_image != null)
                Glide.with(mContext).load(profile_image.getUrl()).apply(RequestOptions.circleCropTransform()).into(mIvPicture);
            mTvUsername.setText(usr.getUsername());
            if (usr.getString("firstName") != null)
                mTvFullName.setText(usr.getString("firstName") + " " + usr.getString("lastName"));
            if (usr.getUsername().equals(ParseUser.getCurrentUser().getUsername()))
                mBtnAdd.setVisibility(View.GONE);

        }


        @Override
        public void onClick(View view) {
            mClickListener.OnItemClicked(getAdapterPosition());
        }
    }//end_VH_class

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser usr = mUsers.get(position);
        holder.bind(usr);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    //Helper function for refresh-on-swipe container
    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ParseUser> list) {
        mUsers.addAll(list);
        notifyDataSetChanged();
    }

    //quick fix to recycled icons
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
