package be.dijlezonen.dzwelg.models;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Stelt een transactie voor, m.a.w. een aan- of verkoop of undo (het terugdraaien van één der vorigen)
 *
 */
public abstract class Transactie {

    public enum TransactieSoort {
        CREDIT, DEBIT, UNDO
    }

    private String id;
    private String userId;
    private long timestamp;
    private TransactieSoort soort;   //omdat het onmogelijk is vanuit Firebase db te weten welke subklasse het is

    protected Transactie(TransactieSoort soort, String userId, String eventId) {
        this.soort = soort;
        this.userId = userId;
        this.timestamp = -1 * new Date().getTime(); // negatieve timestamp om antichronologisch te kunnen ordenen
    }

    public abstract String toString();

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Exclude
    public long getTimestampForKey() {
        return -1 * timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactieSoort getSoort() {
        return soort;
    }
}
