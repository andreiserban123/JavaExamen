import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static List<Carte> carti = new ArrayList<>();
    static List<Cititor> cititori = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader("carti.txt"))) {
            carti = br.lines().map(l -> new Carte(l.split("\t")[0], l.split("\t")[1], l.split("\t")[2], Integer.parseInt(l.split("\t")[3]))).toList();
        }

        try (var conn = DriverManager.getConnection("jdbc:sqlite:biblioteca.db"); var stm = conn.createStatement(); var resultSet = stm.executeQuery("SELECT * FROM Imprumuturi");) {
            while (resultSet.next()) {
                cititori.add(new Cititor(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3)));
            }
        }

        System.out.println("-------------------CERINTA 1---------------------------");
        carti.stream().filter(c -> c.an < 1940).forEach(c -> System.out.println(c.cotaCarte + "\t" + c.titlu + "\t" + c.autor + "\t" + c.an));
        System.out.println("-------------------CERINTA 2---------------------------");
        cititori.stream().distinct().forEach(cititor -> {
            long count = carti.stream().filter(carte -> carte.cotaCarte.equals(cititor.cotaCarte)).map(c -> c.cotaCarte).count();
            if (count > 0) System.out.println(cititor.nume);
        });
        System.out.println("-------------------CERINTA 3---------------------------");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("test.txt"))) {
            bw.write("Nume student\tTotal zile imprumut");
            bw.newLine();
            Map<String, Integer> studZile = cititori.stream().collect(Collectors.groupingBy(c -> c.nume, Collectors.summingInt(c -> c.nrZile)));
            List<Map.Entry<String, Integer>> studZileOrdonate = studZile.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).toList();
            for (var entry : studZileOrdonate) {
                bw.write(entry.getKey() + "\t\t" + entry.getValue());
                bw.newLine();
            }
            System.out.println("Fisierul a fost scris!");
        }

        System.out.println("-------------------CERINTA 4---------------------------");
        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("Server started on port 8080");
            Socket client = server.accept();
            new ClientHandler(client).start();
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
                var out =  new PrintWriter(client.getOutputStream(), true);
                String msg = in.readLine();
                System.out.println(msg);
                carti.stream().filter(c->c.cotaCarte.equals(msg)).forEach(c->
                        out.println(c.titlu+","+c.autor+"," +c.an)
                );

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }


}
