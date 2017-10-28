package be.dijlezonen.dzwelg.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.models.Transactie;

/**
 * Created by Wim on 07/10/2017.
 */

public class TransactieArrayAdapter extends ArrayAdapter<Transactie> {

    private final int mResource;
    private final List<Transactie> transacties;

    public TransactieArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Transactie> objects) {
        super(context, resource, objects);
        mResource = resource;
        transacties = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;

        if(row == null)
        {
            //inflate layout if not yet inflated
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(mResource, parent, false);
        }


        TextView item = (TextView) row.findViewById(R.id.transactie_item_text);
        item.setText(transacties.get(position).toString());


        return row;
    }
}
