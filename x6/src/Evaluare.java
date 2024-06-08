public class Evaluare {
    int idAngajat;
    int an;
    double scor;

    public Evaluare() {
    }

    public Evaluare(int idAngajat, int an, double scor) {
        this.idAngajat = idAngajat;
        this.an = an;
        this.scor = scor;
    }

    @Override
    public String toString() {
        return "Evaluare{" +
                "idAngajat=" + idAngajat +
                ", an=" + an +
                ", scor=" + scor +
                '}';
    }
}
