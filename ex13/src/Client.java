import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("localhost", 8080)) {
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var stdin = new BufferedReader(new InputStreamReader(System.in));
            var out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Introdu denumirea programului de studiu:");
            String mesaj = stdin.readLine();
            out.println(mesaj);

            String response = in.readLine();
            System.out.println("Lista candidaților înscriși: " + response);
        }
    }
}
