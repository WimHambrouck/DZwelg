package be.dijlezonen.dzwelg.models;

import be.dijlezonen.dzwelg.exceptions.SaldoOntoereikendException;

public interface ICanBeUndone {
    Lid undoAction(Lid lid) throws SaldoOntoereikendException;
}
