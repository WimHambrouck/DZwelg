package be.dijlezonen.dzwelg.models;

public class Consumptie {
    private String id;
    private String naam;
    private double prijs;
    private String stocklijnId;

    public Consumptie() {
        //lege constructor voor Firebase
    }

    public String getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    public String getStocklijnId() {
        return stocklijnId;
    }
}
