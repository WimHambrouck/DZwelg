package be.dijlezonen.dzwelg.models;

import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class Activiteit {
    private String id;
    private String titel;
    private long aangemaakt;
    private long datum;
    private double tegoed;

    public Activiteit() {
        // empty constructor needed for firebase
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public Calendar getAangemaakt(){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(aangemaakt);
        return cal;
    }
}
