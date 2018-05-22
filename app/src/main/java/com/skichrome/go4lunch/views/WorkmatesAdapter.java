package com.skichrome.go4lunch.views;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.models.firestore.User;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesViewHolder>
{
    private RequestManager glide;
    private String  origin;
    private String[] texts;

    public WorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager mGlide, String mOrigin, String... mTexts)
    {
        super(options);
        this.glide = mGlide;
        this.origin = mOrigin;
        this.texts = mTexts;
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position, @NonNull User model)
    {
        holder.updateUI(model, this.glide);
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new WorkmatesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_workmates_list_item_recycler_view, parent, false), origin, texts);
    }
}