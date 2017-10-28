package be.dijlezonen.dzwelg.models.transacties;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.util.Date;

import be.dijlezonen.dzwelg.exceptions.BedragException;
import be.dijlezonen.dzwelg.exceptions.SaldoOntoereikendException;
import be.dijlezonen.dzwelg.models.ICanBeUndone;
import be.dijlezonen.dzwelg.models.Lid;
import be.dijlezonen.dzwelg.models.Transactie;

public class CreditTransactie extends Transactie implements ICanBeUndone {
    private static final String LOG_TAG = CreditTransactie.class.getSimpleName();
    private double bedrag;

    public CreditTransactie()
    {
    }

    public CreditTransactie(String userId, double bedrag, String eventId) {
        super(TransactieSoort.CREDIT, userId, eventId);
        this.bedrag = bedrag;
    }

    public double getBedrag() {
        return bedrag;
    }

    @Override
    public Lid undoAction(Lid lid) throws SaldoOntoereikendException {
        try {
            lid.debitSaldo(bedrag);
        } catch (SaldoOntoereikendException e) {
            //als het gebruikerssaldo ontoereikend is, exception doorgooien voor foutmelding in de UI
            throw e;
        } catch (BedragException e) {
            //dit zou niet mogen kunnen (fingers crossed!)
            Log.wtf(LOG_TAG, "Negatief bedrag in undo CreditTransactie!!!");
            FirebaseCrash.log("Negatief bedrag in undo CreditTransactie!!!");
        }

        return lid;
    }

    @Override
    public String toString() {
        Date d = new Date(getTimestampForKey());

        return d.toLocaleString() + " - Bedrag opgeladen: " + formatteerSaldo(bedrag);
    }
}
