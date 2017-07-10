package be.dijlezonen.dzwelg.adapters;

import android.text.Editable;
import android.text.TextWatcher;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import be.dijlezonen.dzwelg.models.Consumptie;
import be.dijlezonen.dzwelg.models.ConsumptieViewHolder;
import be.dijlezonen.dzwelg.models.Consumptielijn;

public class ConsumptieRecyclerAdapter extends FirebaseRecyclerAdapter<Consumptie, ConsumptieViewHolder> {

    private NumberFormat numberFormatter;

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
        numberFormatter = NumberFormat.getCurrencyInstance();
        numberFormatter.setCurrency(Currency.getInstance("EUR"));
    }

    private List<Consumptielijn> consumptielijnen;

    @Override
    protected void populateViewHolder(ConsumptieViewHolder viewHolder, Consumptie model, int position) {
        consumptielijnen.add(position, new Consumptielijn(model, 0));

        viewHolder.txtConsumptieNaam.setText(model.getNaam());

        viewHolder.editHoeveelheid.addTextChangedListener(new HoeveelheidWatcher(viewHolder, position));


        viewHolder.btnPlus.setOnClickListener(v -> {
            int aantal = Integer.parseInt(viewHolder.editHoeveelheid.getText().toString());
            viewHolder.editHoeveelheid.setText(String.format(Locale.getDefault(), "%d", ++aantal));
        });

        viewHolder.btnMin.setOnClickListener(v -> {
            int aantal = Integer.parseInt(viewHolder.editHoeveelheid.getText().toString());
            if (aantal > 0) {
                viewHolder.editHoeveelheid.setText(String.format(Locale.getDefault(), "%d", --aantal));
            }
        });
    }

    private class HoeveelheidWatcher implements TextWatcher {

        private ConsumptieViewHolder consumptieViewHolder;
        private int position;

        HoeveelheidWatcher(ConsumptieViewHolder consumptieViewHolder, int position) {
            this.consumptieViewHolder = consumptieViewHolder;
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
            int aantal = Integer.parseInt(s.toString());
            Consumptielijn consumptielijn = consumptielijnen.get(position);
            consumptielijn.setAantal(aantal);
            consumptieViewHolder.txtSubtotaal.setText(numberFormatter.format(aantal * consumptielijn.getConsumptie().getPrijs()));
        }
    }
}
