package com.skichrome.go4lunch.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.models.firestore.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.fragment_workmates_item_profile_image) ImageView profileImage;
    @BindView(R.id.fragment_workmates_item_join_or_not_text_view) TextView textView;

    WorkmatesViewHolder(View itemView)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateUI(User mUser, RequestManager mGlide)
    {
        this.textView.setText(mUser.getUsername());
        if (mUser.getUrlPicture() != null)
            mGlide.load(mUser.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(profileImage);
    }
}