import org.json.JSONArray;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static List<Candidat> candidati = new ArrayList<>();
    static List<Program> programe = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        // Citirea candidaților din JSON
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

        // Citirea programelor din XML
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

        System.out.println("------------------Cerinta 5-----------------------");

        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("SERVER STARTED ON PORT 8080");
            while (true) {
                Socket client = server.accept();
                System.out.println("Un client s-a conectat");
                new ClientHandler(client).start();
            }
        }

    }

    static class ClientHandler extends Thread {
        Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                var out = new PrintWriter(client.getOutputStream(), true);

                String denumireProgram = in.readLine();
                System.out.println("Am primit denumirea programului: " + denumireProgram);

                int codProgram = programe.stream()
                        .filter(p -> p.denumire.equals(denumireProgram))
                        .map(p -> p.codProgram)
                        .findFirst()
                        .orElse(-1);

                if (codProgram == -1) {
                    out.println("Programul nu a fost gasit");
                } else {
                    List<String> listaCandidati = candidati.stream()
                            .filter(c -> c.optiuni.stream().anyMatch(op -> op.codProgram == codProgram))
                            .map(c -> c.nume)
                            .collect(Collectors.toList());

                    out.println(String.join(", ", listaCandidati));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
