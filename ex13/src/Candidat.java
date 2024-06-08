import java.util.List;

public class Candidat {
    String nume;
    List<Optiune> optiuni;

    public Candidat() {
    }

    public Candidat(String nume, List<Optiune> optiuni) {
        this.nume = nume;
        this.optiuni = optiuni;
    }

    @Override
    public String toString() {
        return "Candidat{" +
                "nume='" + nume + '\'' +
                ", optiuni=" + optiuni +
                '}';
    }
}
