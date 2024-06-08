public class Consum {
    int codMateriePrima;
    float cantitate;

    public Consum(int codMateriePrima, float cantitate) {
        this.codMateriePrima = codMateriePrima;
        this.cantitate = cantitate;
    }

    public int getCodMateriePrima() {
        return codMateriePrima;
    }

    public float getCantitate() {
        return cantitate;
    }

    @Override
    public String toString() {
        return
                "codMateriePrima=" + codMateriePrima +
                        ", cantitate=" + cantitate;
    }
}
