package be.dijlezonen.dzwelg.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.adapters.ConsumptieRecyclerAdapter;
import be.dijlezonen.dzwelg.models.Consumptie;
import be.dijlezonen.dzwelg.models.ConsumptieViewHolder;
import be.dijlezonen.dzwelg.models.Consumptielijn;
import butterknife.BindView;
import butterknife.ButterKnife;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class VerkoopActivity extends AppCompatActivity implements ConsumptieRecyclerAdapter.ConsumptieRecyclerAdapterCallback {

    private NumberFormat numberFormatter;
    private List<Consumptielijn> consumptielijnen;

    @BindView(R.id.txt_totaalbedrag)
    TextView txtTotaal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verkoop);

        initActionBar();

        ButterKnife.bind(this);

        consumptielijnen = new ArrayList<>();
        numberFormatter = NumberFormat.getCurrencyInstance();
        numberFormatter.setCurrency(Currency.getInstance("EUR"));

        initConsumptieLijst();
    }

    private void initConsumptieLijst() {
        Query consumptieRef = FirebaseDatabase.getInstance().getReference(getString(R.string.ref_consumpties)).orderByChild(getString(R.string.ref_child_naam));

        FirebaseRecyclerAdapter<Consumptie, ConsumptieViewHolder> firebaseRecyclerAdapter =
                new ConsumptieRecyclerAdapter(
                        Consumptie.class,
                        R.layout.consumptie_item,
                        ConsumptieViewHolder.class,
                        consumptieRef,
                        this //voor callback methodes
                );

        LinearLayoutManager llm = new LinearLayoutManager(this);
        RecyclerView consumptiesRecyclerView = (RecyclerView) this.findViewById(R.id.consumpties_recycler_view);
        consumptiesRecyclerView.setLayoutManager(llm);
        consumptiesRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_verkoop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_verkoop_afrekenen) {
            Toast.makeText(this, "ZO VEEL GELD MAN", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Als de back toets (aangemaakt met setDisplayHomeAsUpEnabled) wordt ingedrukt
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void updateTotaal() {
        double totaal = 0;
        for (Consumptielijn consumptielijn : consumptielijnen) {
            totaal += consumptielijn.getConsumptie().getPrijs() * consumptielijn.getAantal();
        }
        txtTotaal.setText(numberFormatter.format(totaal));
    }

    @Override
    public void addConsumptielijn(int positie, Consumptie consumptie) {
        consumptielijnen.add(positie, new Consumptielijn(consumptie));
    }

    @Override
    public String updateAantal(int positie, int aantal) {
        Consumptielijn consumptielijn = consumptielijnen.get(positie);
        consumptielijn.setAantal(aantal);
        updateTotaal();
        return numberFormatter.format(consumptielijn.getConsumptie().getPrijs() * aantal);
    }
}
