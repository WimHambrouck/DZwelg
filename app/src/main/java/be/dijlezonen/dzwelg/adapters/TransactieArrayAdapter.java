package be.dijlezonen.dzwelg.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import be.dijlezonen.dzwelg.R;
import be.dijlezonen.dzwelg.exceptions.SaldoOntoereikendException;
import be.dijlezonen.dzwelg.models.ICanBeUndone;
import be.dijlezonen.dzwelg.models.Lid;
import be.dijlezonen.dzwelg.models.Transactie;
import be.dijlezonen.dzwelg.models.transacties.CreditTransactie;
import be.dijlezonen.dzwelg.models.transacties.DebitTransactie;
import be.dijlezonen.dzwelg.models.transacties.UndoTransactie;

public class TransactieArrayAdapter extends ArrayAdapter<Transactie> {

    private final int mResource;
    private final List<Transactie> transacties;
    private Lid mLid;

    public TransactieArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Transactie> objects, Lid lid) {
        super(context, resource, objects);
        mResource = resource;
        transacties = objects;
        mLid = lid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            //inflate layout if not yet inflated
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(mResource, parent, false);
        }


        TextView item = (TextView) row.findViewById(R.id.transactie_item_text);
        item.setText(transacties.get(position).toString());

        LinearLayout linearLayout = (LinearLayout) row.findViewById(R.id.ll_transactie_item);
        linearLayout.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.dialog_alert_transactie_verwijderen_titel);
            builder.setMessage(R.string.dialog_transactie_ongedaan_maken_bericht);
            builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                makeUndoTransactie(position);
                dialog.dismiss();
            });
            builder.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        });

        return row;
    }

    private void makeUndoTransactie(int position) {
        Transactie transactie = transacties.get(position);
        if (transactie instanceof UndoTransactie) {
            Toast.makeText(getContext(), R.string.toas_fout_undo_transactie_ongedaan_maken, Toast.LENGTH_SHORT).show();
        } else {
            if (transactie instanceof CreditTransactie) {
                CreditTransactie creditTransactie = (CreditTransactie) transactie;
                try {
                    creditTransactie.undoAction(mLid);
                    UndoTransactie undoTransactie = new UndoTransactie(transactie.getUserId(), transactie.getEventId(), creditTransactie);
                    mLid.getTransacties().add(undoTransactie);
                } catch (SaldoOntoereikendException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (transactie instanceof DebitTransactie) {
                DebitTransactie debitTransactie = (DebitTransactie) transactie;
                UndoTransactie undoTransactie = new UndoTransactie(transactie.getUserId(), transactie.getEventId(), debitTransactie);
                mLid.getTransacties().add(undoTransactie);
            }
        }
    }
}
