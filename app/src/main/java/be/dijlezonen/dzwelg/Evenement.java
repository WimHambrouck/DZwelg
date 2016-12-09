package be.dijlezonen.dzwelg;

public class Evenement {
    private String id;
    private String titel;

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
}
