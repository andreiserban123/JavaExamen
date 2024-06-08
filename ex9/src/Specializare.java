public class Specializare {
    int codSpecializare;
    int nrLocuri;

    public Specializare() {
    }

    public Specializare(int codSpecializare, int nrLocuri) {
        this.codSpecializare = codSpecializare;
        this.nrLocuri = nrLocuri;
    }

    @Override
    public String toString() {
        return "Specializare{" +
                "codSpecializare=" + codSpecializare +
                ", nrLocuri=" + nrLocuri +
                '}';
    }
}
