package be.dijlezonen.dzwelg.models;

public class Consumptielijn {
    private Consumptie consumptie;
    private int aantal;

    public Consumptielijn(Consumptie consumptie) {
        this.consumptie = consumptie;
        this.aantal = 0;
    }

    public Integer getAantal() {
        return aantal;
    }

    public void setAantal(int aantal) {
        this.aantal = aantal;
    }

    public Consumptie getConsumptie() {
        return consumptie;
    }

    @Override
    public String toString() {
        return aantal + " x " + consumptie.getNaam();
    }
}
