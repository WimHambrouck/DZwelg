package be.dijlezonen.dzwelg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        setTitle(getString(R.string.activity_event_titel));

        //GridView eventsGrid = ButterKnife.findById(this, R.id.gridEvents);



    }
}
