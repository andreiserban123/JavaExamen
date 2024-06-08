public class Candidat {
    long cnpCandidat;
    String numeCandidat;
    double notaBac;
    int codSpecializare;

    public Candidat(long cnpCandidat, String numeCandidat, double notaBac, int codSpecializare) {
        this.cnpCandidat = cnpCandidat;
        this.numeCandidat = numeCandidat;
        this.notaBac = notaBac;
        this.codSpecializare = codSpecializare;
    }

    @Override
    public String toString() {
        return "Candidat{" +
                "cnpCandidat=" + cnpCandidat +
                ", numeCandidat='" + numeCandidat + '\'' +
                ", notaBac=" + notaBac +
                ", codSpecializare=" + codSpecializare +
                '}';
    }
}
