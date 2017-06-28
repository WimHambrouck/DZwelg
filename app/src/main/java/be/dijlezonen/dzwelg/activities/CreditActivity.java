package be.dijlezonen.dzwelg.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import be.dijlezonen.dzwelg.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class CreditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        ButterKnife.bind(this);
    }


    @OnClick({R.id.btnTien, R.id.btnTwintig, R.id.btnVijftig, R.id.btnHonderd})
    public void creditButtonClicked(View v) {
        laadBedrag(Double.parseDouble(((Button) v).getText().toString()));
    }

    private void laadBedrag(double bedrag) {
        Toast.makeText(this, "Bedrag: " + bedrag, Toast.LENGTH_SHORT).show();
    }
}
