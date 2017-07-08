package be.dijlezonen.dzwelg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import be.dijlezonen.dzwelg.models.Consumptie;
import butterknife.BindView;
import butterknife.ButterKnife;

@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class VerkoopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verkoop);

        DatabaseReference consumptieRef = FirebaseDatabase.getInstance().getReference("consumpties");

        FirebaseRecyclerAdapter<Consumptie, ConsumptieViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Consumptie, ConsumptieViewHolder>(
                        Consumptie.class,
                        R.layout.consumptie_item,
                        ConsumptieViewHolder.class,
                        consumptieRef) {
                    @Override
                    protected void populateViewHolder(ConsumptieViewHolder viewHolder, Consumptie model, int position) {
                        viewHolder.txtConsumptieNaam.setText(model.getNaam());
                        viewHolder.getView().setOnClickListener(v -> Toast.makeText(VerkoopActivity.this, model.getNaam(), Toast.LENGTH_SHORT).show());
                    }
                };

        LinearLayoutManager llm = new LinearLayoutManager(this);
        RecyclerView queueRecyclerView = (RecyclerView) this.findViewById(R.id.consumpties_recycler_view);
        queueRecyclerView.setLayoutManager(llm);
        queueRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    /**
     * Class moet public zijn voor Firebase, daarmee SuppressWarnings
     */
    @SuppressWarnings("WeakerAccess")
    public static class ConsumptieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_consumptie_naam)
        TextView txtConsumptieNaam;

        private View view;

        public ConsumptieViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }

        public View getView() {
            return view;
        }
    }
}
