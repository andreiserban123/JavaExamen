public class Sectie {
    int cod_sectie;
    String denumire;
    int numarLocuri;

    public Sectie(int cod_sectie, String denumire, int numarLocuri) {
        this.cod_sectie = cod_sectie;
        this.denumire = denumire;
        this.numarLocuri = numarLocuri;
    }

    public Sectie() {
    }

    @Override
    public String toString() {
        return "Sectie{" +
                "cod_sectie=" + cod_sectie +
                ", denumire='" + denumire + '\'' +
                ", numarLocuri=" + numarLocuri +
                '}';
    }
}
