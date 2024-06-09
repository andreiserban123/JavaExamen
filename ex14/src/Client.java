import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws  Exception{
        try(var socket = new Socket("localhost", 8080)){
            var in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var out = new PrintWriter(socket.getOutputStream(), true);

            var inStd = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Scrie codul departamentului: ");
            int cod = Integer.parseInt(inStd.readLine());
            System.out.println("Trimit " + cod);
            out.println(cod);

            String msg = in.readLine();
            System.out.println("Am primit: " + msg);

        }
    }
}
