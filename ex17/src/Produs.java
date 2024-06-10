public class Produs {
    int id;
    String denumire;
    String categorie;
    double pret;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Produs{");
        sb.append("id=").append(id);
        sb.append(", denumire='").append(denumire).append('\'');
        sb.append(", categorie='").append(categorie).append('\'');
        sb.append(", pret=").append(pret);
        sb.append('}');
        return sb.toString();
    }

    public Produs() {
    }

    public Produs(int id, String denumire, String categorie, double pret) {
        this.id = id;
        this.denumire = denumire;
        this.categorie = categorie;
        this.pret = pret;
    }
}
