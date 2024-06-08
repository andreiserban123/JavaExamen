import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {

    static List<Specializare> specializari = new ArrayList<>();
    static List<Candidat> candidati = new ArrayList<>();

    static Map<String, Integer> map = new HashMap<>();

    public static void main(String[] args) throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite:facultate.db"); var stm = connection.createStatement(); var result = stm.executeQuery("SELECT * FROM specializari");) {
            while (result.next()) {
                int cod = result.getInt(1);
                String denumire = result.getString(2);
                int locuri = result.getInt(3);
                Specializare s = new Specializare(cod, denumire, locuri);
                specializari.add(s);
            }
        }
        specializari.forEach(System.out::println);

        try (BufferedReader bf = new BufferedReader(new FileReader("inscrieri.txt"))) {
            candidati = bf.lines().map(line -> new Candidat(Long.parseLong(line.split(",")[0]), line.split(",")[1], Double.parseDouble(line.split(",")[2]), Integer.parseInt(line.split(",")[3]))).toList();
            candidati.forEach(System.out::println);
        }
        System.out.println("----------------Cerinta 1-------------------");
        int nrLocuri = specializari.stream().mapToInt(s -> s.locuri).sum();
        System.out.println("Nr locuri facultate: " + nrLocuri);
        System.out.println("----------------Cerinta 2-------------------");
        specializari.forEach(specializare -> {

            Long nr = candidati.stream().filter(c -> c.codSpecializare == specializare.codSpecializare).count();

            int ramase = (int) (specializare.locuri - nr);
            map.put(specializare.denumire, ramase);
            if (ramase >= 100) {
                System.out.println(specializare.codSpecializare + " " + specializare.denumire + " " + ramase);
            }
        });
        System.out.println("----------------Cerinta 3-------------------");
        try (FileWriter fw = new FileWriter("inscrieri_specializari.json")) {
            var arr = new JSONArray();
            specializari.forEach(specializare -> {
                Long nr = candidati.stream().filter(c -> c.codSpecializare == specializare.codSpecializare).count();
                double medie = candidati.stream().filter(c -> c.codSpecializare == specializare.codSpecializare).mapToDouble(c -> c.notaBac).average().orElse(0);
                System.out.println(specializare + " " + nr + " " + medie);

                var obj = new JSONObject();
                obj.put("cod_specializare", specializare.codSpecializare);
                obj.put("denumire", specializare.denumire);
                obj.put("numar_inscriere", nr);
                obj.put("medie", medie);
                arr.put(obj);
            });

            arr.write(fw, 0, 2);
            System.out.println("fisierul a fost scris");
        }

        System.out.println("----------------Cerinta 4-------------------");

        Thread server = new Thread(() -> {
            try (var sok = new ServerSocket(8080)) {
                System.out.println("Server started on port " + 8080);
                try (var cli = sok.accept()) {
                    var in = new BufferedReader(new InputStreamReader(cli.getInputStream()));
                    var out = new PrintWriter(cli.getOutputStream(), true);

                    var demumire = in.readLine();
                    System.out.println("Am primit: " + demumire);
                    int nr = map.getOrDefault(demumire, 0);
                    out.println(nr);
                    System.out.println("Am trimis nr: " + nr);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread client = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 8080)) {
                System.out.println("Clientul s-a conectat!");
                var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                var out = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("Scriem Cibernetica");
                out.println("Cibernetica");
                int nr = Integer.parseInt(in.readLine());
                System.out.println("Am primit de la server nr de locuri ramase: " + nr);
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
}