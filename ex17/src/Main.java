import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static List<Produs> produse = new ArrayList<>();
    static List<Vanzare> vanzari = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader("produse.json"))) {
            var tok = new JSONTokener(br);
            var arr = new JSONArray(tok);

            for (int i = 0; i < arr.length(); i++) {
                var obj = arr.getJSONObject(i);
                Produs p = new Produs();
                p.id = obj.getInt("id");
                p.pret = obj.getDouble("pret");
                p.denumire = obj.getString("denumire");
                p.categorie = obj.getString("categorie");
                produse.add(p);
            }
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("vanzari.xml"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        var nodeList = document.getElementsByTagName("vanzare");
        for (int i = 0; i < nodeList.getLength(); i++) {
            var node = nodeList.item(i);
            var el = (Element) node;
            int id = Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
            int id_produs = Integer.parseInt(el.getElementsByTagName("id_produs").item(0).getTextContent());
            int cantitate = Integer.parseInt(el.getElementsByTagName("cantitate").item(0).getTextContent());
            Date data = dateFormat.parse(el.getElementsByTagName("data").item(0).getTextContent());
            Vanzare v = new Vanzare(id, id_produs, cantitate, data);
            vanzari.add(v);
        }

        // Să se afișeze la consolă lista produselor și prețul mediu al acestora pe categorii
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        System.out.println("-----------------------CERINTA 1------------------------");
        produse.forEach(System.out::println);
        produse.stream().collect(Collectors.groupingBy(p -> p.categorie, Collectors.averagingDouble(p -> p.pret))).forEach((k, v) -> System.out.println("Pentru categoria " + k + " pretul mediu este " + decimalFormat.format(v)));
        System.out.println("-----------------------CERINTA 2------------------------");
        // Să se afișeze la consolă cantitatea totală de produse vândute pe fiecare categorie, ordonate descrescător după cantitate
        vanzari.stream().collect(Collectors.groupingBy(v -> produse.stream().filter(p -> p.id == v.id_produs).map(produs -> produs.categorie)
                .findFirst().orElse("nueste"), Collectors.summingInt(v -> v.cantitate))).entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(e -> System.out.println("Din categoria " + e.getKey() + " s-a vandut un nr de " + e.getValue() + " produse"));

        System.out.println("-----------------------CERINTA 3------------------------");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("raport_vanzari.txt"))) {
            produse.forEach(produs -> {

                long count = vanzari.stream().filter(v -> v.id_produs == produs.id).count();
                if (count > 0) {
                    try {
                        bw.write(produs.denumire + System.lineSeparator());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    vanzari.stream().filter(v -> v.id_produs == produs.id).forEach(v -> {
                        try {
                            bw.write("Data: " + dateFormat.format(v.data) + ", Cantitate:" + v.cantitate + System.lineSeparator());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

            });
            System.out.println("Fisier scris");
        }

        System.out.println("-----------------------CERINTA 4------------------------");
        // scrie in json date despre toate produsele vandute
        var arr = new JSONArray();
        List<Produs> produseVandute = produse.stream().filter(p -> vanzari.stream().anyMatch(v -> v.id_produs == p.id)).toList();
        for (Produs produs : produseVandute) {
            var obj = new JSONObject();
            arr.put(obj);
            obj.put("produs", produs.denumire);
            var vanz = new JSONArray();
            obj.put("vanzari", vanz);
            vanzari.stream().filter(v -> v.id_produs == produs.id).forEach(v -> {
                var va = new JSONObject();
                va.put("data", dateFormat.format(v.data));
                va.put("cantitate", v.cantitate);
                vanz.put(va);
            });
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("raport.json"))) {
            arr.write(bw, 0, 2);
            System.out.println("Fisier scris");
        }
        System.out.println("-----------------------CERINTA 5------------------------");
        // Să se afișeze la consolă lista produselor care au fost vândute într-o cantitate totală mai mare de 2 bucăți.
        vanzari.stream().collect(Collectors.groupingBy(v -> v.id_produs, Collectors.summingInt(v -> v.cantitate))).forEach((key, value) -> {
            if (value > 2) {
                int id = key;
                produse.stream().filter(p -> p.id == id).forEach(System.out::println);
            }
        });
        System.out.println("-----------------------CERINTA 6------------------------");
        //lista produselor sortate dupa pret
        produse.stream().sorted((p1, p2) -> Double.compare(p2.pret, p1.pret)).forEach(System.out::println);
        System.out.println("-----------------------CERINTA 7------------------------");
        // clientul trimite un id de produs, iar serverul returneaza denumire produs + vanzarile cu data si cantitate
        Thread server = new Thread(()->{
            try (ServerSocket serverSocket = new ServerSocket(8080)) {
                System.out.println("SERVER STARTED ON 8080");
                var cli = serverSocket.accept();
                System.out.println("[SERVER] client s-a conectat");
                var in = new BufferedReader(new InputStreamReader(cli.getInputStream()));
                var out = new PrintWriter(cli.getOutputStream(), true);

                int cod = Integer.parseInt(in.readLine());
                Produs p = produse.stream().filter(pr -> pr.id == cod).findFirst().orElse(null);
                if (p == null) {
                    out.println("ID-ul nu exista");
                } else {
                    String responose = p.denumire + " ";
                    for (Vanzare vanzare : vanzari) {
                        if (vanzare.id_produs == p.id) {
                            responose += dateFormat.format(vanzare.data) + " ";
                            responose += vanzare.cantitate + " ";
                        }
                    }
                    out.println(responose);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread client = new Thread(()->{
            try (Socket con = new Socket("localhost", 8080)) {
                var in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                var out = new PrintWriter(con.getOutputStream(), true);

                var stdIn = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Introdu un id de produs:");
                var id = stdIn.readLine();
                out.println(id);
                System.out.println("Am transmis id " + id);
                var msg = in.readLine();
                System.out.println("Am primit:");
                System.out.println(msg);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        server.start();
        Thread.sleep(1000);
        client.start();
        server.join();
        client.join();
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

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                int cod = Integer.parseInt(in.readLine());
                Produs p = produse.stream().filter(pr -> pr.id == cod).findFirst().orElse(null);
                if (p == null) {
                    out.println("ID-ul nu exista");
                } else {
                    String responose = p.denumire + " ";
                    for (Vanzare vanzare : vanzari) {
                        if (vanzare.id_produs == p.id) {
                            responose += dateFormat.format(vanzare.data) + " ";
                            responose += vanzare.cantitate + " ";
                        }
                    }
                    out.println(responose);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}