package be.dijlezonen.dzwelg.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import be.dijlezonen.dzwelg.ConsumptieRecyclerAdapter;
import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.models.Consumptie;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class VerkoopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verkoop);

        DatabaseReference consumptieRef = FirebaseDatabase.getInstance().getReference("consumpties");

        FirebaseRecyclerAdapter<Consumptie, ConsumptieRecyclerAdapter.ConsumptieViewHolder> firebaseRecyclerAdapter =
                new ConsumptieRecyclerAdapter(
                        Consumptie.class,
                        R.layout.consumptie_item,
                        ConsumptieRecyclerAdapter.ConsumptieViewHolder.class,
                        consumptieRef
                );

        LinearLayoutManager llm = new LinearLayoutManager(this);
        RecyclerView consumptiesRecyclerView = (RecyclerView) this.findViewById(R.id.consumpties_recycler_view);
        consumptiesRecyclerView.setLayoutManager(llm);
        consumptiesRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


}
