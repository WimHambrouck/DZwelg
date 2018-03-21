package be.dijlezonen.dzwelg.models.transacties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.dijlezonen.dzwelg.exceptions.SaldoOntoereikendException;
import be.dijlezonen.dzwelg.models.Consumptie;
import be.dijlezonen.dzwelg.models.Consumptielijn;
import be.dijlezonen.dzwelg.models.Lid;
import be.dijlezonen.dzwelg.models.Transactie;
import be.dijlezonen.dzwelg.models.ICanBeUndone;

public class DebitTransactie extends Transactie implements ICanBeUndone {

    private List<Consumptielijn> consumptielijnen;
    private double bedrag;

    public DebitTransactie() {
    }

    public DebitTransactie(String userId, String eventId, String kassaverantwoordelijkeId, List<Consumptielijn> consumptielijnen, double bedrag) {
        super(TransactieSoort.DEBIT, userId, eventId, kassaverantwoordelijkeId);
        this.consumptielijnen = filterConsumptieLijnen(consumptielijnen);
        this.bedrag = bedrag;
    }

    /**
     * Haalt alle consumptielijnen waarbij het aantal == 0 weg
     * (m.a.w.: alleen maar de effectief aangekochte consumpties blijven over)
     *
     * @param consumptielijnen De te filteren consumptielijnen
     * @return Een ArrayList met de gefilterde consumptielijnen
     */
    private List<Consumptielijn> filterConsumptieLijnen(List<Consumptielijn> consumptielijnen) {
        List<Consumptielijn> gefilterdeConsumptielijnen = new ArrayList<>();

        assert consumptielijnen != null;
        for (Consumptielijn consumptielijn :
                consumptielijnen) {
            if (consumptielijn.getAantal() != 0) {
                gefilterdeConsumptielijnen.add(consumptielijn);
            }
        }

        return gefilterdeConsumptielijnen;
    }

    @Override
    public Lid undoAction(Lid lid) throws SaldoOntoereikendException {
        return null;
    }

    public List<Consumptielijn> getConsumptielijnen() {
        return consumptielijnen;
    }

    public double getBedrag() {
        return bedrag;
    }

    @Override
    public String toString() {
        Date d = new Date(getTimestampForKey());

        StringBuilder sb =
                new StringBuilder(d.toLocaleString() + " - Verkoop: " + formatteerSaldo(bedrag));

        if (!consumptielijnen.isEmpty())
        {
            sb.append(" (").append(consumptielijnen.get(0).toString());

            for (int i = 1; i < consumptielijnen.size(); i++)
            {
                sb.append(", ").append(consumptielijnen.get(i).toString());
            }

            sb.append(")");
        }

        return sb.toString();
    }
}
