package com.skichrome.go4lunch.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesViewHolder extends RecyclerView.ViewHolder
{
    @BindView(R.id.fragment_workmates_item_profile_image) ImageView profileImage;
    @BindView(R.id.fragment_workmates_item_join_or_not_text_view) TextView textView;

    private String origin;
    private String[] texts;

    WorkmatesViewHolder(View itemView, String mOrigin, String... mTexts)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.origin = mOrigin;
        this.texts = mTexts;
    }

    void updateUI(User mUser, RequestManager mGlide)
    {
        UserHelper.getUser(mUser.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot mDocumentSnapshot)
            {
                User user = mDocumentSnapshot.toObject(User.class);
                String result = "";

                if (user != null)
                {
                    if (origin == null)
                        result = user.getUsername() + " " + (user.getSelectedPlace() != null ? texts[0] + " " + user.getSelectedPlace().getName() : texts[1]);
                    else
                        result = (user.getSelectedPlace() != null && user.getSelectedPlace().getId().equals(origin) ? user.getUsername() + " " + texts[0] : "");
                }
                textView.setText(result);
            }
        });

        if (mUser.getUrlPicture() != null)
            mGlide.load(mUser.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(profileImage);
    }
}