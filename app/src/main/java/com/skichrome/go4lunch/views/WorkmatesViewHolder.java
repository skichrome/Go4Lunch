package com.skichrome.go4lunch.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

class WorkmatesViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.fragment_workmates_item_profile_image) ImageView mProfileImage;
    @BindView(R.id.fragment_workmates_item_join_or_not_text_view) TextView mTextView;

    private String mOrigin;
    private String[] mTexts;

    WorkmatesViewHolder(View itemView, String origin, String... texts)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mOrigin = origin;
        this.mTexts = texts;
    }

    void updateUI(User user, RequestManager glide)
    {
        UserHelper.getUser(user.getUid()).addOnSuccessListener(mDocumentSnapshot ->
        {
            User user1 = mDocumentSnapshot.toObject(User.class);
            String result = "";

            if (user1 != null)
            {
                if (mOrigin == null)
                    result = user1.getUsername() + " " + (user1.getSelectedPlaceId() != null ? mTexts[0] + " " + user1.getSelectedPlace().getName() : mTexts[1]);
                else
                    result = (user1.getSelectedPlaceId() != null && user1.getSelectedPlace().getId().equals(mOrigin) ? user1.getUsername() + " " + mTexts[0] : "");
            }
            mTextView.setText(result);
        });

        if (user.getUrlPicture() != null)
            glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
    }
}