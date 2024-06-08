import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static List<Sectie> sectii = new ArrayList<>();
    static List<Pacient> pacienti = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (FileReader fisier = new FileReader("Date\\sectii.json")) {
            JSONTokener tokener = new JSONTokener(fisier);
            JSONArray arr = new JSONArray(tokener);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Sectie s = new Sectie();
                s.denumire = obj.getString("denumire");
                s.cod_sectie = obj.getInt("cod_sectie");
                s.numarLocuri = obj.getInt("numar_locuri");
                sectii.add(s);
            }
        }
//        sectii.forEach(System.out::println);
        try (BufferedReader br = new BufferedReader(new FileReader("Date\\pacienti.txt"))) {
            br.lines().forEach(line -> {

                        pacienti.add(new Pacient(Long.parseLong(line.split(",")[0]),
                                line.split(",")[1],
                                Integer.parseInt(line.split(",")[2]),
                                Integer.parseInt(line.split(",")[3])
                        ));
                    }
            );
        }
//        pacienti.forEach(System.out::println);

        System.out.println("------------------ Cerinta 1 -------------------");
        sectii.stream().filter(s -> s.numarLocuri > 10).forEach(System.out::println);
        System.out.println("------------------ Cerinta 2 -------------------");
        Map<Sectie, Double> sectiiCuMedieVarsta = sectii.stream()
                .collect(Collectors.toMap(
                        sectie -> sectie,
                        sectie -> pacienti.stream()
                                .filter(pacient -> pacient.cod_sectie == sectie.cod_sectie)
                                .mapToInt(pacient -> pacient.varsta)
                                .average()
                                .orElse(0.0)
                ));
        List<Map.Entry<Sectie, Double>> sortedSectii = sectiiCuMedieVarsta.entrySet().stream()
                .sorted(Map.Entry.<Sectie, Double>comparingByValue().reversed())
                .toList();
        sortedSectii.forEach(entry -> {
            Sectie sectie = entry.getKey();
            Double medieVarsta = entry.getValue();
            System.out.println("Cod: " + sectie.cod_sectie + ", Denumire: " + sectie.denumire + ", Numar Locuri: " + sectie.numarLocuri + ", Varsta Medie: " + medieVarsta);
        });
        System.out.println("------------------ Cerinta 3 -------------------");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Date\\jurnal.txt"))) {
            Map<Sectie, Long> sectiiCuPacieti = sectii.stream().collect(Collectors.toMap(sectie -> sectie,
                    sectie -> pacienti.stream().filter(p -> p.cod_sectie == sectie.cod_sectie).mapToInt(p -> p.varsta).count()
            ));
            sectiiCuPacieti.forEach((k, v) -> {

                try {
                    bw.write(k.cod_sectie + "," + k.denumire + "," + v + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("fisier scris");
            System.out.println("------------------ Cerinta 4 -------------------");
            try (ServerSocket server = new ServerSocket(8080)) {
                while (true){
                    System.out.println("[SERVER] serverul a pornit pe portul 8080");
                    var client = server.accept();
                    System.out.println("Clientul s-a conectat");
                    new ClientHandler(client).start();
                }
            }
        }
    }

    static class ClientHandler extends Thread {
        Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 var out = new PrintWriter(socket.getOutputStream(), true)) {

                int cod = Integer.parseInt(in.readLine());
                System.out.println("[Server] Am primit cod de la client " + cod);

                // Filtrăm secția după cod
                boolean sectieGasita = false;
                for (Sectie s : sectii) {
                    if (s.cod_sectie == cod) {
                        sectieGasita = true;
                        Long nr = pacienti.stream().filter(p -> p.cod_sectie == s.cod_sectie).count();
                        System.out.println("[Server] Transmis " + (s.numarLocuri - nr) + " catre client");
                        out.println(s.numarLocuri - nr);
                        break;
                    }
                }
                if (!sectieGasita) {
                    out.println("Sectie nu a fost gasita");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}