import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    static List<Persoana> persoane = new ArrayList<>();
    static List<Tranzactie> trazactii = new ArrayList<>();

    public static void main(String[] args) {
        try {
            String url = "jdbc:sqlite:Date\\bursa.db";
            var connection = DriverManager.getConnection(url);
            System.out.println("Conexiunea la bd a fost realizata cu succes!");
            var stm = connection.createStatement();
            var resulSet = stm.executeQuery("SELECT * from Persoane");
            while (resulSet.next()) {
                Persoana p = new Persoana();
                p.codPersoana = resulSet.getInt(1);
                p.cnp = resulSet.getString(2);
                p.nume = resulSet.getString(3);
                persoane.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader bf = new BufferedReader(new FileReader("Date\\bursa_tranzactii.txt"))) {
            trazactii = bf.lines().map(linie -> new Tranzactie(Integer.parseInt(linie.split(",")[0]),
                    linie.split(",")[1],
                    linie.split(",")[2],
                    Integer.parseInt(linie.split(",")[3]),
                    Float.parseFloat(linie.split(",")[4])
            )).toList();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("---------------Cerinta 1------------------");
        System.out.println("Numar de nerezidenti: " + persoane.stream().filter(p -> p.cnp.charAt(0) == '8' || p.cnp.charAt(0) == '9').mapToInt(p -> p.codPersoana).count());
        System.out.println("---------------Cerinta 2------------------");
        Map<String, Long> m = trazactii.stream().collect(Collectors.groupingBy(tranzactie -> tranzactie.simbol,
                Collectors.counting()));
        m.forEach((k, v) -> {
            System.out.println(k + " -> " + v + " tranzactii");
        });
        System.out.println("---------------Cerinta 3------------------");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Date\\simboluri.txt"))) {
            trazactii.stream().distinct().forEach(t ->
            {
                try {
                    bw.write(t.simbol + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("Fisierul a fost scris!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("---------------Cerinta 4------------------");
        persoane.forEach(p -> {
            System.out.println(p.nume);
            var cum = trazactii.stream().filter(t -> t.tip.equals("cumparare")).collect(Collectors.groupingBy(
                    t -> t.simbol,
                    Collectors.summingDouble(t -> t.cantitate * t.pret)
            ));

            var vaz = trazactii.stream().filter(t -> t.tip.equals("vanzare")).collect(Collectors.groupingBy(
                    t -> t.simbol,
                    Collectors.summingDouble(t -> t.cantitate * t.pret)
            ));
            cum.forEach((simbol, sumaCumparare) -> {
                double sumaVanzare = vaz.getOrDefault(simbol, 0.0);
                double diferenta = sumaCumparare - sumaVanzare;
                System.out.println( simbol + " -> " + diferenta);
            });
        });
        
    }
}