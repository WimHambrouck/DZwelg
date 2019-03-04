package be.dijlezonen.dzwelg.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import java.util.Locale;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.models.Consumptie;
import be.dijlezonen.dzwelg.models.ConsumptieViewHolder;

public class ConsumptieRecyclerAdapter extends FirebaseRecyclerAdapter<Consumptie, ConsumptieViewHolder> {

    private static final int MAX_BESTELAANTAL = 100;
    private ConsumptieRecyclerAdapterCallback callback;

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     *
    public ConsumptieRecyclerAdapter(Class<Consumptie> modelClass, int modelLayout, Class<ConsumptieViewHolder> viewHolderClass, Query ref, ConsumptieRecyclerAdapterCallback callback) {
        this.layout = modelLayout;
        this.callback = callback;
    }*/

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ConsumptieRecyclerAdapter(@NonNull FirebaseRecyclerOptions<Consumptie> options, ConsumptieRecyclerAdapterCallback consumptieRecyclerAdapterCallback) {
        super(options);
        this.callback = consumptieRecyclerAdapterCallback;
    }


    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onBindViewHolder(@NonNull ConsumptieViewHolder viewHolder, int position, @NonNull Consumptie model) {
        callback.addConsumptielijn(position, model);

        viewHolder.txtConsumptieNaam.setText(model.getNaam());

        viewHolder.editHoeveelheid.addTextChangedListener(new HoeveelheidWatcher(viewHolder, position));

        viewHolder.btnPlus.setOnClickListener(v -> {
            viewHolder.editHoeveelheid.requestFocus();
            hideKeyboard(viewHolder.getView());
            int aantal = Integer.parseInt(viewHolder.editHoeveelheid.getText().toString());
            if (aantal < MAX_BESTELAANTAL) {
                viewHolder.editHoeveelheid.setText(String.format(Locale.getDefault(), "%d", ++aantal));
            }
        });

        viewHolder.btnMin.setOnClickListener(v -> {
            viewHolder.editHoeveelheid.requestFocus();
            hideKeyboard(viewHolder.getView());
            int aantal = Integer.parseInt(viewHolder.editHoeveelheid.getText().toString());
            if (aantal > 0) {
                viewHolder.editHoeveelheid.setText(String.format(Locale.getDefault(), "%d", --aantal));
            }
        });
    }

    @NonNull
    @Override
    public ConsumptieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.consumptie_item, parent, false);
        return new ConsumptieViewHolder(view);
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
            int aantal = 0;

            try {
                aantal = Integer.parseInt(s.toString());
            } catch (NumberFormatException e) {
                //als het
                consumptieViewHolder.editHoeveelheid.setText("0");
            }

            if (aantal > MAX_BESTELAANTAL) {
                consumptieViewHolder.editHoeveelheid.setText(String.format(Locale.getDefault(), "%d", MAX_BESTELAANTAL));
            } else {
                consumptieViewHolder.txtSubtotaal.setText(callback.updateAantal(position, aantal));
            }
        }
    }

    public interface ConsumptieRecyclerAdapterCallback {
        /**
         * Voegt consumptielijn toe om gemakkelijk (sub)tota(a)l(en) te kunnen berekenen
         * (anders moet er geïtereerd over alle views in de adapter)
         *
         * @param positie    Positie van de consumptielijn in de RecyclerView
         * @param consumptie Toe te voegen consumptie
         */
        void addConsumptielijn(int positie, Consumptie consumptie);

        /**
         * Update het aantal van een consumptie in een consumptielijn
         *
         * @param positie Positie van de te updaten consumptie
         * @param aantal  Nieuw aantal van deze consumptie
         * @return Prijs van de consumptie * aantal
         */
        String updateAantal(int positie, int aantal);
    }
}
