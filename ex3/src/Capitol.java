import java.util.Objects;

public class Capitol {
    int codCapitol;
    int codSantier;
    String denumireCheltuiala;
    String um;
    float cantitate;
    float pu;

    @Override
    public String toString() {
        return "Capitol{" +
                "codCapitol=" + codCapitol +
                ", codSantier=" + codSantier +
                ", denumireCheltuiala='" + denumireCheltuiala + '\'' +
                ", um='" + um + '\'' +
                ", cantitate=" + cantitate +
                ", pu=" + pu +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Capitol capitol)) return false;
        return codCapitol == capitol.codCapitol;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codCapitol);
    }
}
