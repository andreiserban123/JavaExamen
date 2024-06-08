import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("localhost", 8080)) {
            System.out.println("[Client] M-am connectat la server");
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var out = new PrintWriter(socket.getOutputStream(), true);
            var stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Introduceti codul de santier");
            var codSantier = stdIn.readLine();
            out.println(codSantier);
            String msg = in.readLine();
            System.out.println("Am primit de la server");
            System.out.println(msg);
        }
    }
}
