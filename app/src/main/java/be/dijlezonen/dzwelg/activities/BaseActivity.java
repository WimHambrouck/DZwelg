package be.dijlezonen.dzwelg.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private FirebaseUser kassaUser;

    public FirebaseUser getKassaUser(){ return kassaUser; }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        kassaUser = firebaseAuth.getCurrentUser();
        if(kassaUser == null)
        {
            finish();
        }
    }
}
