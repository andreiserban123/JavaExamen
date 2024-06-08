import java.util.List;

public class Liceu {
    int codLiceu;
    String numeLiceu;
    int nrSpecializari;
    List<Specializare> specializari;

    public Liceu(int codLiceu, String numeLiceu, int nrSpecializari, List<Specializare> specializari) {
        this.codLiceu = codLiceu;
        this.numeLiceu = numeLiceu;
        this.nrSpecializari = nrSpecializari;
        this.specializari = specializari;
    }

    @Override
    public String toString() {
        return "Liceu{" +
                "codLiceu=" + codLiceu +
                ", numeLiceu='" + numeLiceu + '\'' +
                ", nrSpecializari=" + nrSpecializari +
                ", specializari=" + specializari +
                '}';
    }
}
