
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
                out.println("Exit From the chat");
                String serverResponse=in.readLine();
                if(serverResponse != null)
                System.out.println(serverResponse);    
                break;
            }
            out.println(message);
            }

            out.close();
            in.close();
            userInput.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
