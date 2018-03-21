package be.dijlezonen.dzwelg.models.transacties;

import be.dijlezonen.dzwelg.models.ICanBeUndone;
import be.dijlezonen.dzwelg.models.Transactie;

public class UndoTransactie extends Transactie {

    private ICanBeUndone transactie;

    public UndoTransactie() {
    }

    public UndoTransactie(String userId, String eventId, String kassaverantwoordelijkeId, ICanBeUndone transactie) {
        super(TransactieSoort.UNDO, userId, eventId, kassaverantwoordelijkeId);
        this.transactie = transactie;
    }

    public ICanBeUndone getTransactie() {
        return transactie;
    }

    @Override
    public String toString() {
        return "UNDO";
    }
}
