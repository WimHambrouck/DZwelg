package be.dijlezonen.dzwelg.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.exceptions.BedragException;
import be.dijlezonen.dzwelg.fragments.EigenBedragDialogFragment;
import be.dijlezonen.dzwelg.models.Lid;
import be.dijlezonen.dzwelg.models.transacties.CreditTransactie;
import be.dijlezonen.dzwelg.models.transacties.UndoTransactie;
import butterknife.ButterKnife;
import butterknife.OnClick;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class CreditActivity extends AppCompatActivity implements EigenBedragDialogFragment.EigenBedragDialogListener {

    private static final String LOG_TAG = CreditActivity.class.getSimpleName();
    private Lid mLid;
    private DatabaseReference mLidRef;
    private String mEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        initActionBar();
        initLid();

        ButterKnife.bind(this);
    }

    private void initLid() {
        Bundle myExtras = getIntent().getExtras();
        String lidId = myExtras.getString(getString(R.string.extra_lid_id));
        mEventId = myExtras.getString(getString(R.string.extra_event_id));

        if (lidId != null) {
            mLidRef = FirebaseDatabase.getInstance().getReference(getString(R.string.ref_leden)).child(lidId);

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
    }

    @OnClick({R.id.btnTien, R.id.btnTwintig, R.id.btnVijftig, R.id.btnHonderd})
    public void creditButtonClicked(View v) {
        laadBedrag(Double.parseDouble(((Button) v).getText().toString()));
    }

    @OnClick(R.id.btnEigenBedrag)
    public void eigenBedrag() {
        DialogFragment dialog = new EigenBedragDialogFragment();
        dialog.show(getFragmentManager(), "dialog");
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    private void laadBedrag(double bedrag) {
        laadBedrag(bedrag, false);
    }

    private void laadBedrag(double bedrag, boolean override) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (!override && bedrag > 100) {
            builder.setTitle(R.string.zeker)
                    .setMessage(String.format(getString(R.string.invoeren_groot_bedrag), bedrag))
                    .setPositiveButton(android.R.string.yes, ((dialog, which) -> laadBedrag(bedrag, true)))
                    .setNegativeButton(android.R.string.no, ((dialog, which) -> dialog.dismiss()))
                    .create().show();
        } else {
            try {
                mLid.creditSaldo(bedrag);

                //nieuwe creditTransactie om weg te schrijven naar transacties van gebruiker
                CreditTransactie creditTransactie = new CreditTransactie(mLid.getId(), bedrag, mEventId);
                //transacties als key timestamp meegeven voor snellere ophaling later
                DatabaseReference newTransactie = mLidRef
                        .child(getString(R.string.ref_transacties))
                        .child(String.valueOf(creditTransactie.getTimestampForKey())); //timestamp is geïnverteerd, dus moet terug positief worden gemaakt voor de key
                newTransactie.setValue(creditTransactie);

                mLidRef.child(getString(R.string.ref_child_saldo)).setValue(mLid.getSaldo());

                // toevoegen transactie aan lijst met "dirty" transacties
                DatabaseReference refDirtyTransacties = FirebaseDatabase.getInstance()
                        .getReference(getString(R.string.ref_transacties_dirty));
                refDirtyTransacties.push().setValue(creditTransactie);

                refDirtyTransacties.child(getString(R.string.ref_totaal)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Double totaal = dataSnapshot.getValue(Double.class);
                        if(totaal == null)
                        {
                            totaal = creditTransactie.getBedrag();
                        } else {
                            totaal+= creditTransactie.getBedrag();
                        }

                        refDirtyTransacties.child(getString(R.string.ref_totaal)).setValue(totaal);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOG_TAG, "Firebase derped out: " + databaseError.getMessage());
                        FirebaseCrash.log("Fout (onCancelled) bij updaten totaal in transacties_dirty: " + databaseError.getMessage());
                    }
                });

                Toast.makeText(CreditActivity.this, getString(R.string.success_opgeladen, bedrag), Toast.LENGTH_SHORT).show();
                finish();
            } catch (BedragException e) {
                builder.setTitle("Fout!")
                        .setMessage(e.getMessage())
                        .setNeutralButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        }
    }

    @Override
    public void onDialogPositiveClick(EigenBedragDialogFragment dialog) {
        laadBedrag(dialog.getIngevoerdBedrag());
    }
}
