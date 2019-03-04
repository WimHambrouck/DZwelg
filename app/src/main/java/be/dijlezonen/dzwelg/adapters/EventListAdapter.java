package be.dijlezonen.dzwelg.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.Query;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.activities.UserListActivity;
import be.dijlezonen.dzwelg.models.Activiteit;
import butterknife.ButterKnife;

public class EventListAdapter extends FirebaseListAdapter<Activiteit> {

    private EventListAdapterCallback callback;

    public EventListAdapter(@NonNull FirebaseListOptions<Activiteit> options, EventListAdapterCallback eventListAdapterCallback) {
        super(options);
        callback = eventListAdapterCallback;
    }

    @Override
    protected void populateView(@NonNull View v, @NonNull Activiteit model, int position) {
        callback.hideProgressbar();
        TextView txtTitel = v.findViewById(R.id.txt_event_titel);
        txtTitel.setText(model.getTitel());
        v.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent intent = new Intent(context, UserListActivity.class);
            intent.putExtra(context.getString(R.string.extra_event), model);
            context.startActivity(intent);
        });
    }

    public interface EventListAdapterCallback {
        void hideProgressbar();
    }

}
