import java.util.Arrays;

public class Proiect {
    int codProiect;
    String numeProiect;
    double buget;
    int[] echipa;

    public Proiect() {
    }

    public Proiect(int codProiect, String numeProiect, double buget, int[] echipa) {
        this.codProiect = codProiect;
        this.numeProiect = numeProiect;
        this.buget = buget;
        this.echipa = echipa;
    }

    @Override
    public String toString() {
        return "Proiect{" +
                "codProiect=" + codProiect +
                ", numeProiect='" + numeProiect + '\'' +
                ", buget=" + buget +
                ", echipa=" + Arrays.toString(echipa) +
                '}';
    }
}
