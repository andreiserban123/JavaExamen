import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static List<Carte> carti = new ArrayList<>();
    static List<Imprumut> biblioteca = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (BufferedReader bf = new BufferedReader(new FileReader("Date\\carti.txt"))) {
            carti = bf.lines().map(line -> new Carte(line.split("\t")[0],
                    line.split("\t")[1],
                    line.split("\t")[2],
                    Integer.parseInt(line.split("\t")[3])
            )).toList();
        }

        try (var conn = DriverManager.getConnection("jdbc:sqlite:Date\\biblioteca.db");
             var stm = conn.createStatement();
             var resultSet = stm.executeQuery("SELECT * from imprumuturi");
        ) {
            while (resultSet.next()) {
                String nume = resultSet.getString(1);
                String cota = resultSet.getString(2);
                int zile = resultSet.getInt(3);
                biblioteca.add(new Imprumut(nume, cota, zile));
            }
        }
        System.out.println("----------------------CERINTA 1-----------------------");
        carti.stream().filter(c -> c.an < 1940).sorted(Comparator.comparing(c -> c.tilu)).forEach(System.out::println);
        System.out.println("----------------------CERINTA 2-----------------------");
        biblioteca.stream().map(i -> i.numeStudent).distinct().forEach(System.out::println);
        System.out.println("----------------------CERINTA 3-----------------------");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Date\\cititori.txt"))) {
            bw.write("Nume Student" + "\t\t - " + "Total zile imprumut" + System.lineSeparator());

            biblioteca.stream().collect(Collectors.groupingBy(i -> i.numeStudent, Collectors.summingInt(c -> c.nrZile))).entrySet()
                    .stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).forEach(e -> {
                        try {
                            bw.write(e.getKey() + "\t\t\t - " + e.getValue() + System.lineSeparator());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
            System.out.println("Fisier scris!");
        }
        System.out.println("----------------------CERINTA 4-----------------------");
        Thread server = new Thread(() -> {
            try (ServerSocket socket = new ServerSocket(8080)) {
                System.out.println("[SERVER] STARTED on 8080");
                var client = socket.accept();
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                var out = new PrintWriter(client.getOutputStream(), true);
                String cota = in.readLine();
                var carte = carti.stream().filter(c -> c.cota.equals(cota)).findFirst().orElse(null);
                if (carte == null) {
                    out.println("[SERVER] Nu exista cartea cu cota respectiva");
                } else {
                    out.println(carte.tilu + " " + carte.autor + " " + carte.an);
                    System.out.println("[SERVER] am trimis raspuns");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Thread client = new Thread(() -> {
            try (var cli = new Socket("localhost", 8080)) {
                System.out.println("[CLIENT] m-am conectat la server");
                var in = new BufferedReader(new InputStreamReader(cli.getInputStream()));
                var out = new PrintWriter(cli.getOutputStream(), true);
                out.println("Cota_0014");
                System.out.println("[CLIENT] am trimis Cota_0014");
                String response = in.readLine();
                System.out.println("[CLIENT] am primit: " + response);
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