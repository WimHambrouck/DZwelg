package be.dijlezonen.dzwelg.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.activities.UserDetailActivity;
import be.dijlezonen.dzwelg.activities.UserListActivity;
import be.dijlezonen.dzwelg.models.Lid;
import be.dijlezonen.dzwelg.models.Transactie;
import be.dijlezonen.dzwelg.models.transacties.CreditTransactie;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single User detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_USER_ID = "user_id";

    private Lid mLid;

    @BindView(R.id.user_detail_saldo)
    TextView mTxtSaldo;


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
            updateViews();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

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

        if (mLid != null) {
            updateViews();
        }

        return rootView;
    }

    private void updateViews() {
        if (mLid != null) {
            mTxtSaldo.setText(mLid.getSaldoGeformatteerd());

        } else {
            mTxtSaldo.setText("");
        }
    }
}
