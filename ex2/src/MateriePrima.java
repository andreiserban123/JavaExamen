public class MateriePrima {
    private int cod;
    private String denumire;
    private float cantitate;
    private float pretUnitar;
    private String unitateMasura;


    public MateriePrima(int cod, String denumire, float cantitate, float pretUnitar, String unitateMasura) {
        this.cod = cod;
        this.denumire = denumire;
        this.cantitate = cantitate;
        this.pretUnitar = pretUnitar;
        this.unitateMasura = unitateMasura;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public float getCantitate() {
        return cantitate;
    }

    public void setCantitate(float cantitate) {
        this.cantitate = cantitate;
    }

    public float getPretUnitar() {
        return pretUnitar;
    }

    public void setPretUnitar(float pretUnitar) {
        this.pretUnitar = pretUnitar;
    }

    public String getUnitateMasura() {
        return unitateMasura;
    }

    public void setUnitateMasura(String unitateMasura) {
        this.unitateMasura = unitateMasura;
    }

    @Override
    public String toString() {
        return "MateriePrima{" +
                "cod=" + cod +
                ", denumire='" + denumire + '\'' +
                ", cantitate=" + cantitate +
                ", pretUnitar=" + pretUnitar +
                ", unitateMasura='" + unitateMasura + '\'' +
                '}';
    }
}
