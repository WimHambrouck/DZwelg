package be.dijlezonen.dzwelg.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class Activiteit implements Parcelable {
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

    public boolean isEvenement() {
        return datum != 0;
    }

    public Calendar getDatum() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(datum);
        return cal;
    }

    public Calendar getAangemaakt(){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(aangemaakt);
        return cal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.titel);
        dest.writeLong(this.aangemaakt);
        dest.writeLong(this.datum);
        dest.writeDouble(this.tegoed);
    }

    protected Activiteit(Parcel in) {
        this.id = in.readString();
        this.titel = in.readString();
        this.aangemaakt = in.readLong();
        this.datum = in.readLong();
        this.tegoed = in.readDouble();
    }

    public static final Parcelable.Creator<Activiteit> CREATOR = new Parcelable.Creator<Activiteit>() {
        @Override
        public Activiteit createFromParcel(Parcel source) {
            return new Activiteit(source);
        }

        @Override
        public Activiteit[] newArray(int size) {
            return new Activiteit[size];
        }
    };
}
