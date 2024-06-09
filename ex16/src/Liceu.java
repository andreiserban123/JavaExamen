import java.util.List;

public class Liceu {
    int cod_liceu;
    String nume_liceu;
    int nr_specializari;
    List<Specializare> specializari;

    public Liceu() {
    }

    public Liceu(int cod_liceu, String nume_liceu, int nr_specializari, List<Specializare> specializari) {
        this.cod_liceu = cod_liceu;
        this.nume_liceu = nume_liceu;
        this.nr_specializari = nr_specializari;
        this.specializari = specializari;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Liceu{");
        sb.append("cod_liceu=").append(cod_liceu);
        sb.append(", nume_liceu='").append(nume_liceu).append('\'');
        sb.append(", nr_specializari=").append(nr_specializari);
        sb.append(", specializari=").append(specializari);
        sb.append('}');
        return sb.toString();
    }
}
