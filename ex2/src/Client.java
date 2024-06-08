import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (var socket = new Socket("localhost", 8080);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var out = new PrintWriter(socket.getOutputStream(), true)) {

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Introduceti codul produsului: ");
            String codProdus = stdIn.readLine();
            out.println(codProdus);

            String response = in.readLine();
            System.out.println("Cantitatea produsa: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
