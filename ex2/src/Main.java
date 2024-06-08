import org.json.JSONArray;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static List<MateriePrima> materiiPrime = new ArrayList<>();
    static List<Produs> produse = new ArrayList<>();

    public static void main(String[] args) {
        try {
            String url = "jdbc:sqlite:examen.db";
            var connection = DriverManager.getConnection(url);
            System.out.println("Conexiunea la bd a fost realizata cu succes");
            Statement statement = connection.createStatement();
            var resultSet = statement.executeQuery("SELECT * FROM MateriiPrime");
            while (resultSet.next()) {
                MateriePrima m = new MateriePrima(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getFloat(3), resultSet.getFloat(4), resultSet.getString(5));
                materiiPrime.add(m);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (FileReader file = new FileReader("Produse.json")) {
            var token = new JSONTokener(file);
            var jsonArr = new JSONArray(token);
            for (int i = 0; i < jsonArr.length(); i++) {
                Produs p = new Produs();
                p.consumuri = new ArrayList<>();
                var jsonObj = jsonArr.getJSONObject(i);
                int cod = jsonObj.getInt("Cod produs");
                var denumire = jsonObj.getString("Denumire produs");
                p.codProdus = cod;
                p.denumireProdus = denumire;
                var consumuri = jsonObj.getJSONArray("Consumuri");
                for (int j = 0; j < consumuri.length(); j++) {
                    var consum = consumuri.getJSONObject(j);
                    int codMaterie = consum.getInt("Cod materie prima");
                    float cantitate = consum.getFloat("Cantitate");
                    Consum c = new Consum(codMaterie, cantitate);
                    p.consumuri.add(c);
                }
                produse.add(p);
            }
            System.out.println("Produse: ");
            produse.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("--------------Cerinta 1-------------------");
        double valoare = materiiPrime.stream().mapToDouble(m -> m.getPretUnitar() * m.getCantitate()
        ).sum();
        System.out.println("valoarea totala:" + valoare);
        System.out.println("-------------Cerinta 2---------------------");
        produse.stream().sorted((o1, o2) -> Integer.compare(o2.consumuri.size(), o1.consumuri.size())).forEach(System.out::println);
        System.out.println("----------------Cerinta 3-----------------");
        produse.forEach(produs ->
                produs.consumuri.forEach(consum ->
                        materiiPrime.parallelStream()
                                .filter(materiePrima -> materiePrima.getCod() == consum.getCodMateriePrima())
                                .forEach(materiePrima -> materiePrima.setCantitate(materiePrima.getCantitate() - consum.cantitate))
                )
        );
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = doc.createElement("materii_prime");
            doc.appendChild(rootElement);
            for (MateriePrima materiePrima : materiiPrime) {
                Element materieElement = doc.createElement("materie_prima");
                materieElement.setAttribute("cod", String.valueOf(materiePrima.getCod()));
                materieElement.setAttribute("denumire", materiePrima.getDenumire());
                valoare = materiePrima.getCantitate() * materiePrima.getPretUnitar();
                materieElement.setAttribute("valoare", String.valueOf(valoare));
                rootElement.appendChild(materieElement);
            }
            //salvare efectiva
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new FileWriter("stoc.xml")));
            System.out.println("Fisier salvat cu succes!");

        } catch (ParserConfigurationException | IOException | TransformerException e) {
            throw new RuntimeException(e);
        }

        System.out.println("----------------Cerinta 4----------------------");
        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("Serverul a pornit pe portul 8080");
            while (true) {
                var socket = server.accept();
                System.out.println("S-a conectat un client");
                new Thread(new ClientThread(socket, materiiPrime)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class ClientThread implements Runnable {
        private Socket socket;
        private List<MateriePrima> materiiPrime;

        public ClientThread(Socket socket, List<MateriePrima> materiiPrime) {
            this.socket = socket;
            this.materiiPrime = materiiPrime;
        }

        @Override
        public void run() {
            try {
                var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                var out = new PrintWriter(socket.getOutputStream(), true);
                int cod = Integer.parseInt(in.readLine());
                double valoare = materiiPrime.stream()
                        .filter(materiePrima -> materiePrima.getCod() == cod)
                        .mapToDouble(materiePrima -> materiePrima.getCantitate() * materiePrima.getPretUnitar())
                        .sum();
                out.println(valoare);
                System.out.println("S-a trimis catre client valoarea: " + valoare);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}