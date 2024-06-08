import java.util.List;

public class Cititor {
    String nume;
    List<CarteImprumutata> cartiImprumutate;

    @Override
    public String toString() {
        return "Cititor{" +
                "nume='" + nume + '\'' +
                ", cartiImprumutate=" + cartiImprumutate +
                '}';
    }

    public Cititor() {
    }

    public Cititor(String nume, List<CarteImprumutata> cartiImprumutate) {
        this.nume = nume;
        this.cartiImprumutate = cartiImprumutate;
    }
}
