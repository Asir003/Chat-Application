
import java.net.*;
import java.io.*;

public class Client { 
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12346);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput=new BufferedReader(new InputStreamReader(System.in));

            new ReadThread(in).start();

            String message;
            while(true){
            System.out.println("You: ");
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
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ReadThread extends Thread{
    private BufferedReader in;

    public ReadThread(BufferedReader in){
        this.in=in;
    }

    public void run(){
        try{
            String message;
            while((message = in.readLine())!=null){
                System.out.println("             "+ message);
                System.out.println("You ");
            }
        }
        catch(IOException e){
            System.out.println("Error client");
        }
    }
}
