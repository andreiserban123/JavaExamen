import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static List<Santier> santiere = new ArrayList<>();
    static List<Capitol> capitole = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (FileReader fr = new FileReader("santiere.json")) {
            JSONTokener tokener = new JSONTokener(fr);
            var arr = new JSONArray(tokener);
            for (int i = 0; i < arr.length(); i++) {
                var obj = arr.getJSONObject(i);
                santiere.add(new Santier(obj.getInt("Cod Santier"),
                        obj.getString("Localitate"),
                        obj.getString("Strada"),
                        obj.getString("Obiectiv"),
                        obj.getDouble("Valoare")
                ));
            }
        }
        String url = "jdbc:sqlite:devize.db";
        try (var connection = DriverManager.getConnection(url);
             var stm = connection.createStatement();
             var resultSet = stm.executeQuery("SELECT * FROM capitole");
        ) {
            while (resultSet.next()) {
                capitole.add(new Capitol(resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDouble(5),
                        resultSet.getDouble(6)
                ));

            }
        }

        System.out.println("--------------------------CERINTA 1-------------------------");
        santiere.forEach(System.out::println);
        System.out.println("Valoare medie estimata: " + santiere.stream().mapToDouble(s -> s.valoare).average().getAsDouble());
        System.out.println("--------------------------CERINTA 2-------------------------");
        Map<Integer, Double> cantitatiPerCapitol = capitole.stream().collect(Collectors.groupingBy(c -> c.codCapitol, Collectors.summingDouble(
                c -> c.cantitate
        )));
        cantitatiPerCapitol.forEach((v, k) ->
                System.out.println("Cod capitol " + v + ", cantitate " + k)
        );
        System.out.println("--------------------------CERINTA 3-------------------------");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("devize.txt"))) {
            capitole.stream().mapToInt(c -> c.codCapitol).distinct().forEach(c -> {
                System.out.println(c);
                Map<Integer, Double> santierVal = capitole.stream().filter(capitol -> capitol.codCapitol == c).collect(
                        Collectors.groupingBy(capitol -> capitol.codSantier, Collectors.summingDouble(capitol -> capitol.cantitate *
                                capitol.pu))
                );
                santierVal.forEach((v, k) -> {
                    System.out.println("cod santier " + v + ", valoare " + k);
                });
            });
        }

        System.out.println("--------------------------CERINTA 4-------------------------");
        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("Server started on 8080");
            while (true) {
                Socket client = server.accept();
                new ClientHandler(client).start();
            }
        }

    }

    static class ClientHandler extends Thread {
        private Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                var out = new PrintWriter(client.getOutputStream(), true);

                var codSantier = Integer.parseInt(in.readLine());
                System.out.println("Am primit codStantier: " + codSantier);
                Map<String, Double> m = santiere.stream().filter(s -> s.codSantier == codSantier).collect(Collectors.toMap(s -> s.obiectiv,
                        s -> s.valoare
                ));
                m.forEach((k, v) -> {
                    out.println(k + "," + v);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}