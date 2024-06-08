import org.json.JSONArray;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static List<Candidat> candidati = new ArrayList<>();
    static List<Program> programe = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (BufferedReader bf = new BufferedReader(new FileReader("candidati.json"))) {
            var tokener = new JSONTokener(bf);
            var arr = new JSONArray(tokener);
            for (int i = 0; i < arr.length(); i++) {
                var pr = arr.getJSONObject(i);
                Candidat c = new Candidat();
                c.nume = pr.getString("nume");
                var op = pr.getJSONArray("optiuni");
                List<Optiune> optiuni = new ArrayList<>();
                for (int j = 0; j < op.length(); j++) {
                    var obj1 = op.getJSONObject(j);
                    Optiune optiune = new Optiune();
                    optiune.codProgram = obj1.getInt("codProgram");
                    optiune.punctaj = obj1.getDouble("punctaj");
                    optiuni.add(optiune);
                }
                c.optiuni = optiuni;
                candidati.add(c);
            }
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse("programe.xml");

        var nodeList = doc.getElementsByTagName("program");
        for (int i = 0; i < nodeList.getLength(); i++) {
            var node = nodeList.item(i);
            var el = (Element) node;
            int codProgram = Integer.parseInt(el.getElementsByTagName("codProgram").item(0).getTextContent());
            String denumire = el.getElementsByTagName("denumire").item(0).getTextContent();
            int nrLocuri = Integer.parseInt(el.getElementsByTagName("nrLocuri").item(0).getTextContent());
            Program p = new Program(codProgram, denumire, nrLocuri);
            programe.add(p);
        }

        System.out.println("--------------------Cerinta 1----------------------");
        candidati.forEach(c ->
                System.out.println(c.nume + " - " + c.optiuni.size() + " optiuni")
        );
        System.out.println("--------------------Cerinta 2----------------------");
        programe.stream().sorted((p1, p2) -> Integer.compare(p2.nrLocuri, p1.nrLocuri)).forEach(p -> {
            System.out.println(p.denumire + " - " + p.nrLocuri + " locuri");
        });
        System.out.println("--------------------Cerinta 3----------------------");
        String createTableSQL = "CREATE TABLE IF NOT EXISTS OptiuniCandidati (\n" +
                "    nume TEXT,\n" +
                "    codProgram INTEGER,\n" +
                "    punctaj REAL\n" +
                ");";
        String insertSQL = "INSERT INTO OptiuniCandidati VALUES (?, ?, ?)";
        String selectSQL = "SELECT * FROM OptiuniCandidati";
        try (var con = DriverManager.getConnection("jdbc:sqlite:admitere.db")) {

            try (var stm = con.createStatement()) {
                stm.executeUpdate(createTableSQL);
            }

            try (var stm = con.prepareStatement(insertSQL)) {
                for (Candidat candidat : candidati) {
                    for (Optiune optiune : candidat.optiuni) {
                        stm.setString(1, candidat.nume);
                        stm.setInt(2, optiune.codProgram);
                        stm.setDouble(3, optiune.punctaj);
                        stm.executeUpdate();
                    }
                }
            }
            try (var stm = con.createStatement();
                 var resultSet = stm.executeQuery(selectSQL)) {
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("nume") + " - " + resultSet.getInt("codProgram") + " - " + resultSet.getDouble("punctaj"));
                }
            }
        }
        System.out.println("------------------Cerinta 4-----------------------");
        Map<Integer, Double> codSiMedie = candidati.stream().flatMap(candidat -> candidat.optiuni.stream())
                .collect(Collectors.groupingBy(o -> o.codProgram, Collectors.averagingDouble(o -> o.punctaj)));
        codSiMedie.entrySet().forEach(e -> {
            int cod = e.getKey();
            String denumire = programe.stream().filter(p -> p.codProgram == cod).map(s -> s.denumire).findFirst().get();
            System.out.println(denumire + " - " + e.getValue());
        });


    }
}
