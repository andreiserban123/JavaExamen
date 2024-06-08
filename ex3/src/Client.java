import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {
        try(Socket socket = new Socket("localhost", 8080)){
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Introduceti codul de santier");
            var codSantier = stdIn.readLine();
            out.println(codSantier);
            String line = in.readLine();
            String[] values = line.split(",");
            System.out.println("Obiectivul este " + values[0] + " iar valoarea estimata "+ Double.parseDouble(values[1]));

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
