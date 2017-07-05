package be.dijlezonen.dzwelg.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.exceptions.BedragException;
import be.dijlezonen.dzwelg.models.Lid;
import butterknife.ButterKnife;
import butterknife.OnClick;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class CreditActivity extends AppCompatActivity {

    private static final String LOG_TAG = CreditActivity.class.getSimpleName();
    private Lid mLid;
    private DatabaseReference mLidRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        Intent myIntent = getIntent();
        String lidId = myIntent.getExtras().getString(UserListActivity.EXTRA_LID_ID);

        if (lidId != null) {
            mLidRef = FirebaseDatabase.getInstance().getReference("leden").child(lidId);

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

        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnTien, R.id.btnTwintig, R.id.btnVijftig, R.id.btnHonderd})
    public void creditButtonClicked(View v) {
        laadBedrag(Double.parseDouble(((Button) v).getText().toString()));
    }

    private void laadBedrag(double bedrag) {
        try {
            mLid.updateSaldo(bedrag);
            mLidRef.child("saldo").setValue(mLid.getSaldo());
            finish();
        } catch (BedragException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Fout!")
                    .setMessage(e.getMessage());
        }
    }
}
