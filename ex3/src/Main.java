import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static List<Santier> santiere = new ArrayList<>();
    static List<Capitol> capitole = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (FileReader fisier = new FileReader("santiere.json")) {
            JSONTokener tokener = new JSONTokener(fisier);
            JSONArray jsonArray = new JSONArray(tokener);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject santier = jsonArray.getJSONObject(i);
                int cod = santier.getInt("Cod Santier");
                String localitate = santier.getString("Localitate");
                float valoare = santier.getFloat("Valoare");
                String obiectiv = santier.getString("Obiectiv");
                String strada = santier.getString("Strada");
                Santier santier1 = new Santier(cod, localitate, strada, obiectiv, valoare);
                santiere.add(santier1);
            }
        }
        try {
            String url = "jdbc:sqlite:devize.db";
            var connection = DriverManager.getConnection(url);
            Statement stm = connection.createStatement();
            var resultSet = stm.executeQuery("select * from CAPITOLE");
            while (resultSet.next()) {
                Capitol c = new Capitol();
                c.codCapitol = resultSet.getInt(1);
                c.codSantier = resultSet.getInt(2);
                c.denumireCheltuiala = resultSet.getString(3);
                c.um = resultSet.getString(4);
                c.cantitate = resultSet.getFloat(5);
                c.pu = resultSet.getFloat(6);
                capitole.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("----------------CERINTA 1--------------------");
        santiere.forEach(System.out::println);
        System.out.println("Valorea medie: " + santiere.stream().mapToDouble(el -> el.valoare).average().getAsDouble());

        System.out.println("----------------CERINTA 2---------------------");
        Map<Integer, Double> cerinta2 = capitole.stream().collect(Collectors.groupingBy(
                c -> c.codCapitol,
                Collectors.summingDouble(c -> c.cantitate)
        ));
        cerinta2.forEach((k, v) -> {
            System.out.println("Capitolul: " + k + " Cantitatea: " + v);
        });
        System.out.println("----------------CERINTA 3---------------------");
        try (FileWriter writer = new FileWriter("devize.txt")) {
            capitole.stream().distinct().forEach(capitol -> {
                try {
                    writer.write(capitol.codCapitol + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                capitole.stream().filter(c -> c.codCapitol == capitol.codCapitol).forEach(capitol1 -> {
                            try {
                                writer.write(capitol1.codSantier + " " + capitol1.pu * capitol1.cantitate + "\n");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
            });
            System.out.println("Fisier scris!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("----------------CERINTA 4---------------------");
        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("[Server] Am pornit pe portul 8080");
            while (true) {
                var socket = server.accept();
                System.out.println("S-a conectat un client");
                new ClientThread(socket).start();
            }
        }
    }

    static class ClientThread extends Thread {

        private final Socket socket;

        public ClientThread(Socket client) {
            this.socket = client;
        }

        @Override
        public void run() {
            try {
                var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                var out = new PrintWriter(socket.getOutputStream(), true);

                int codSantier = Integer.parseInt(in.readLine());
                for(var santier :santiere){
                    if(santier.codSantier == codSantier){
                        out.println(santier.obiectiv + "," + santier.valoare);
                        System.out.println("S-au trimis catre client datele!");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}