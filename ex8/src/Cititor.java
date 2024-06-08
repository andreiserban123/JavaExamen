import java.util.Objects;

public class Cititor {
    String nume;
    String cotaCarte;
    int nrZile;

    @Override
    public String toString() {
        return "Cititor{" +
                "nume='" + nume + '\'' +
                ", cotaCarte='" + cotaCarte + '\'' +
                ", nrZile=" + nrZile +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cititor cititor)) return false;
        return Objects.equals(nume, cititor.nume);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nume);
    }

    public Cititor() {
    }

    public Cititor(String nume, String cotaCarte, int nrZile) {
        this.nume = nume;
        this.cotaCarte = cotaCarte;
        this.nrZile = nrZile;
    }
}
