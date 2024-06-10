import java.text.SimpleDateFormat;
import java.util.Date;

public class Vanzare {
    int id;
    int id_produs;
    int cantitate;
    Date data;

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final StringBuilder sb = new StringBuilder("Vanzare{");
        sb.append("id=").append(id);
        sb.append(", id_produs=").append(id_produs);
        sb.append(", cantitate=").append(cantitate);
        sb.append(", data=").append(dateFormat.format(data));
        sb.append('}');
        return sb.toString();
    }

    public Vanzare() {
    }

    public Vanzare(int id, int id_produs, int cantitate, Date data) {
        this.id = id;
        this.id_produs = id_produs;
        this.cantitate = cantitate;
        this.data = data;
    }
}
