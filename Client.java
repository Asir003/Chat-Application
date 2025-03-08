
import java.net.*;
import java.io.*;

public class Client { 
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 12346);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Hello Server!");

            String serverMessage = in.readLine();
            System.out.println("Server: " + serverMessage);

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
