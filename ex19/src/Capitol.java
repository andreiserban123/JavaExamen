public class Capitol {
    int codCapitol;
    int codSantier;
    String denumire;
    String um;
    double cantitate;
    double pu;


    public Capitol(int codCapitol, int codSantier, String denumire, String um, double cantitate, double pu) {
        this.codCapitol = codCapitol;
        this.codSantier = codSantier;
        this.denumire = denumire;
        this.um = um;
        this.cantitate = cantitate;
        this.pu = pu;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Capitol{");
        sb.append("codCapitol=").append(codCapitol);
        sb.append(", codSantier=").append(codSantier);
        sb.append(", denumire='").append(denumire).append('\'');
        sb.append(", um='").append(um).append('\'');
        sb.append(", cantitate=").append(cantitate);
        sb.append(", pu=").append(pu);
        sb.append('}');
        return sb.toString();
    }
}
