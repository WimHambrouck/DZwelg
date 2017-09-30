package be.dijlezonen.dzwelg.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.adapters.ConsumptieRecyclerAdapter;
import be.dijlezonen.dzwelg.exceptions.BedragException;
import be.dijlezonen.dzwelg.exceptions.SaldoOntoereikendException;
import be.dijlezonen.dzwelg.models.Consumptie;
import be.dijlezonen.dzwelg.models.ConsumptieViewHolder;
import be.dijlezonen.dzwelg.models.Consumptielijn;
import be.dijlezonen.dzwelg.models.Lid;
import be.dijlezonen.dzwelg.models.transacties.DebitTransactie;
import butterknife.BindView;
import butterknife.ButterKnife;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class VerkoopActivity extends AppCompatActivity implements ConsumptieRecyclerAdapter.ConsumptieRecyclerAdapterCallback {

    private static final String LOG_TAG = VerkoopActivity.class.getSimpleName();
    private NumberFormat numberFormatter;
    private List<Consumptielijn> consumptielijnen;
    private Lid mLid;
    private DatabaseReference mLidRef;
    private double mTotaal;


    @BindView(R.id.txt_totaalbedrag)
    TextView txtTotaal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verkoop);

        Intent intent = getIntent();
        String lidId = intent.getExtras().getString(getString(R.string.extra_lid_id));

        if (lidId != null) {
            mLidRef = FirebaseDatabase.getInstance()
                    .getReference(getString(R.string.ref_leden))
                    .child(lidId);
            mLidRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mLid = dataSnapshot.getValue(Lid.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseCrash.report(new DatabaseException(databaseError.getMessage()));
                    Log.e(LOG_TAG, databaseError.toString());
                }
            });
        } else {
            finish();
        }

        initActionBar();

        ButterKnife.bind(this);

        consumptielijnen = new ArrayList<>();
        numberFormatter = NumberFormat.getCurrencyInstance();
        numberFormatter.setCurrency(Currency.getInstance("EUR"));

        initConsumptieLijst();
    }

    private void initConsumptieLijst() {
        Query consumptieRef = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.ref_consumpties))
                .orderByChild(getString(R.string.ref_child_naam));

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
            try {
                mLid.debitSaldo(mTotaal);
                mLidRef.child(getString(R.string.ref_child_saldo)).setValue(mLid.getSaldo());

                DebitTransactie debitTransactie = new DebitTransactie(mLid.getId(), consumptielijnen, mTotaal);
                DatabaseReference debitRef = mLidRef
                        .child(getString(R.string.ref_transacties))
                        .child(String.valueOf(debitTransactie.getTimestampForKey()));
                debitRef.setValue(debitTransactie);

                Toast.makeText(VerkoopActivity.this, getString(R.string.success_afgetrokken, mTotaal), Toast.LENGTH_SHORT).show();
                finish();
            } catch (SaldoOntoereikendException e) {
                Toast.makeText(VerkoopActivity.this, "saldo ontoereikend", Toast.LENGTH_SHORT).show();
            } catch (BedragException e) {
                Toast.makeText(VerkoopActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
        mTotaal = totaal;
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
