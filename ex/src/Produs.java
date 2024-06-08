import java.util.Objects;

public class Produs {
    private int codProdus;
    private String denumire;
    private double pret;

    public Produs() {
    }

    public Produs(int codProdus, String denumire, double pret) {
        this.codProdus = codProdus;
        this.denumire = denumire;
        this.pret = pret;
    }

    public int getCodProdus() {
        return codProdus;
    }

    public void setCodProdus(int codProdus) {
        this.codProdus = codProdus;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public double getPret() {
        return pret;
    }

    public void setPret(double pret) {
        this.pret = pret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produs produs)) return false;
        return getCodProdus() == produs.getCodProdus() && Double.compare(getPret(), produs.getPret()) == 0 && Objects.equals(getDenumire(), produs.getDenumire());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCodProdus(), getDenumire(), getPret());
    }

    @Override
    public String toString() {
        return "Produs{" +
                "codProdus=" + codProdus +
                ", denumire='" + denumire + '\'' +
                ", pret=" + pret +
                '}';
    }
}
