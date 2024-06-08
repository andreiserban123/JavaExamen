public class Pacient {
    long cnp;
    String nume;
    int varsta;
    int cod_sectie;

    public Pacient() {
    }

    public Pacient(long cnp, String nume, int varsta, int cod_sectie) {
        this.cnp = cnp;
        this.nume = nume;
        this.varsta = varsta;
        this.cod_sectie = cod_sectie;
    }

    @Override
    public String toString() {
        return "Pacienti{" +
                "cnp=" + cnp +
                ", nume='" + nume + '\'' +
                ", varsta=" + varsta +
                ", cod_sectie=" + cod_sectie +
                '}';
    }
}
