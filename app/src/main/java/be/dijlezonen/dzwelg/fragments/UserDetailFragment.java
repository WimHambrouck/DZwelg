package be.dijlezonen.dzwelg.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.activities.UserDetailActivity;
import be.dijlezonen.dzwelg.activities.UserListActivity;
import be.dijlezonen.dzwelg.adapters.TransactieArrayAdapter;
import be.dijlezonen.dzwelg.models.Consumptie;
import be.dijlezonen.dzwelg.models.Consumptielijn;
import be.dijlezonen.dzwelg.models.Lid;
import be.dijlezonen.dzwelg.models.Transactie;
import be.dijlezonen.dzwelg.models.transacties.CreditTransactie;
import be.dijlezonen.dzwelg.models.transacties.DebitTransactie;
import be.dijlezonen.dzwelg.models.transacties.UndoTransactie;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single User detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends Fragment {

    public static final String ARG_USER_ID = "user_id";

    private Lid mLid;

    @BindView(R.id.user_detail_saldo)
    TextView mTxtSaldo;

    @BindView(R.id.user_detail_transactie_list)
    ListView mTransactieLijst;


    private CollapsingToolbarLayout mAppBarLayout;

    public UserDetailFragment() {
        /*
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
    }

    private ValueEventListener lidListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mLid = dataSnapshot.getValue(Lid.class);

            Iterable<DataSnapshot> dataSnapshots = dataSnapshot.child("transacties").getChildren();

            for (DataSnapshot snapshot : dataSnapshots)
            {
                Transactie.TransactieSoort soort =
                        snapshot.child("soort").getValue(Transactie.TransactieSoort.class);

                assert soort != null;

                switch (soort)
                {
                    case CREDIT:
                        CreditTransactie creditTransactie = snapshot.getValue(CreditTransactie.class);
                        mLid.getTransacties().add(creditTransactie);
                        break;
                    case DEBIT:
                        DebitTransactie debitTransactie = snapshot.getValue(DebitTransactie.class);
                        mLid.getTransacties().add(debitTransactie);
                        break;
                    case UNDO:
                        UndoTransactie undoTransactie = snapshot.getValue(UndoTransactie.class);
                        mLid.getTransacties().add(undoTransactie);
                        break;
                }
            }
            updateViews();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void setUpAppBar() {
        Activity activity = this.getActivity();
        mAppBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_USER_ID)) {
            setUpAppBar();
            String userId = getArguments().getString(ARG_USER_ID);
            if (userId != null) {
                DatabaseReference mLedenRef;
                mLedenRef = FirebaseDatabase.getInstance().getReference(getString(R.string.ref_leden)).child(userId);
                mLedenRef.addValueEventListener(lidListener);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    private void updateViews() {
        mTxtSaldo.setText(mLid.getSaldoGeformatteerd());
        List<Transactie> transactieCopy = mLid.getTransacties().subList(0, mLid.getTransacties().size());
        Collections.reverse(transactieCopy);
        TransactieArrayAdapter arrayAdapter =
                new TransactieArrayAdapter(getContext(), R.layout.user_detail_transactie_item, transactieCopy);
        mTransactieLijst.setAdapter(arrayAdapter);
    }
}
