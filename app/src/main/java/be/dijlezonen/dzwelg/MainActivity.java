package be.dijlezonen.dzwelg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.Locale;

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
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private ProgressDialog progressDialog;
    private OnCompleteListener<AuthResult> firebaseAuthOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            progressDialog.dismiss();
            Log.d(LOG_TAG, "Firebase auth result: " + task.isSuccessful());

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user is handled in the listener.
            if (!task.isSuccessful()) {
                FirebaseCrash.logcat(Log.ERROR, LOG_TAG, "Firebase auth failed");
                FirebaseCrash.report(task.getException());
                String toastMessage = getString(R.string.fout_onbekend);
                if (task.getException() != null) {
                    toastMessage = task.getException().getMessage();
                }
                Toast.makeText(
                        MainActivity.this,
                        String.format(Locale.getDefault(), "%s %s", getString(R.string.aanmelden_mislukt), toastMessage),
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get firebase authorisation
        firebaseAuth = FirebaseAuth.getInstance();

        //listen to firebase authorisation state to check when user is logged in
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in, start EventListActivity
                    signInSucces();
                } else {
                    // user is signed out
                }
            }
        };

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthStateListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
        }
    }

    private void signInSucces() {
        Intent main = new Intent(MainActivity.this, EventListActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //clear top of the stack, making EventListActivity the first in the backstack
        startActivity(main);
    }

    @OnClick(R.id.btnLogin)
    public void signIn() {
        if (validateFields()) {
            showProgressDialog();

            String email = mTxtEmail.getText().toString();
            String wachtwoord = mTxtWachtwoord.getText().toString();

            firebaseAuth.signInWithEmailAndPassword(email, wachtwoord)
                    .addOnCompleteListener(this, firebaseAuthOnCompleteListener);

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
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.bezig_aanmelden));
        progressDialog.show();
    }
}
