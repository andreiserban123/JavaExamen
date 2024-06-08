import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static List<Evaluare> evaluari = new ArrayList<>();

    public static void main(String[] args) {
        try {
            DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("evaluari.xml");

            var nodeList = document.getElementsByTagName("evaluare");
            for (int i = 0; i < nodeList.getLength(); i++) {
                var node = nodeList.item(i);

                var el = (Element)node;
                int idAngajat = Integer.parseInt(el.getElementsByTagName("id_angajat").item(0).getTextContent());
                int an = Integer.parseInt(el.getElementsByTagName("an").item(0).getTextContent());
                double scor= Double.parseDouble(el.getElementsByTagName("scor").item(0).getTextContent());

                evaluari.add(new Evaluare(idAngajat, an, scor));
            }
            evaluari.forEach(System.out::println);

            Document myDoc = builder.newDocument();
            Element root = myDoc.createElement("evaluari");
            myDoc.appendChild(root);
            for (var evaluare : evaluari) {
                Element evaluareEl = myDoc.createElement("evaluare");
                root.appendChild(evaluareEl);
                Element idAngajatEl = myDoc.createElement("id_angajat");
                idAngajatEl.setTextContent(String.valueOf(evaluare.idAngajat));
                evaluareEl.appendChild(idAngajatEl);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(myDoc), new StreamResult(new File("test.xml")));
            System.out.println("Am scris fisierul XML");



            var arr = new JSONArray();
            for(var evaluare : evaluari){
                var object = new JSONObject();
                arr.put(object);
                object.put("id_angajat", evaluare.idAngajat);
            }
            try(FileWriter writer = new FileWriter("test.json")){
                arr.write(writer, 0,2);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
