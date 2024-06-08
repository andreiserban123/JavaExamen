public class Produs {
    int idProdus;
    String denumire;
    double pret;

    public Produs(int idProdus, String denumire, double pret) {
        this.idProdus = idProdus;
        this.denumire = denumire;
        this.pret = pret;
    }

    @Override
    public String toString() {
        return "Produs{" +
                "idProdus=" + idProdus +
                ", denumire='" + denumire + '\'' +
                ", pret=" + pret +
                '}';
    }
}
