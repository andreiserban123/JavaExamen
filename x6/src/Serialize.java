import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Serialize {
    public static void main(String[] args) throws Exception {
        List<Angajat> angajati = new ArrayList<>();
        angajati.add(new Angajat(1, "John Doe", "Developer", 50000));
        angajati.add(new Angajat(2, "Jane Smith", "Manager", 60000));

        try (
             ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("test.dat"))) {
         oos.writeObject(angajati);
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("test.dat"))){
            angajati = (List<Angajat>) ois.readObject();
            angajati.forEach(System.out::println);
        }
    }
}
