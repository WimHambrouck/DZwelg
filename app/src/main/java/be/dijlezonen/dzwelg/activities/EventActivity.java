package be.dijlezonen.dzwelg.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import be.dijlezonen.dzwelg.R;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        setTitle(getIntent().getStringExtra("event"));
    }
}
