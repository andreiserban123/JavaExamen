import org.w3c.dom.ls.LSOutput;

public class Imprumut {
    String numeStudent;
    String cota;
    int nrZile;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Imprumut{");
        sb.append("numeStudent='").append(numeStudent).append('\'');
        sb.append(", cota='").append(cota).append('\'');
        sb.append(", nrZile=").append(nrZile);
        sb.append('}');
        return sb.toString();
    }

    public Imprumut() {
    }

    public Imprumut(String numeStudent, String cota, int nrZile) {
        this.numeStudent = numeStudent;
        this.cota = cota;
        this.nrZile = nrZile;
    }
}
