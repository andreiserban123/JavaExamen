import java.io.Serializable;

public class Angajat implements Serializable {
    int id;
    String nume;
    String departament;
    double salariu;


    public Angajat(int id, String nume, String departament, double salariu) {
        this.id = id;
        this.nume = nume;
        this.departament = departament;
        this.salariu = salariu;
    }

    @Override
    public String toString() {
        return "Angajat{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                ", departament='" + departament + '\'' +
                ", salariu=" + salariu +
                '}';
    }
}
