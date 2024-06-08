public class ProiectFinalizat {
    int codProiect;
    String numeProiect;
    int anFinalizare;

    public ProiectFinalizat() {
    }

    public ProiectFinalizat(int codProiect, String numeProiect, int anFinalizare) {
        this.codProiect = codProiect;
        this.numeProiect = numeProiect;
        this.anFinalizare = anFinalizare;
    }

    @Override
    public String toString() {
        return "ProiectFinalizat{" +
                "codProiect=" + codProiect +
                ", numeProiect='" + numeProiect + '\'' +
                ", anFinalizare=" + anFinalizare +
                '}';
    }
}
