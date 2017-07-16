package be.dijlezonen.dzwelg.models;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import be.dijlezonen.dzwelg.exceptions.BedragException;
import be.dijlezonen.dzwelg.exceptions.SaldoOntoereikendException;

public class Lid {
    private String id;
    private String voornaam;
    private String achternaam;
    private double saldo;
    private boolean actiefInLijst;

    public Lid() {
        // lege constructor voor firebase
    }

    public String getVoornaam() {
        return voornaam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public double getSaldo() {
        return saldo;
    }

    public void creditSaldo(double bedrag) throws BedragException {
        if (bedrag < 0) {
            throw new BedragException("Op te laden bedrag mag niet negatief zijn!");
        } else if (bedrag < 10) {
            throw new BedragException("Op te laden bedrag mag niet kleiner zijn dan €10!");
        } else {
            this.saldo += bedrag;
        }
    }

    public void debitSaldo(double bedrag) throws BedragException {
        if (bedrag < 0) {
            throw new BedragException("Af te trekken bedrag mag niet negatief zijn!");
        } else if ((saldo - bedrag) < 0) {
            throw new SaldoOntoereikendException();
        } else {
            this.saldo -= bedrag;
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVolledigeNaam() {
        return String.format(Locale.getDefault(), "%s %s", voornaam, achternaam);
    }

    public String getSaldoGeformatteerd()
    {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setCurrency(Currency.getInstance("EUR"));
        return format.format(saldo);
    }

    @java.lang.SuppressWarnings("squid:S00122")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lid lid = (Lid) o;

        return id != null ? id.equals(lid.id) : lid.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public boolean isActiefInLijst() {
        return actiefInLijst;
    }

    public void setActiefInLijst(boolean actiefInLijst) {
        this.actiefInLijst = actiefInLijst;
    }
}
