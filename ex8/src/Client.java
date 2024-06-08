import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try(Socket socket = new Socket("localhost", 8080)) {
            System.out.println("M-am conectat la server");
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var out = new PrintWriter(socket.getOutputStream(), true);
            String cota = "Cota_0012";
            out.println(cota);
            String response = in.readLine();
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
