import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Main {
    static List<Tranzactie> tranzactii = new ArrayList<>();
    static List<Produs> produse = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (BufferedReader bf = new BufferedReader(new FileReader("produse.txt"))) {
            produse = bf.lines().map(l -> new Produs(Integer.parseInt(l.split(",")[0]),
                    l.split(",")[1],
                    Double.parseDouble(l.split(",")[2])
            )).toList();
        }

        try (BufferedReader bf = new BufferedReader(new FileReader("tranzactii.json"))) {
            var tokener = new JSONTokener(bf);
            var arr = new JSONArray(tokener);
            for (int i = 0; i < arr.length(); i++) {
                var obj = arr.getJSONObject(i);
                int codProdus = obj.getInt("codProdus");
                int cantitate = obj.getInt("cantitate");
                Tip tip = obj.getEnum(Tip.class, "tip");
                tranzactii.add(new Tranzactie(codProdus, cantitate, tip));
            }
        }

        System.out.println("-------------Cerinta 1------------------");
        System.out.println("nr de prod: " + produse.size());
        System.out.println("-------------Cerinta 2------------------");
        produse.stream().sorted(Comparator.comparing(p -> p.denumire)).forEach(System.out::println);
        System.out.println("-------------Cerinta 3------------------");
        try (FileWriter writer = new FileWriter("date\\subiect1\\lista.txt")) {
            Map<String, Long> nrOri = new HashMap<>();
            produse.forEach(produs -> {
                long nr = tranzactii.stream().filter(t -> t.codProdus == produs.idProdus).count();
                nrOri.put(produs.denumire, nr);
            });
            nrOri.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).forEach(e -> {
                try {
                    writer.write(e.getKey() + ", " + e.getValue() + "\n");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            System.out.println("Fisier scris!");
        }
        System.out.println("-------------Cerinta 4------------------");
        List<Double> stocuri = new ArrayList<>();
        produse.forEach(produs -> {
            int intrari = tranzactii.stream()
                    .filter(t -> t.codProdus == produs.idProdus && t.tip.semn == Tip.intrare.semn)
                    .mapToInt(t -> t.cantitate).sum();
            int iesiri = tranzactii.stream()
                    .filter(t -> t.codProdus == produs.idProdus && t.tip.semn == Tip.iesire.semn)
                    .mapToInt(t -> t.cantitate).sum();
            int cantitate = intrari - iesiri;
            Double stoc = produs.pret * cantitate;
            stocuri.add(stoc);
        });
        Double valoareTot = stocuri.stream().reduce((double) 0, Double::sum);
        System.out.println(valoareTot);
        System.out.println("-----------------------Cerinta 5-----------------------");

        Thread server = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(8080)) {
                System.out.println("Server started on port 8080");
                var client = serverSocket.accept();
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                var out = new PrintWriter(client.getOutputStream(), true);
                int codProdus = Integer.parseInt(in.readLine());

                int intrari = tranzactii.stream().filter(t -> t.codProdus == codProdus && t.tip.semn == Tip.intrare.semn)
                        .mapToInt(t -> t.cantitate).sum();

                int iesiri = tranzactii.stream().filter(t -> t.codProdus == codProdus && t.tip.semn == Tip.iesire.semn)
                        .mapToInt(t -> t.cantitate).sum();
                double pret = produse.stream().filter(p -> p.idProdus == codProdus).mapToDouble(p -> p.pret).findFirst().getAsDouble();
                double valoare = (intrari - iesiri) * pret;
                out.println(valoare);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Thread client = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 8080)) {
                var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                var out = new PrintWriter(socket.getOutputStream(), true);
                out.println(2);
                double val = Double.parseDouble(in.readLine());
                System.out.println("Am primit val: " + val);

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