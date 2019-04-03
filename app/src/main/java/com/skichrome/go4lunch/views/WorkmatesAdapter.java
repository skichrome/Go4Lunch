package com.skichrome.go4lunch.views;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.models.firestore.User;

/**
 * Adapter tha extends FirestoreRecyclerAdapter, to diplay a list of workmates.
 */
public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesViewHolder>
{
    /**
     * For imageView updates with an url.
     */
    private RequestManager mGlide;
    /**
     * If the user has selected a restaurant, his id is stored here.
     */
    private String mSelectedRestaurant;
    /**
     * Contains strings that show if user has selected a place, or not.
     */
    private String[] mTexts;

    /**
     * Constructor for this adapter. Init fields.
     * @param glide
     *      {@link #mGlide}
     * @param selectedRestaurant
     *      {@link #mSelectedRestaurant}
     * @param texts
     *      {@link #mTexts}
     */
    public WorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide, String selectedRestaurant, String... texts)
    {
        super(options);
        this.mGlide = glide;
        this.mSelectedRestaurant = selectedRestaurant;
        this.mTexts = texts;
    }

    /**
     * Update view holder when needed.
     * @param holder
     *      The holder to update.
     * @param position
     *      The position of the holder.
     * @param model
     *      The user associated.
     */
    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position, @NonNull User model)
    {
        holder.updateUI(model, this.mGlide);
    }

    /**
     * Create view holder with needed parameters.
     */
    @NonNull @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new WorkmatesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_workmates_list_item_recycler_view, parent, false), mSelectedRestaurant, mTexts);
    }
}