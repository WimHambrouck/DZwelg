package be.dijlezonen.dzwelg.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import be.dijlezonen.dzwelg.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EigenBedragDialogFragment extends DialogFragment {

    private EigenBedragDialogListener mListener;

    @BindView(R.id.edit_bedrag)
    EditText mEditBedrag;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_eigen_bedrag, null, false);
        ButterKnife.bind(this, view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dismiss())
                .setPositiveButton(android.R.string.ok, ((dialog, which) -> mListener.onDialogPositiveClick(this)));

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (EigenBedragDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " heeft geen implementatie van EigenBedragDialogListener!");
        }
    }

    public double getIngevoerdBedrag() {
        String bedrag = mEditBedrag.getText().toString();
        if (bedrag.isEmpty()) {
            return 0;
        } else {
            return Double.parseDouble(bedrag);
        }
    }

    public interface EigenBedragDialogListener {
        void onDialogPositiveClick(EigenBedragDialogFragment dialog);
    }
}
