package be.dijlezonen.dzwelg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        setTitle(getString(R.string.activity_event_titel));

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(getString(R.string.ref_evenementen));


        GridView eventsGrid = ButterKnife.findById(this, R.id.gridEvents);

        FirebaseListAdapter<Evenement> fbList = new FirebaseListAdapter<Evenement>(
                this, Evenement.class, R.layout.event, dbRef) {
            @Override
            protected void populateView(View v, final Evenement model, int position) {
                TextView txtTitel = ButterKnife.findById(v, R.id.txt_event_titel);
                txtTitel.setText(model.getTitel());
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(EventActivity.this, model.getAangemaakt().getTime().toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        eventsGrid.setAdapter(fbList);

    }
}
