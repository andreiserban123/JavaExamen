import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        try (var client = new Socket("localhost", 8080)) {
            var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            var out = new PrintWriter(client.getOutputStream(), true);
            var stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Introdu un cod de santier");
            out.println(stdIn.readLine());
            String response = in.readLine();
            System.out.println("Am primit " + response);
        }
    }
}
