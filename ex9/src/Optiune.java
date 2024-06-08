public class Optiune {
    int codLiceu;
    int codSpecializare;

    public Optiune(int codLiceu, int codSpecializare) {
        this.codLiceu = codLiceu;
        this.codSpecializare = codSpecializare;
    }

    public Optiune() {
    }

    @Override
    public String toString() {
        return "Optiune{" +
                "codLiceu=" + codLiceu +
                ", codSpecializare=" + codSpecializare +
                '}';
    }
}
