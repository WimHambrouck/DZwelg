package be.dijlezonen.dzwelg.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.adapters.EventListAdapter;
import be.dijlezonen.dzwelg.models.Activiteit;
import butterknife.ButterKnife;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class EventListActivity extends AppCompatActivity implements EventListAdapter.EventListAdapterCallback {

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        setTitle(getString(R.string.activity_event_titel));

        showProgressDialog();

        Query dbRef = FirebaseDatabase.getInstance().getReference(getString(R.string.ref_activiteit))
                .orderByChild("actief")
                .startAt(true)
                .endAt(true);

        GridView eventsGrid = ButterKnife.findById(this, R.id.gridEvents);
        FirebaseListAdapter<Activiteit> fbList =
                new EventListAdapter(this, Activiteit.class, R.layout.event, dbRef);
        eventsGrid.setAdapter(fbList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_afmelden) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setTitle(getString(R.string.even_wachten));
            mProgressDialog.setMessage(getString(R.string.laden_van_evenementen));
        }
        mProgressDialog.show();
    }

    @Override
    public void hideProgressbar() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
