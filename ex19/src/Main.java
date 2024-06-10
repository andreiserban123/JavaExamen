import org.json.JSONArray;
import org.json.JSONTokener;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    static List<Santier> santiere = new ArrayList<>();
    static List<Capitol> capitole = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (var conn = DriverManager.getConnection("jdbc:sqlite:devize.db");
             var stm = conn.createStatement();
             var resultSet = stm.executeQuery("SELECT * FROM CAPITOLE");
        ) {
            while (resultSet.next()) {
                int codCap = resultSet.getInt(1);
                int codsant = resultSet.getInt(2);
                String denumire = resultSet.getString(3);
                String um = resultSet.getString(4);
                double cant = resultSet.getDouble(5);
                double pret = resultSet.getDouble(6);

                capitole.add(new Capitol(codCap, codsant, denumire, um, cant, pret));

            }
        }
        capitole.forEach(System.out::println);

        try (BufferedReader br = new BufferedReader(new FileReader("santiere.json"))) {
            var tokener = new JSONTokener(br);
            var arr = new JSONArray(tokener);
            for (int i = 0; i < arr.length(); i++) {
                var obj = arr.getJSONObject(i);
//                System.out.println(obj);
                Santier s = new Santier();
                s.codSantier = obj.getInt("Cod Santier");
                s.localitate = obj.getString("Localitate");
                s.strada = obj.getString("Strada");
                s.obiectiv = obj.getString("Obiectiv");
                s.valoare = obj.getDouble("Valoare");
                santiere.add(s);
            }
        }
        System.out.println();
        santiere.forEach(System.out::println);

        System.out.println("--------------------CERINTA 1--------------------------");
        santiere.forEach(System.out::println);
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        System.out.println(decimalFormat.format(santiere.stream().mapToDouble(s -> s.valoare).average().orElse(0)));
        System.out.println("--------------------CERINTA 2--------------------------");
        capitole.stream().collect(Collectors.groupingBy(c -> c.codCapitol, Collectors.summingDouble(c -> c.cantitate))).forEach((k, v) -> {
            System.out.println("Cod " + k + "," + v);
        });
        System.out.println("--------------------CERINTA 3--------------------------");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        var root = document.createElement("capitole");
        document.appendChild(root);

        capitole.stream().mapToInt(c -> c.codCapitol).distinct().forEach(codCap -> {

            var cap = document.createElement("capitol");
            root.appendChild(cap);
            var codSantier = document.createElement("cod-cap");
            codSantier.setTextContent(String.valueOf(codCap));
            cap.appendChild(codSantier);
//            System.out.println("cod cap: " + codCap);
            var santiere = document.createElement("santiere");
            cap.appendChild(santiere);
            capitole.stream().filter(c -> c.codCapitol == codCap).collect(Collectors.groupingBy(c -> c.codSantier, Collectors.summingDouble(c -> c.cantitate * c.pu)))
                    .forEach((k, v) -> {
//                        System.out.println("cod santier: " + k + ", valoare: " + v);
                        var santier = document.createElement("santier");
                        santiere.appendChild(santier);
                        var cod_santier = document.createElement("cod_santier");
                        cod_santier.setTextContent(String.valueOf(k));
                        santier.appendChild(cod_santier);
                        var valoare = document.createElement("valoare");
                        valoare.setTextContent(String.valueOf(v));
                        santier.appendChild(valoare);
                    });
        });
        File f = new File("capitole.xml");
        TransformerFactory factory1 = TransformerFactory.newInstance();
        Transformer transformer = factory1.newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(f));
        System.out.println("XML SCRIS!");
        System.out.println("--------------------CERINTA 4--------------------------");
        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("SERVER STARTED ON 8080");
            while (true) {
                Socket client = server.accept();
                System.out.println("Un client s-a conectat");
                new RequestHandler(client).start();
            }

        }
    }

    static class RequestHandler extends Thread {
        Socket client;

        public RequestHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                var out = new PrintWriter(client.getOutputStream(), true);

                int codSantier = Integer.parseInt(in.readLine());
                System.out.println(codSantier);
                Santier s = santiere.stream().filter(sa -> sa.codSantier == codSantier).findFirst().orElse(null);
                if (s == null) {
                    out.println("Cod santier invalid");
                } else {
                    out.println(s.obiectiv + ", " + s.valoare);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}