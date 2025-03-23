import java.net.*;
import java.io.*;
import java.util.*;

public class Server{
    private static List<ClientHandler> clientWriters= new ArrayList<>();
    public static void main(String[] args){
        try(ServerSocket serverSocket=new ServerSocket(12346)){
            System.out.println("Server started.Waiting for clients...");
            
            int k=1;
            while(true){
                Socket clientSocket=serverSocket.accept();
                System.out.println("Client"+k+": "+"connected.");
                k++;
                new ClientHandler(clientSocket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        static class ClientHandler extends Thread{
            private Socket socket;
            private BufferedReader in;
            private PrintWriter out;
            private String name;

            public ClientHandler(Socket socket){
                this.socket=socket;
            }

            public void run(){
                try{
                    in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out=new PrintWriter(socket.getOutputStream(), true);

                    out.println("Enter your name: ");
                    name=in.readLine();
                    System.out.println(name + " joined the chat");

                    synchronized(clientWriters){
                        clientWriters.add(this);
                        broadcast(name +" joined the chat ","Server");
                    }

                    String message;
                    while ((message=in.readLine())!=null) {
                        System.out.println(name+": "+message);
                        
                        broadcast(message,name);
                    }
                }
                catch(IOException e){
                    System.out.println("Client disconnected");
                }
                finally{
                    synchronized(clientWriters){
                        clientWriters.remove(this);
                        broadcast(name+"Left The chat","Server");
                    }
                    try{
                        socket.close();
                    }
                    catch(IOException e){
                        System.out.println("Error");
                    }
                }
            }
            private void broadcast(String message,String sender){
                synchronized(clientWriters){
                    for(ClientHandler client: clientWriters){
                        if(client != this){
                            client.out.println( sender +": "+message);
                        }
                    }
                }
            }
        }
    }

