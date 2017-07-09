package be.dijlezonen.dzwelg.adapters;

import android.text.Editable;
import android.text.TextWatcher;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import be.dijlezonen.dzwelg.models.Consumptie;
import be.dijlezonen.dzwelg.models.ConsumptieViewHolder;
import be.dijlezonen.dzwelg.models.Consumptielijn;

public class ConsumptieRecyclerAdapter extends FirebaseRecyclerAdapter<Consumptie, ConsumptieViewHolder> {
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
        consumptielijnen = new ArrayList<>();
    }

    private List<Consumptielijn> consumptielijnen;

    @Override
    protected void populateViewHolder(ConsumptieViewHolder viewHolder, Consumptie model, int position) {
        consumptielijnen.add(position, new Consumptielijn(model, 0));

        viewHolder.txtConsumptieNaam.setText(model.getNaam());

        viewHolder.editHoeveelheid.setText(String.format(Locale.getDefault(), "%d", consumptielijnen.get(position).getAantal()));
        viewHolder.editHoeveelheid.addTextChangedListener(new HoeveelheidWatcher(position));


        viewHolder.btnPlus.setOnClickListener(v -> {
            int aantal = Integer.parseInt(viewHolder.editHoeveelheid.getText().toString());
            viewHolder.editHoeveelheid.setText(String.format(Locale.getDefault(), "%d", ++aantal));
        });

        viewHolder.btnMin.setOnClickListener(v -> {
            int aantal = Integer.parseInt(viewHolder.editHoeveelheid.getText().toString());
            if (aantal > 0)
                viewHolder.editHoeveelheid.setText(String.format(Locale.getDefault(), "%d", --aantal));
        });

    }

    private class HoeveelheidWatcher implements TextWatcher {

        private int position;

        HoeveelheidWatcher(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //niet gebruikt
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //niet gebruikt
        }

        @Override
        public void afterTextChanged(Editable s) {
            consumptielijnen.get(position).setAantal(Integer.parseInt(s.toString()));
        }
    }
}
