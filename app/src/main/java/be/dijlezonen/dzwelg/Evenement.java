package be.dijlezonen.dzwelg;

import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class Evenement {
    private String id;
    private String titel;
    private Long aangemaakt;

    public Evenement() {
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
