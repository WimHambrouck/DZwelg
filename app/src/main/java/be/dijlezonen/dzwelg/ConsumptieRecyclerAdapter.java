package be.dijlezonen.dzwelg;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import be.dijlezonen.dzwelg.models.Consumptie;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsumptieRecyclerAdapter extends FirebaseRecyclerAdapter<Consumptie, ConsumptieRecyclerAdapter.ConsumptieViewHolder> {
    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public ConsumptieRecyclerAdapter(Class<Consumptie> modelClass, int modelLayout, Class<ConsumptieViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(ConsumptieViewHolder viewHolder, Consumptie model, int position) {
        viewHolder.txtConsumptieNaam.setText(model.getNaam());
    }

    public static class ConsumptieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_consumptie_naam)
        TextView txtConsumptieNaam;
        @BindView(R.id.edit_hoeveelheid)
        EditText editHoeveelheid;
        @BindView(R.id.btn_min)
        ImageButton btnMin;
        @BindView(R.id.btn_plus)
        ImageButton btnPlus;

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
