import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        try(Socket socket = new Socket("localhost", 8080)){
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var out = new PrintWriter(socket.getOutputStream(),true);
            var stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("introdu un nume:");
            String nume = stdIn.readLine();
            System.out.println("Am trimis:" +nume);
            out.println(nume);
            String response = in.readLine();
            System.out.println("Am primit: " + response);
        }
    }
}
