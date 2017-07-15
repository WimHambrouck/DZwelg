package be.dijlezonen.dzwelg.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.fragments.UserDetailFragment;
import be.dijlezonen.dzwelg.models.Lid;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

/**
 * An activity representing a list of Users. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class UserListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String LOG_TAG = UserListActivity.class.getSimpleName();
    public static final String EXTRA_LID_ID = "LID_ID";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private LedenRecyclerViewAdapter mAdapter;

    private List<Lid> mLeden;
    private List<Lid> mGefilterdeLeden;

    private Lid mActiefLid;
    private ChildEventListener mLedenEventListener;


    @BindView(R.id.search_view)
    SearchView searchView;

    @BindView(R.id.fab_menu)
    FloatingActionMenu mFab;

    @BindView(R.id.fab_menu_credit)
    FloatingActionButton mFabCredit;

    @BindView(R.id.fab_menu_debit)
    FloatingActionButton mFabDebit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        initActionBar();
        showProgressDialog();
        setupLedenEventListener();
        setupRecyclerView();

        if (findViewById(R.id.user_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        ButterKnife.bind(this);

        // TODO: 15/07/2017  aparte listener met callbaks
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        mFabCredit.setOnClickListener(v -> {
            Intent creditActivity = new Intent(UserListActivity.this, CreditActivity.class);
            creditActivity.putExtra(EXTRA_LID_ID, mActiefLid.getId());
            startActivity(creditActivity);
            mFab.close(false);
        });

        mFabDebit.setOnClickListener(view -> {
            Intent verkoopActivity = new Intent(UserListActivity.this, VerkoopActivity.class);
            verkoopActivity.putExtra(EXTRA_LID_ID, mActiefLid.getId());
            startActivity(verkoopActivity);
            mFab.close(false);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        filterLedenLijst(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.v(LOG_TAG, "Query changed: " + newText);
        filterLedenLijst(newText);
        return true;
    }

    @Override
    public boolean onClose() {
        Log.d(LOG_TAG, "search closed");
        mGefilterdeLeden = mLeden;
        return false;
    }

    private void filterLedenLijst(String query) {
        ArrayList<Lid> tempLijst = new ArrayList<>();

        for (Lid lid : mLeden) {
            if (lid.getVolledigeNaam().toLowerCase().contains(query.toLowerCase())) {
                tempLijst.add(lid);
            }
        }

        mGefilterdeLeden = tempLijst;
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.even_wachten);
        mProgressDialog.setMessage(getString(R.string.laden_leden));
        mProgressDialog.show();
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra(getString(R.string.extra_event_title)));
        }
    }

    private void setupRecyclerView() {
        mLeden = new ArrayList<>();
        mGefilterdeLeden = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.user_list);
        mAdapter = new LedenRecyclerViewAdapter(mGefilterdeLeden);
        DatabaseReference mLedenRef = FirebaseDatabase.getInstance().getReference(getString(R.string.ref_leden));
        Query q = mLedenRef.orderByChild("voornaam");
        q.addChildEventListener(mLedenEventListener);
    }

    private void setupLedenEventListener() {
        mLedenEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                Lid nieuwLid = dataSnapshot.getValue(Lid.class);
                nieuwLid.setId(dataSnapshot.getKey());
                mLeden.add(nieuwLid);
                mGefilterdeLeden = mLeden;
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //gezien we enkel naam weergeven en veranderen van naam niet mag, is dit geen use
                //case. Eventuele andere info (zoals bedrag) die wordt weergegeven, wordt
                //geüpdate door UserDetailFragment
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Lid lid = dataSnapshot.getValue(Lid.class);
                if (lid.equals(mActiefLid)) {
                    mActiefLid = null;
                }
                mLeden.remove(lid);
                mGefilterdeLeden = mLeden;
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(LOG_TAG, "child moved: " + s);
                FirebaseCrash.log("onChildMoved getriggerd in UserListActivity!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "mLedenEventListener:onCancelled", databaseError.toException());
                FirebaseCrash.report(databaseError.toException());
                Toast.makeText(UserListActivity.this, R.string.fout_inladen_leden, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private class LedenRecyclerViewAdapter
            extends RecyclerView.Adapter<UserListActivity.LidViewHolder> {

        LedenRecyclerViewAdapter(List<Lid> leden) {

        }

        @Override
        public UserListActivity.LidViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_list_content, parent, false);
            return new UserListActivity.LidViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final UserListActivity.LidViewHolder holder, int position) {
            final Lid lid = mGefilterdeLeden.get(position);
            holder.mTxtLidNaam.setText(lid.getVolledigeNaam());

            if (lid.isActiefInLijst()) {
                holder.mView.setBackgroundColor(getColor(R.color.colorPrimary));
                holder.mTxtLidNaam.setTextColor(Color.WHITE);
            }

            holder.mView.setOnClickListener(view -> {
                // collapse search view (zodra we op een lid krijgen, mag de filtering weg)
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(UserDetailFragment.ARG_ITEM_ID, lid.getId());
                    UserDetailFragment fragment = new UserDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.user_detail_container, fragment)
                            .commit();
                    zetActief(lid);
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra(UserDetailFragment.ARG_ITEM_ID, lid.getId());
                    context.startActivity(intent);
                }
            });
        }

        /**
         * Zorgt ervoor dat het lid actief wordt gezet + refresh van adapter zodat we in de view
         * zien dat het desbetreffende lid geslecteerd is.
         *
         * @param lid Het te selecteren lid
         */
        private void zetActief(Lid lid) {
            /* als de fab (credit/debit) niet zichtbaar is (= standaard niet, omdat bij aanvang
             * scherm er niemand gesecteerd is), dan zichtbaar maken + dichtklappen indien opengeklapt
             */
            if (mFab.getVisibility() == View.INVISIBLE) {
                mFab.setVisibility(View.VISIBLE);
            }

            mFab.close(true);

            if (mActiefLid != null) {
                mLeden.get(mLeden.indexOf(mActiefLid)).setActiefInLijst(false);
            }
            mActiefLid = lid;
            mLeden.get(mLeden.indexOf(mActiefLid)).setActiefInLijst(true);

            mRecyclerView.setAdapter(mAdapter);
        }

        @Override
        public int getItemCount() {
            return mGefilterdeLeden.size();
        }
    }

    static class LidViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.user_list_lid_naam)
        TextView mTxtLidNaam;

        LidViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
