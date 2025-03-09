
import java.net.*;
import java.io.*;

public class Client { 
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12346);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput=new BufferedReader(new InputStreamReader(System.in));

            String message;
            while(true){
            System.out.println("Enter message: ");
            message=userInput.readLine();
            if(message.equalsIgnoreCase("Exit")){
                System.out.println("Exit From the chat");
                break;
            }
            out.println(message);
            String serverResponse=in.readLine();
            }
            
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
