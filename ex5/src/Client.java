import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080)) {
            System.out.println("[Client] M-am conectat la server");
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var out = new PrintWriter(socket.getOutputStream(), true);

            // Trimitere cod secție la server
            out.println("2");
            System.out.println("[Client] am transmis mesaj");

            // Primirea răspunsului de la server
            String response = in.readLine();
            System.out.println("[Client] am primit de la server: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
