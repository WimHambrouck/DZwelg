package be.dijlezonen.dzwelg.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class UserListActivity extends AppCompatActivity {

    private static final String LOG_TAG = UserListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private SimpleItemRecyclerViewAdapter mAdapter;

    private List<Lid> mLeden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra(getString(R.string.extra_event_title)));
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.even_wachten);
        mProgressDialog.setMessage(getString(R.string.laden_gebruikers));
        mProgressDialog.show();

        mLeden = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.user_list);
        mAdapter = new SimpleItemRecyclerViewAdapter(mLeden);
        setupRecyclerView();

        if (findViewById(R.id.user_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
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

    private void setupRecyclerView() {
        DatabaseReference ledenRef = FirebaseDatabase.getInstance().getReference(getString(R.string.ref_leden));
        Query q = ledenRef.orderByChild("voornaam");

        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                mLeden.add(dataSnapshot.getValue(Lid.class));
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Lid lid = dataSnapshot.getValue(Lid.class);
                mLeden.set(mLeden.indexOf(lid), lid);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mLeden.remove(dataSnapshot.getValue(Lid.class));
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(LOG_TAG, "child moved: " + s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, databaseError.getMessage());
            }
        });
    }

    private class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<LidViewHolder> {

        private final List<Lid> mLeden;

        SimpleItemRecyclerViewAdapter(List<Lid> leden) {
            mLeden = leden;
        }

        @Override
        public LidViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_list_content, parent, false);
            return new LidViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final LidViewHolder holder, int position) {
            final Lid lid = mLeden.get(position);
            holder.mTxtLidNaam.setText(lid.getVolledigeNaam());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(UserDetailFragment.ARG_ITEM_ID, lid.getId());
                        UserDetailFragment fragment = new UserDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.user_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, UserDetailActivity.class);
                        intent.putExtra(UserDetailFragment.ARG_ITEM_ID, lid.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mLeden.size();
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
