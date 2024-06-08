public class Tranzactie {
    int codProdus;
    int cantitate;
    Tip tip;

    public Tranzactie(int codProdus, int cantitate, Tip tip) {
        this.codProdus = codProdus;
        this.cantitate = cantitate;
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "Tranzactie{" +
                "codProdus=" + codProdus +
                ", cantitate=" + cantitate +
                ", tip=" + tip +
                '}';
    }
}
