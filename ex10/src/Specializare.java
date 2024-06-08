public class Specializare {
    int codSpecializare;
    String denumire;
    int locuri;

    @Override
    public String toString() {
        return "Specializare{" +
                "codSpecializare=" + codSpecializare +
                ", denumire='" + denumire + '\'' +
                ", locuri=" + locuri +
                '}';
    }

    public Specializare() {
    }

    public Specializare(int cod, String denumire, int locuri) {
        this.codSpecializare = cod;
        this.denumire = denumire;
        this.locuri = locuri;
    }
}
