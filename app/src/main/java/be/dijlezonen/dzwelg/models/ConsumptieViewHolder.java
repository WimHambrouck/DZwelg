package be.dijlezonen.dzwelg.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import be.dijlezonen.dzwelg.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsumptieViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.txt_consumptie_naam)
    public TextView txtConsumptieNaam;
    @BindView(R.id.edit_hoeveelheid)
    public EditText editHoeveelheid;
    @BindView(R.id.btn_min)
    public ImageButton btnMin;
    @BindView(R.id.btn_plus)
    public ImageButton btnPlus;

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
