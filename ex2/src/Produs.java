import java.util.List;

public class Produs {
    int codProdus;
    String denumireProdus;
    List<Consum> consumuri;

    public Produs() {
    }

    public Produs(int codProdus, String denumireProdus, List<Consum> consumuri) {
        this.codProdus = codProdus;
        this.denumireProdus = denumireProdus;
        this.consumuri = consumuri;
    }

    @Override
    public String toString() {
        return "Produs{" +
                "codProdus=" + codProdus +
                ", denumireProdus='" + denumireProdus + '\'' +
                ", consumuri=" + consumuri +
                '}';
    }
}
