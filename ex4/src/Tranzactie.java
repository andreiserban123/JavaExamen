import java.util.Objects;

public class Tranzactie {
    int codClient;
    String simbol;
    String tip;
    int cantitate;
    float pret;

    public Tranzactie() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tranzactie that)) return false;
        return Objects.equals(simbol, that.simbol);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(simbol);
    }

    public Tranzactie(int codClient, String simbol, String tip, int cantitate, float pret) {
        this.codClient = codClient;
        this.simbol = simbol;
        this.tip = tip;
        this.cantitate = cantitate;
        this.pret = pret;
    }

    @Override
    public String toString() {
        return "Tranzactie{" +
                "codClient=" + codClient +
                ", simbol='" + simbol + '\'' +
                ", tip='" + tip + '\'' +
                ", cantitate=" + cantitate +
                ", pret=" + pret +
                '}';
    }
}
