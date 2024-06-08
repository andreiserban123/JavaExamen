import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static List<Angajat> angajati;
    static List<Proiect> proiecte = new ArrayList<>();
    static List<Evaluare> evaluari = new ArrayList<>();
    static List<ProiectFinalizat> proiecteFinalizate = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (var br = new BufferedReader(new FileReader("angajati.csv"))) {
            angajati = br.lines().map(l -> new Angajat(
                    Integer.parseInt(l.split(",")[0]),
                    l.split(",")[1],
                    l.split(",")[2],
                    Double.parseDouble(l.split(",")[3])
            )).toList();
        }

        try (var file = new FileReader("proiecte.json")) {
            var tokener = new JSONTokener(file);

            JSONArray arr = new JSONArray(tokener);
            for (int i = 0; i < arr.length(); i++) {
                var obj = arr.getJSONObject(i);
                int codProiect = obj.getInt("cod_proiect");
                String numeProiect = obj.getString("nume_proiect");
                double buget = obj.getDouble("buget");
                JSONArray echipaArr = obj.getJSONArray("echipa");
                List<Integer> echipa = new ArrayList<>();
                for (int j = 0; j < echipaArr.length(); j++) {
                    int codAngajat = echipaArr.getInt(j);
                    echipa.add(codAngajat);
                }
                int[] echipaArray = echipa.stream().mapToInt(Integer::intValue).toArray();
                proiecte.add(new Proiect(codProiect, numeProiect, buget, echipaArray));
            }
        }
        try {
            File xmlFile = new File("evaluari.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("evaluare");
            for (int i = 0; i < nList.getLength(); i++) {
                var node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    int idAngajat = Integer.parseInt(element.getElementsByTagName("id_angajat").item(0).getTextContent());
                    int an = Integer.parseInt(element.getElementsByTagName("an").item(0).getTextContent());
                    double scor = Double.parseDouble(element.getElementsByTagName("scor").item(0).getTextContent());
                    Evaluare e = new Evaluare(idAngajat, an, scor);
                    evaluari.add(e);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try (var connection = DriverManager.getConnection("jdbc:sqlite:proiecte_finalizate.db");
             Statement stm = connection.createStatement();
             var resulSet = stm.executeQuery("SELECT * from proiecte_finalizate");
        ) {
            while (resulSet.next()) {
                proiecteFinalizate.add(new ProiectFinalizat(resulSet.getInt(1), resulSet.getString(2),
                        resulSet.getInt(3)));
            }
        }

        System.out.println("---------------------CERINTA 1----------------------");
        Map<String, Double> map = angajati.stream().collect(Collectors.groupingBy(
                a -> a.departament,
                Collectors.averagingDouble(a -> a.salariu)
        ));
        map.forEach((v, k) -> {
            System.out.println(v + "->" + k);
        });
        System.out.println("---------------------CERINTA 2----------------------");
        List<Angajat> angajatiPesteMedie = angajati.stream()
                .filter(angajat -> angajat.salariu > map.get(angajat.departament))
                .sorted(Comparator.comparing(angajat -> angajat.nume))
                .toList();
        angajatiPesteMedie.forEach(System.out::println);

        System.out.println("--------------------CERINTA 3--------------------------");

        angajati.stream().sorted((a1, a2) -> Double.compare(a2.salariu, a1.salariu)).limit(3).
                forEach(angajat -> System.out.println(angajat));

        System.out.println("--------------------CERINTA 4--------------------------");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("angajati.bin"))) {
            oos.writeObject(angajati);
            System.out.println("S-a scris cu success!");
        }
        var array = new JSONArray();
        for (var proiect : proiecte) {
            var object = new JSONObject();
            array.put(object);
            object.put("codProiect", proiect.codProiect);
            object.put("numeProiect", proiect.numeProiect);
            object.put("buget", proiect.buget);
            object.put("echipa", proiect.echipa);
        }
        evaluari.add( new Evaluare( 3, 2021, 9.5));
        evaluari.add( new Evaluare( 4, 2021, 8.5));
        evaluari.forEach(System.out::println);

        try (FileWriter writer = new FileWriter("proiecte_finalizate.json")) {
            array.write(writer, 2,0);
        }
        
        System.out.println("S-a scris in json cu sucess!");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element root = document.createElement("proiecte_finalizate");
        document.appendChild(root);
        for (var proiect : proiecteFinalizate) {
            Element proiectElement = document.createElement("proiect_finalizat");
            root.appendChild(proiectElement);
            Element codProiect = document.createElement("cod_proiect");
            codProiect.appendChild(document.createTextNode(String.valueOf(proiect.codProiect)));
            proiectElement.appendChild(codProiect);
            Element numeProiect = document.createElement("nume_proiect");
            numeProiect.appendChild(document.createTextNode(proiect.numeProiect));
            proiectElement.appendChild(numeProiect);
            Element anFinalizare = document.createElement("an_finalizare");
            anFinalizare.appendChild(document.createTextNode(String.valueOf(proiect.anFinalizare)));
            proiectElement.appendChild(anFinalizare);
        }
        // create the xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        // create the xml file
        transformer.transform(new DOMSource(document), new StreamResult(new File("proiecte_finalizate.xml")));
        System.out.println("S-a scris in xml cu success!");
    }
}