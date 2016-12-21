package be.dijlezonen.dzwelg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class EventListActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        showProgressDialog();

        setTitle(getString(R.string.activity_event_titel));

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(getString(R.string.ref_evenementen));

        GridView eventsGrid = ButterKnife.findById(this, R.id.gridEvents);

        FirebaseListAdapter<Evenement> fbList = new FirebaseListAdapter<Evenement>(
                this, Evenement.class, R.layout.event, dbRef) {

            @Override
            protected void populateView(View v, final Evenement model, int position) {
                if(mProgressDialog.isShowing())
                {
                    mProgressDialog.dismiss();
                }
                TextView txtTitel = ButterKnife.findById(v, R.id.txt_event_titel);
                txtTitel.setText(model.getTitel());
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(EventListActivity.this, EventActivity.class);
                        intent.putExtra("event", model.getTitel());
                        startActivity(intent);
                    }
                });
            }
        };

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
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle(getString(R.string.even_wachten));
        mProgressDialog.setMessage(getString(R.string.laden_van_evenementen));
        mProgressDialog.show();
    }
}
