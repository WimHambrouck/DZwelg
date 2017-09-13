package be.dijlezonen.dzwelg.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.models.Rollen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.btnLogin)
    Button mLoginButton;
    @BindView(R.id.txtEmail)
    EditText mTxtEmail;
    @BindView(R.id.txtWachtwoord)
    EditText mTxtWachtwoord;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthStateListener;
    private ProgressDialog mProgressDialog;
    private OnCompleteListener<AuthResult> mFirebaseAuthOnCompleteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebaseAuth();

        ButterKnife.bind(this);
    }

    private void initFirebaseAuth() {
        mFirebaseAuthOnCompleteListener = new AuthCompleteListener();

        // get firebase authorisation
        mFirebaseAuth = FirebaseAuth.getInstance();

        //listen to firebase authorisation state to check when user is logged in
        mFirebaseAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // user is signed in, get corresponding user from db and check role
                DatabaseReference lidRef = FirebaseDatabase.getInstance()
                        .getReference(getString(R.string.ref_leden))
                        .child(user.getUid())
                        .child(getString(R.string.ref_rollen))
                        .child(Rollen.Kassaverantwoordelijke);

                lidRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean kassamens = false;
                        if(dataSnapshot.exists())
                        {
                            kassamens = dataSnapshot.getValue(Boolean.class);
                        }
                        if(kassamens)
                        {
                            signInSucces();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(android.R.string.dialog_alert_title);
                            builder.setMessage(R.string.fout_gebruiker_geen_rechten);
                            builder.setNeutralButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
                            builder.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this,
                                String.format(
                                        getString(R.string.fout_database_verbinding),
                                        databaseError.getMessage()
                                ),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFirebaseAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthStateListener);
        }
    }

    private void signInSucces() {
        Intent main = new Intent(MainActivity.this, EventListActivity.class);
        startActivity(main);
        finish();
    }

    @OnClick(R.id.btnLogin)
    public void signIn() {
        if (validateFields()) {
            showProgressDialog();

            String email = mTxtEmail.getText().toString();
            String wachtwoord = mTxtWachtwoord.getText().toString();

            mFirebaseAuth.signInWithEmailAndPassword(email, wachtwoord)
                    .addOnCompleteListener(this, mFirebaseAuthOnCompleteListener);

        }
    }

    private boolean validateFields() {
        boolean correct = true;
        int minPasswordLength = 6;
        String email = mTxtEmail.getText().toString();
        String password = mTxtWachtwoord.getText().toString();

        //email matching: http://stackoverflow.com/a/9225678/3115379
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mTxtEmail.setError(getString(R.string.ongeldig_email));
            correct = false;
        } else {
            mTxtEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < minPasswordLength) {
            mTxtWachtwoord.setError(getString(R.string.wachtwoord_te_kort));
            correct = false;
        } else {
            mTxtWachtwoord.setError(null);
        }

        return correct;
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.bezig_aanmelden));
        mProgressDialog.show();
    }

    private class AuthCompleteListener implements OnCompleteListener<AuthResult> {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            mProgressDialog.dismiss();
            Log.d(LOG_TAG, "Firebase auth result: " + task.isSuccessful());

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user is handled in the listener.
            if (!task.isSuccessful()) {
                FirebaseCrash.logcat(Log.ERROR, LOG_TAG, "Firebase auth failed");
                FirebaseCrash.report(task.getException());
                String toastMessage = getString(R.string.fout_onbekend);
                //noinspection ThrowableResultOfMethodCallIgnored
                Throwable t = task.getException();
                if (t != null) {
                    toastMessage = t.getMessage();
                    FirebaseCrash.report(task.getException());
                }
                Toast.makeText(
                        MainActivity.this,
                        String.format(Locale.getDefault(), "%s %s", getString(R.string.aanmelden_mislukt), toastMessage),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
