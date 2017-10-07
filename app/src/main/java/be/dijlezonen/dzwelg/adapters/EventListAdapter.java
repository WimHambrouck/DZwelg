package be.dijlezonen.dzwelg.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.activities.UserListActivity;
import be.dijlezonen.dzwelg.models.Activiteit;
import butterknife.ButterKnife;

public class EventListAdapter extends FirebaseListAdapter<Activiteit> {

    private EventListAdapterCallback callback;

    /**
     * @param activity    The activity containing the ListView
     * @param modelClass  Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout This is the layout used to represent a single list item. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of modelClass.
     * @param ref         The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                    combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public EventListAdapter(Activity activity, Class<Activiteit> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        callback = (EventListAdapterCallback) activity;
    }

    @Override
    protected void populateView(View v, Activiteit activiteit, int position) {
        callback.hideProgressbar();
        TextView txtTitel = ButterKnife.findById(v, R.id.txt_event_titel);
        txtTitel.setText(activiteit.getTitel());
        v.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, UserListActivity.class);
            intent.putExtra(mActivity.getString(R.string.extra_event), activiteit);
            mActivity.startActivity(intent);
        });
    }

    public interface EventListAdapterCallback {
        void hideProgressbar();
    }

}
