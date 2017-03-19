package be.dijlezonen.dzwelg.models;

import java.util.Locale;

public class Lid {
    private String id;
    private String voornaam;
    private String achternaam;
    private double saldo;

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

    public void setSaldo(double saldo) {
        this.saldo = saldo;
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
        return String.format(Locale.getDefault(), "€ %.2f", saldo);
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
}
