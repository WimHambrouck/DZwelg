package be.dijlezonen.dzwelg.models.transacties;

import java.util.List;

import be.dijlezonen.dzwelg.exceptions.SaldoOntoereikendException;
import be.dijlezonen.dzwelg.models.Consumptielijn;
import be.dijlezonen.dzwelg.models.Lid;
import be.dijlezonen.dzwelg.models.Transactie;
import be.dijlezonen.dzwelg.models.ICanBeUndone;

public class DebitTransactie extends Transactie implements ICanBeUndone {

    private List<Consumptielijn> consumptielijnen;
    private double bedrag;

    public DebitTransactie(String userId, List<Consumptielijn> consumptielijnen, double bedrag) {
        super(TransactieSoort.DEBIT, userId);
        this.consumptielijnen = consumptielijnen;
        this.bedrag = bedrag;
    }

    @Override
    public Lid undoAction(Lid lid) throws SaldoOntoereikendException {
        return null;
    }

    public List<Consumptielijn> getConsumptielijnen() {
        return consumptielijnen;
    }

    @Override
    public String toString() {
        return "Debit";
    }

    public double getBedrag() {
        return bedrag;
    }
}
