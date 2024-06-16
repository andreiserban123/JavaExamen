import org.json.JSONArray;
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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

public class Main {
    static List<Candidat> candidati = new ArrayList<>();
    static List<Liceu> licee = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (BufferedReader bf = new BufferedReader(new FileReader("candidati.json"))) {
            var tokener = new JSONTokener(bf);
            var arr = new JSONArray(tokener);
            for (int i = 0; i < arr.length(); i++) {
                var candidat = arr.getJSONObject(i);
                int cod_candidat = candidat.getInt("cod_candidat");
                String nume = candidat.getString("nume_candidat");
                double medie = candidat.getDouble("media");
                var ops = candidat.getJSONArray("optiuni");
                List<Optiune> optiuni = new ArrayList<>();
                for (int j = 0; j < ops.length(); j++) {
                    var op = ops.getJSONObject(j);
                    int cod_liceu = op.getInt("cod_liceu");
                    int cod_specializare = op.getInt("cod_specializare");
                    optiuni.add(new Optiune(cod_liceu, cod_specializare));
                }
                candidati.add(new Candidat(cod_candidat, nume, medie, optiuni));
            }
        }

        try (BufferedReader bf = new BufferedReader(new FileReader("licee.txt"))) {
            String line = null;
            while ((line = bf.readLine()) != null) {
                String[] values = line.split(",");
                Liceu l = new Liceu();
                l.cod_liceu = Integer.parseInt(values[0]);
                l.nume_liceu = values[1];
                l.nr_specializari = Integer.parseInt(values[2]);
                l.specializari = new ArrayList<>();
                line = bf.readLine();
                values = line.trim().split(",");
                int j = 0;
                for (int i = 0; i < l.nr_specializari; i++) {
                    Specializare specializare = new Specializare();
                    specializare.cod_specializare = Integer.parseInt(values[j]);
                    specializare.nr_locuri = Integer.parseInt(values[j + 1]);
                    j += 2;
                    l.specializari.add(specializare);
                }
                licee.add(l);
            }
        }
        System.out.println("-----------------CERINTA 1---------------------");
        candidati.stream().filter(c -> c.media >= 9).forEach(System.out::println);
        System.out.println("-----------------CERINTA 2---------------------");
        Map<Liceu, Integer> liceuSiLocuri = new HashMap<>();
        licee.forEach(liceu -> {
            int nrLocuri = liceu.specializari.stream().flatMapToInt(specializare -> IntStream.of(specializare.nr_locuri))
                    .sum();
            liceuSiLocuri.put(liceu, nrLocuri);
        });
        liceuSiLocuri.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(
                e -> System.out.println(e.getKey().cod_liceu + " " + e.getKey().nume_liceu + " are " + e.getValue() + " locuri")
        );

        System.out.println("-----------------CERINTA 3---------------------");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("jurnal.txt"))) {
            candidati.stream().sorted((c1, c2) -> {
                if (c1.optiuni.size() == c2.optiuni.size()) {
                    return Double.compare(c2.media, c1.media);
                }
                return Integer.compare(c2.optiuni.size(), c1.optiuni.size());
            }).forEach(c -> {
                try {
                    bw.write(c + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        System.out.println("Fisier scris!");
        System.out.println("-----------------CERINTA 4---------------------");
        try (var conn = DriverManager.getConnection("jdbc:sqlite:exam.db")) {

            try (var stm = conn.createStatement()) {
                stm.executeUpdate("create table IF NOT EXISTS CANDIDATI (cod_candidat integer,nume_candidat text,medie double,numar_optiuni integer)");
                stm.executeUpdate("delete from candidati");
            }
            try (var pstm = conn.prepareStatement("INSERT INTO CANDIDATI values(?,?,?,?)")) {
                candidati.forEach(c -> {
                    try {
                        pstm.setInt(1, c.cod_candidat);
                        pstm.setString(2, c.nume_candidat);
                        pstm.setDouble(3, c.media);
                        pstm.setInt(4, c.optiuni.size());
                        pstm.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }


            try (var stm = conn.createStatement();
                 var resultSet = stm.executeQuery("SELECT *FROM CANDIDATI");
            ) {
                while (resultSet.next()) {
                    System.out.println(resultSet.getInt(1) + " " + resultSet.getString(2));
                }
            }
        }
        System.out.println("-------------------CERINTA 5----------------------");
        try (FileWriter fw = new FileWriter("candidati.xml")) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            var root = document.createElement("candidati");
            document.appendChild(root);

            for (Candidat candidat : candidati) {
                var cand = document.createElement("candidat");
                root.appendChild(cand);
                var cod_cand = document.createElement("cod_candidat");
                cand.appendChild(cod_cand);
                cod_cand.setTextContent(String.valueOf(candidat.cod_candidat));
                var nume = document.createElement("nume_candidat");
                cand.appendChild(nume);
                nume.setTextContent(candidat.nume_candidat);
                var media = document.createElement("media");
                cand.appendChild(media);
                media.setTextContent(String.valueOf(candidat.media));
                var optiuni = document.createElement("optiuni");
                for (var optiune : candidat.optiuni) {
                    var op = document.createElement("optiune");
                    optiuni.appendChild(op);
                    var cod_specializare = document.createElement("cod_specializare");
                    op.appendChild(cod_specializare);
                    cod_specializare.setTextContent(String.valueOf(optiune.cod_specializare));
                    var cod_liceu = document.createElement("cod_liceu");
                    op.appendChild(cod_liceu);
                    cod_liceu.setTextContent(String.valueOf(optiune.cod_liceu));
                }
                cand.appendChild(optiuni);
            }
            TransformerFactory factory1 = TransformerFactory.newInstance();
            Transformer transformer = factory1.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(fw));
            System.out.println("XML scris");

            //citire xml
            document = builder.parse("candidati.xml");
            var nodeList = document.getElementsByTagName("candidat");
            for (int i = 0; i < nodeList.getLength(); i++) {
                var node = nodeList.item(i);
                var el = (Element) node;
                int cod_candidat = Integer.parseInt(el.getElementsByTagName("cod_candidat").item(0).getTextContent());
                String nume = el.getElementsByTagName("nume_candidat").item(0).getTextContent();
                System.out.println(cod_candidat + " " + nume);
                var ops = el.getElementsByTagName("optiune");
                for (int j = 0; j < ops.getLength(); j++) {
                    var node2 = ops.item(j);
                    var op = (Element) node2;
                    int cod_specializare = Integer.parseInt(op.getElementsByTagName("cod_specializare").item(0).getTextContent());
                    int cod_liceu = Integer.parseInt(op.getElementsByTagName("cod_liceu").item(0).getTextContent());
                    System.out.println("\t" + cod_specializare + " " + cod_liceu);
                }
            }
        }
        System.out.println("------------------CERINTA 6--------------------");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("candidati.bin"))) {
            oos.writeInt(candidati.size());
            for (Candidat candidat : candidati) {
                oos.writeInt(candidat.cod_candidat);
                oos.writeUTF(candidat.nume_candidat);
                oos.writeDouble(candidat.media);
                oos.writeInt(candidat.optiuni.size());
                for (Optiune optiune : candidat.optiuni) {
                    oos.writeInt(optiune.cod_specializare);
                    oos.writeInt(optiune.cod_liceu);
                }
            }
        }

        candidati.forEach(System.out::println);
        System.out.println("Fisier binar scris cu success!");

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("candidati.bin"))) {
            int nrCand = ois.readInt();
            candidati = new ArrayList<>(nrCand);
            for (int i = 0; i < nrCand; i++) {
                Candidat c = new Candidat();
                c.cod_candidat = ois.readInt();
                c.nume_candidat = ois.readUTF();
                c.media = ois.readDouble();
                int nrOptiuni = ois.readInt();
                c.optiuni = new ArrayList<>();
                for (int j = 0; j < nrOptiuni; j++) {
                    Optiune ops = new Optiune();
                    ops.cod_specializare = ois.readInt();
                    ops.cod_liceu = ois.readInt();
                    c.optiuni.add(ops);
                }
                candidati.add(c);
            }
        }
        candidati.forEach(System.out::println);
    }
}