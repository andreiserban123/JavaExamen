import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Main {
    static List<Produs> produse;
    static List<Tranzactie> tranzactii = new ArrayList<>();

    public static void main(String[] args) {
        try (var fisier = new BufferedReader(new FileReader("Date\\Produse.txt"))) {
            produse = fisier.lines().map(linie -> new Produs(
                    Integer.parseInt(linie.split(",")[0]),
                    linie.split(",")[1],
                    Double.parseDouble(linie.split(",")[2])
            )).toList();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (var fisier = new FileReader("Date\\Tranzactii.json")) {
            var tokener = new JSONTokener(fisier);
            var jsonTranzactii = new JSONArray(tokener);
            for (int i = 0; i < jsonTranzactii.length(); i++) {
                var jsonTranzactie = jsonTranzactii.getJSONObject(i);
                int cod = jsonTranzactie.getInt("codProdus");
                int cantitate = jsonTranzactie.getInt("cantitate");
                Tip tip = jsonTranzactie.getEnum(Tip.class, "tip");
                tranzactii.add(new Tranzactie(cod, cantitate, tip));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("----------Cerinta 1-------------------");
        System.out.println("NR de produse: " + produse.size());
        System.out.println("---------Cerinta 2-----------------------");
        produse.stream().sorted(Comparator.comparing(Produs::getDenumire)).forEach(System.out::println);
        System.out.println("------------------CERINTA 3------------------");
        Map<Integer, String> temp = new HashMap<>();
        produse.forEach(produs -> {
            int nrTrazactii = tranzactii.stream().filter((tranzactie -> tranzactie.getCodProdus() == produs.getCodProdus())).toList().size();
            temp.put(nrTrazactii, produs.getDenumire());
        });

        List<Map.Entry<Integer, String>> listaHash = new ArrayList<>(temp.entrySet());
        Collections.sort(listaHash, Map.Entry.comparingByKey());
        Collections.reverse(listaHash);

        try (var writer = new BufferedWriter(new FileWriter("lista.txt"))) {
            listaHash.forEach(e -> {
                try {
                    writer.write("Denumire " + e.getValue() + " Nr trazactii " + e.getKey() + "\n");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Fisierul a fost scris!");

        System.out.println("-------------Cerinta 4---------------------");
        List<Double> stocuri = new ArrayList<>();

        produse.forEach(produs -> {
            int intrari = tranzactii.stream()
                    .filter(tranzactie -> tranzactie.getCodProdus() == produs.getCodProdus())
                    .filter(tranzactie -> tranzactie.getTip().getSemn() == Tip.intrare.getSemn())
                    .mapToInt(Tranzactie::getCantitate).sum();

            int iesiri = tranzactii.stream()
                    .filter(tranzactie -> tranzactie.getCodProdus() == produs.getCodProdus())
                    .filter(tranzactie -> tranzactie.getTip().getSemn() == Tip.iesire.getSemn())
                    .mapToInt(Tranzactie::getCantitate).sum();
            int cant = intrari - iesiri;
            Double stoc = cant * produs.getPret();
            stocuri.add(stoc);
        });

        Double sum = stocuri.stream().mapToDouble(Double::doubleValue).sum();
        System.out.println("Stocuri: " + sum);

        System.out.println("------------------------- Cerinta 5 --------------------");
        Thread server = new Thread(new Runnable() {
            @Override
            public void run() {
                try (ServerSocket server = new ServerSocket(8080)) {
                    System.out.println("[SERVER] Se asteapta conexiunea cu clietul...");
                    try (Socket client = server.accept()) {
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                        System.out.println("[SERVER] S-a realizat conexiunea cu clientul...");
                        int cod = Integer.parseInt((in.readLine()));
                        System.out.println("[SERVER] S-a primit codul " + cod);

                        var valoare = produse.stream().filter(produs -> produs.getCodProdus() == cod)
                                .mapToDouble(Produs::getPret).sum();
                        out.println(valoare);
                        System.out.println("[SERVER] Am trimis urmatoarea valoare clientului: " + valoare);
                        System.out.println("[SERVER] Am inchis conexiunea");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread client = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Socket client = new Socket("localhost", 8080)) {
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    System.out.println("[CLIENT] S-a realizat conexiunea cu server-ul...");
                    int cod = 3;
                    out.println(cod);
                    System.out.println("[CLIENT] S-a trimis codul produsului");

                    var valoare = Float.parseFloat(in.readLine());
                    System.out.println("[CLIENT] Am primit valoare de la server: " + valoare);
                    System.out.println("[Client] Am inchis conexiunea!");

                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        server.start();
        client.start();
        try {
            server.join();
            client.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Main ended!");
    }
}