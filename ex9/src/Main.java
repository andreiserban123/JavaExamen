import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static List<Liceu> licee = new ArrayList<>();
    static List<Candidat> candidati = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        try (BufferedReader br = new BufferedReader(new FileReader("Date\\licee.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int idLiceu = Integer.parseInt(values[0]);
                String numeLiceu = values[1];
                int nrSpecializari = Integer.parseInt(values[2]);
                line = br.readLine();
                values = line.split(",");
                List<Specializare> specializari = new ArrayList<>(nrSpecializari);
                for (int i = 0; i < values.length - 1; i += 2) {
                    Specializare s = new Specializare();
                    s.codSpecializare = Integer.parseInt(values[i]);
                    s.nrLocuri = Integer.parseInt(values[i + 1]);
                    specializari.add(s);
                }
                licee.add(new Liceu(idLiceu, numeLiceu, nrSpecializari, specializari));
            }

        }

//        licee.forEach(System.out::println);
        try (FileReader file = new FileReader("Date\\candidati.json")) {
            var tokener = new JSONTokener(file);
            var arr = new JSONArray(tokener);
            for (int i = 0; i < arr.length(); i++) {
                var obj = arr.getJSONObject(i);
                int codCandidat = obj.getInt("cod_candidat");
                String numeCandidat = obj.getString("nume_candidat");
                double media = obj.getDouble("media");
                var imbricat = obj.getJSONArray("optiuni");
                List<Optiune> optiuni = new ArrayList<>();
                for (int j = 0; j < imbricat.length(); j++) {
                    Optiune optiune = new Optiune();
                    var obj2 = imbricat.getJSONObject(j);
                    optiune.codLiceu = obj2.getInt("cod_liceu");
                    optiune.codSpecializare = obj2.getInt("cod_specializare");
                    optiuni.add(optiune);
                }
                candidati.add(new Candidat(codCandidat, numeCandidat, media, optiuni));
            }
        }

        System.out.println("-------------------------Cerinta 1-------------------------------");
        System.out.println("numar de stud cu medie peste noua: " + candidati.stream().filter(c -> c.media >= 9.0).count());
        System.out.println("-------------------------Cerinta 2-------------------------------");
        licee.forEach(liceu -> {
            int nr = liceu.specializari.stream().mapToInt(s -> s.nrLocuri).reduce(Integer::sum).getAsInt();
            System.out.println("CodLiceu:" + liceu.codLiceu + " Liceu: " + liceu.numeLiceu + " nr locuri totale: " + nr);
        });
        System.out.println("-------------------------Cerinta 3-------------------------------");

        candidati.stream().sorted((c1, c2) -> {
            if (c1.optiuni.size() == c2.optiuni.size()) {
                return Double.compare(c2.media, c1.media);
            }
            return Integer.compare(c2.optiuni.size(), c1.optiuni.size());
        }).forEach(System.out::println);

        System.out.println("-------------------------Cerinta 4-------------------------------");


        try (var connection = DriverManager.getConnection("jdbc:sqlite:Date\\candidati.db");
             var stm = connection.prepareStatement("CREATE TABLE IF NOT EXISTS CANDIDATI (cod_candidat INTEGER, nume_candidat TEXT, medie DOUBLE, numar_optiuni INTEGER)");

        ) {
            stm.executeUpdate();
        }
        try (var connection = DriverManager.getConnection("jdbc:sqlite:Date\\candidati.db");
             var insertStm = connection.prepareStatement("INSERT INTO CANDIDATI VALUES (?, ?, ?, ?)")
        ) {
            for (var cand : candidati) {
                insertStm.setInt(1, cand.codCandidat);
                insertStm.setString(2, cand.numeCandidat);
                insertStm.setDouble(3, cand.media);
                insertStm.setInt(4, cand.optiuni.size());
                insertStm.executeUpdate();
            }
            System.out.println("S-au realizat inserturile");
        }
    }
}
