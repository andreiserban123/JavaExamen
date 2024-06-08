import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Main {
    static List<Carte> biblioteca = new ArrayList<>();
    static List<Cititor> cititori = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("biblioteca.xml"));

        var nodList = doc.getElementsByTagName("carte");
        for (int i = 0; i < nodList.getLength(); i++) {
            var nod = nodList.item(i);
            var el = (Element) nod;
            String titlu = el.getElementsByTagName("titlu").item(0).getTextContent();
            String autor = el.getElementsByTagName("autor").item(0).getTextContent();
            String gen = el.getElementsByTagName("gen").item(0).getTextContent();
            int nrPagini = Integer.parseInt(el.getElementsByTagName("numarPagini").item(0).getTextContent());
            biblioteca.add(new Carte(titlu, autor, gen, nrPagini));
        }
        biblioteca.forEach(System.out::println);

        try (BufferedReader bf = new BufferedReader(new FileReader("cititori.json"))) {
            var tokener = new JSONTokener(bf);
            var arr = new JSONArray(tokener);
            for (int i = 0; i < arr.length(); i++) {
                var obj1 = arr.getJSONObject(i);
                String nume = obj1.getString("nume");
                List<CarteImprumutata> cartiImprumutate = new ArrayList<>();
                var arr2 = obj1.getJSONArray("cartiImprumutate");
                for (int j = 0; j < arr2.length(); j++) {
                    var obj2 = arr2.getJSONObject(j);
                    CarteImprumutata ci = new CarteImprumutata();
                    ci.titlu = obj2.getString("titlu");
                    ci.dataImprumut = obj2.getString("dataImprumut");
                    cartiImprumutate.add(ci);
                }
                cititori.add(new Cititor(nume, cartiImprumutate));
            }
        }

        cititori.forEach(System.out::println);

        System.out.println("----------------Cerinta 1------------------------");
        Map<String, Integer> nrPerGen = biblioteca.stream().collect(Collectors.groupingBy(c -> c.gen, Collectors.summingInt(c -> c.nrPagini)));
        nrPerGen.forEach((k, v) -> {
            System.out.println(k + "->" + v);
        });
        System.out.println("----------------Cerinta 2------------------------");

        cititori.stream().filter(c -> c.cartiImprumutate.size() > 2).sorted(Comparator.comparing(c -> c.nume)).forEach(
                System.out::println
        );
        System.out.println("----------------Cerinta 3------------------------");

        try (FileWriter fw = new FileWriter("carti.json")) {

            var arr = new JSONArray();
            biblioteca.forEach(carte -> {
                var obj1 = new JSONObject();
                obj1.put("titlu", carte.titlu);
                obj1.put("autor", carte.autor);
                obj1.put("gen", carte.gen);
                obj1.put("nrPagini", carte.nrPagini);
                arr.put(obj1);
            });

            arr.write(fw, 0, 2);
            System.out.println("fisier scris!");

        }
        System.out.println("----------------Cerinta 4------------------------");
        File f = new File("cititori.xml");
        doc = builder.newDocument();
        var root = doc.createElement("cititori");
        doc.appendChild(root);
        for (var citior : cititori) {
            var el = doc.createElement("cititor");
            root.appendChild(el);
            var nume = doc.createElement("nume");
            nume.setTextContent(citior.nume);
            el.appendChild(nume);
            var cartiImprumutate = doc.createElement("cartiImprumutate");
            el.appendChild(cartiImprumutate);
            for (var carteImprumutate : citior.cartiImprumutate) {
                var elCarte = doc.createElement("carteImprumutata");
                cartiImprumutate.appendChild(elCarte);
                var titlu = doc.createElement("titlu");
                titlu.setTextContent(carteImprumutate.titlu);
                elCarte.appendChild(titlu);
                var dataImprumut = doc.createElement("dataImprumut");
                dataImprumut.setTextContent(carteImprumutate.dataImprumut);
                elCarte.appendChild(dataImprumut);
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(f));

        System.out.println("Fisierul xml a fost scris!");
        System.out.println("---------------Cerint 5-----------------");
        try (var connection = DriverManager.getConnection("jdbc:sqlite:biblioteca.db");
             var stm = connection.createStatement();
        ) {
            stm.execute("CREATE TABLE IF NOT EXISTS Carti(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nume text," +
                    "autor text," +
                    "nrPagini integer," +
                    "nrImprumuturi integer" +
                    ")");

            for (var carte : biblioteca) {
                int counter = 0;
                for (var cititor : cititori) {
                    for (var imprumut : cititor.cartiImprumutate) {
                        if (imprumut.titlu.equals(carte.titlu))
                            counter++;
                    }
                }
                stm.execute("INSERT INTO CARTI(nume, autor, nrPagini, nrImprumuturi) values (" +
                        "'" + carte.titlu + "'," +
                        "'" + carte.autor + "'," +
                        carte.nrPagini + "," + counter +
                        ")");
            }
            stm.close();
            System.out.println("Commited with success!");
        }
        System.out.println("--------------------------CERINTA 6--------------------------");
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("SERVER STARTED ON 8080");
            while (true){
                var client = serverSocket.accept();
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
                String nume = in.readLine();
                AtomicBoolean ok = new AtomicBoolean(false);
                cititori.stream().filter(c -> c.nume.equals(nume)).forEach(cititor -> {
                    String msg = cititor.cartiImprumutate.stream().map(c -> c.titlu).collect(Collectors.joining(","));
                    System.out.println("Mesajul transmis: "+msg);
                    ok.set(true);
                    out.println(msg);
                });
                if(!ok.get()){
                    out.println("Numele nu se regaseste in lista de clienti!");
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}