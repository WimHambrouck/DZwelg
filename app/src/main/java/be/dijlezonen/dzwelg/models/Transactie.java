package be.dijlezonen.dzwelg.models;

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

    protected Transactie(TransactieSoort soort, String userId) {
        this.soort = soort;
        this.userId = userId;
        this.timestamp = new Date().getTime();
    }

    public abstract String toString();

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
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
