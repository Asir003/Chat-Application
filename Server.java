import java.net.*;
import java.io.*;

public class Server{
    public static void main(String[] args){
        try{
            ServerSocket serverSocket=new ServerSocket(12346);
            System.out.println("Server started.Waiting for clients...");

            Socket clientSocket=serverSocket.accept();
            System.out.println("Client connected.");

            BufferedReader in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out=new PrintWriter(clientSocket.getOutputStream(), true);

            String message;
            while ((message=in.readLine())!=null) {
                System.out.println("Client: "+message);
                out.println("Server received: "+message);
            }

            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
