import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.*;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    static List<Proiect> proiecte = new ArrayList<>();
    static List<Cheltuiala> cheltuieli = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (BufferedReader bf = new BufferedReader(new FileReader("proiecte.json"))) {
            var tokener = new JSONTokener(bf);
            var arr = new JSONArray(tokener);
            for (int i = 0; i < arr.length(); i++) {
                var obj = arr.getJSONObject(i);
                int cod = obj.getInt("cod_proiect");
                String numeProiect = obj.getString("nume_proiect");
                String manager = obj.getString("manager");
                double buget = obj.getDouble("buget");
                proiecte.add(new Proiect(cod, numeProiect, manager, buget));
            }
        }

        try (var conn = DriverManager.getConnection("jdbc:sqlite:cheltuieli.db");
             var stm = conn.createStatement();
             var resultSet = stm.executeQuery("SELECT  * from Cheltuieli");
        ) {
            while (resultSet.next()) {
                int codCheltuiala = resultSet.getInt(1);
                int codProiecte = resultSet.getInt(2);
                String descriere = resultSet.getString(3);
                String unit = resultSet.getString(4);
                double um = resultSet.getDouble(5);
                double pret = resultSet.getDouble(6);
                cheltuieli.add(new Cheltuiala(codCheltuiala, codProiecte, descriere, unit, um, pret));
            }
        }
        System.out.println("-----------------------CERINTA 1---------------------------");
        // Să se afișeze la consolă proiectele și valoarea medie a bugetelor acestora
        proiecte.forEach(System.out::println);

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        System.out.println(decimalFormat.format(proiecte.stream().mapToDouble(Proiect::getBuget).average().getAsDouble()));
        System.out.println("-----------------------CERINTA 2---------------------------");
        // Să se afișeze la consolă valoarea totala pentru fiecare cheltuială

        Map<Cheltuiala, Double> ch = cheltuieli.stream().collect(Collectors.groupingBy(c -> c, Collectors.summingDouble(c -> c.cantitate * c.pret_unitar)));
        ch.keySet().forEach(k -> {
            System.out.println(k + " valoare " + ch.get(k));
        });
        System.out.println("-----------------------CERINTA 3---------------------------");

        // Să se afișeze lista managerilor și numărul de proiecte gestionate de fiecare.
        Map<String, Long> man = proiecte.stream().collect(Collectors.groupingBy(c -> c.manager, Collectors.counting()));
        man.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(e -> {
            System.out.println("Manager " + e.getKey() + " are " + e.getValue() + " proiecte");
        });

        // Să se afișeze bugetul total pentru fiecare manager, ordonat descrescător după buget
        System.out.println("---------------------CERINTA 4-----------------------------");
        Map<String, Double> manDupaBug = proiecte.stream().collect(Collectors.groupingBy(c -> c.manager, Collectors.summingDouble(c -> c.buget)));
        manDupaBug.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach((e -> {
            System.out.println("Manager " + e.getKey() + " are buggetul " + e.getValue());
        }));
        System.out.println("---------------------CERINTA 5---------------------------");
        // Să se salveze în fișierul raport_cheltuieli.txt o situație a cheltuielilor la nivel de proiecte, cu sumarizare pe cheltuieli
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("raport.cheltuieli.txt"))) {
            Map<Integer, Double> map = cheltuieli.stream().collect(Collectors.groupingBy(c -> c.cod_proiect, Collectors.summingDouble(c -> c.cantitate * c.pret_unitar)));
            map.entrySet().forEach(e -> {
                int codProiect = e.getKey();
                Proiect p = proiecte.stream().filter(pr -> pr.cod_proiect == codProiect).findFirst().orElse(null);
                if (p == null) {
                    System.out.println("Proiectul nu exista");
                } else {
                    try {
                        bw.write(p.nume_proiect + " are cheltuieli in valoare de " + e.getValue()+"\n");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }

    }
}