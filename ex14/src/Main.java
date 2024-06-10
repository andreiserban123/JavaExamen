import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static List<Departament> departaments = new ArrayList<>();
    static List<Angatat> angajati = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try (var conn = DriverManager.getConnection("jdbc:sqlite:companie.db");
             var stm = conn.createStatement();
             var resultSet = stm.executeQuery("SELECT * FROM DEPARTAMENTE")
        ) {
            while (resultSet.next()) {
                int cod = resultSet.getInt(1);
                String numire = resultSet.getString(2);
                double buget = resultSet.getDouble(3);
                String manager = resultSet.getString(4);
                departaments.add(new Departament(cod, numire, buget, manager));
            }
//           departaments.forEach(System.out::println);
        }

        try (BufferedReader bf = new BufferedReader(new FileReader("angajati.txt"))) {
            angajati = bf.lines().map(l -> new Angatat(Integer.parseInt(l.split(",")[0]),
                    Integer.parseInt(l.split(",")[1]),
                    l.split(",")[2],
                    Double.parseDouble(l.split(",")[3])
            )).toList();
            angajati.forEach(System.out::println);
        }
        // Să se calculeze bugetul total al companiei (suma bugetelor tuturor departamentelor) și să se afișeze la consolă.

        System.out.println("------------------Cerinta 1---------------------");

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        System.out.println("Buget total " + decimalFormat.format(departaments.stream().mapToDouble(d -> d.buget).sum()));
        // Să se afișeze la consolă numărul total de angajați pentru fiecare departament.
        System.out.println("------------------Cerinta 2---------------------");

        departaments.forEach(departament -> {
            long count = angajati.stream().filter(angatat -> angatat.codDepartament == departament.codDepartament).count();
            System.out.println("Dep de " + departament.numire + " are " + count + " de angajati");
        });

        // Să se calculeze media salariilor tuturor angajaților și să se afișeze la consolă angajații care au salariul
        // mai mare decât această medie.

        System.out.println("------------------Cerinta 3---------------------");

        double medie = angajati.stream().mapToDouble(a -> a.salariul).average().getAsDouble();
        System.out.println("Media: " + medie);
        angajati.stream().filter(a -> a.salariul > medie).forEach(System.out::println);

        // Să se scrie într-un fișier departamente.json lista departamentelor și numărul de angajați din fiecare departament în format JSON.

        System.out.println("------------------Cerinta 4---------------------");

        try (FileWriter fw = new FileWriter("departamente.json")) {
            Map<Departament, Long> depNr = new HashMap<>();
            departaments.forEach(departament -> {
                long count = angajati.stream().filter(angatat -> angatat.codDepartament == departament.codDepartament).count();
                depNr.put(departament, count);
            });
            var arr = new JSONArray();
            for (var entry : depNr.entrySet()) {
                var obj = new JSONObject();
                obj.put("idDepartament", entry.getKey().codDepartament);
                obj.put("denumire", entry.getKey().numire);
                obj.put("nrAngj", entry.getValue());
                arr.put(obj);
            }
            arr.write(fw, 0, 2);
            System.out.println("fisier scris");
        }
        // Să se implementeze un server și un client TCP/IP.
        // Clientul trimite serverului codul unui departament.
        // Serverul răspunde cu denumirea departamentului și bugetul acestuia.
        // Serverul poate servi oricâte cereri.
        System.out.println("------------------Cerinta 5---------------------");

        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("SERVER STARTED ON PORT 8080");
            while(true){
                System.out.println("S-a conectat un client");
                var client = server.accept();
                new ClientHandler(client).start();
            }
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
                var in  = new BufferedReader(new InputStreamReader(client.getInputStream()));
                var out = new PrintWriter(client.getOutputStream(), true);

                int codDep = Integer.parseInt(in.readLine());

               Departament d =  departaments.stream().filter(dep->dep.codDepartament == codDep).findFirst().orElse(null);

                if (d != null) {
                    out.println(d.numire+","+d.buget);
                } else {
                    out.println("Nu exista departamentul cu codul " + codDep);
                }
                System.out.println("Am servit clientul");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}