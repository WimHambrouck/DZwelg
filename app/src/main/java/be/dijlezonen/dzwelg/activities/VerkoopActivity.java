package be.dijlezonen.dzwelg.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.adapters.ConsumptieRecyclerAdapter;
import be.dijlezonen.dzwelg.models.Consumptie;
import be.dijlezonen.dzwelg.models.ConsumptieViewHolder;
import be.dijlezonen.dzwelg.models.Consumptielijn;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class VerkoopActivity extends AppCompatActivity implements ConsumptieRecyclerAdapter.ConsumptieRecyclerAdapterCallback {

    List<Consumptielijn> consumptielijnen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verkoop);

        consumptielijnen = new ArrayList<>();

        DatabaseReference consumptieRef = FirebaseDatabase.getInstance().getReference(getString(R.string.ref_consumpties));

        FirebaseRecyclerAdapter<Consumptie, ConsumptieViewHolder> firebaseRecyclerAdapter =
                new ConsumptieRecyclerAdapter(
                        Consumptie.class,
                        R.layout.consumptie_item,
                        ConsumptieViewHolder.class,
                        consumptieRef,
                        this //voor callback naar updateTotaal
                );

        LinearLayoutManager llm = new LinearLayoutManager(this);
        RecyclerView consumptiesRecyclerView = (RecyclerView) this.findViewById(R.id.consumpties_recycler_view);
        consumptiesRecyclerView.setLayoutManager(llm);
        consumptiesRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void addConsumptielijn(int positie, Consumptie consumptie) {
        consumptielijnen.add(positie, new Consumptielijn(consumptie));
    }

    @Override
    public double updateAantal(int positie, int aantal) {
        Consumptielijn consumptielijn = consumptielijnen.get(positie);
        consumptielijn.setAantal(aantal);
        return consumptielijn.getConsumptie().getPrijs() * aantal;
    }
}
