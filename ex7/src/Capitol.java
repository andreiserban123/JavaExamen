public class Capitol {
    int codCapitol;
    int codSantier;
    String denumireCheltuiala;
    String um;
    double cantitate;
    double pu;

    @Override
    public String toString() {
        return "Capitol{" + "codCapitol=" + codCapitol + ", codSantier=" + codSantier + ", denumireCheltuiala='" + denumireCheltuiala + '\'' + ", um='" + um + '\'' + ", cantitate=" + cantitate + ", pu=" + pu + '}';
    }

    public Capitol() {
    }

    public Capitol(int codCapitol, int codSantier, String denumireCheltuiala, String um, double cantitate, double pu) {
        this.codCapitol = codCapitol;
        this.codSantier = codSantier;
        this.denumireCheltuiala = denumireCheltuiala;
        this.um = um;
        this.cantitate = cantitate;
        this.pu = pu;
    }
}
