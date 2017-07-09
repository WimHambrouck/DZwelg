package be.dijlezonen.dzwelg.models;

public class Consumptielijn {
    private Consumptie consumptie;
    private int aantal;

    public Consumptielijn(Consumptie consumptie, int aantal) {
        this.consumptie = consumptie;
        this.aantal = aantal;
    }

    public Integer getAantal() {
        return aantal;
    }

    public void setAantal(int aantal) {
        this.aantal = aantal;
    }
}
