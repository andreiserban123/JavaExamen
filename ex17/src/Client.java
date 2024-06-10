import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        try (Socket con = new Socket("localhost", 8080)) {
            var in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            var out = new PrintWriter(con.getOutputStream(), true);

            var stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Introdu un id de produs:");
            var id = stdIn.readLine();
            out.println(id);
            System.out.println("Am transmis id " + id);
            var msg = in.readLine();
            System.out.println("Am primit:");
            System.out.println(msg);
        }
    }
}
