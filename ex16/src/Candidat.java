import java.util.List;

public class Candidat {
    int cod_candidat;
    String nume_candidat;
    double media;
    List<Optiune> optiuni;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Candidat{");
        sb.append("cod_candidat=").append(cod_candidat);
        sb.append(", nume_candidat='").append(nume_candidat).append('\'');
        sb.append(", media=").append(media);
        sb.append(", optiuni=").append(optiuni);
        sb.append('}');
        return sb.toString();
    }

    public Candidat() {
    }

    public Candidat(int cod_candidat, String nume_candidat, double media, List<Optiune> optiuni) {
        this.cod_candidat = cod_candidat;
        this.nume_candidat = nume_candidat;
        this.media = media;
        this.optiuni = optiuni;
    }
}
