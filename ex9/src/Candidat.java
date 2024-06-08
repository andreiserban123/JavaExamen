import java.util.List;

public class Candidat {
    int codCandidat;
    String numeCandidat;
    double media;
    List<Optiune> optiuni;

    public Candidat() {
    }

    public Candidat(int codCandidat, String numeCandidat, double media, List<Optiune> optiuni) {
        this.codCandidat = codCandidat;
        this.numeCandidat = numeCandidat;
        this.media = media;
        this.optiuni = optiuni;
    }

    @Override
    public String toString() {
        return "Candidat{" +
                "codCandidat=" + codCandidat +
                ", numeCandidat='" + numeCandidat + '\'' +
                ", media=" + media +
                ", optiuni=" + optiuni +
                '}';
    }
}
