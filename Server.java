import java.net.*;
import java.io.*;
import java.util.*;

public class Server{
    private static List<PrintWriter> clientWriters= new ArrayList<>();
    public static void main(String[] args){
        try(ServerSocket serverSocket=new ServerSocket(12346)){
            System.out.println("Server started.Waiting for clients...");

            while(true){
                Socket clientSocket=serverSocket.accept();
                System.out.println("Client connected.");

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

            public ClientHandler(Socket socket){
                this.socket=socket;
            }

            public void run(){
                try{
                    in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out=new PrintWriter(socket.getOutputStream(), true);

                    synchronized(clientWriters){
                        clientWriters.add(out);
                    }

                    String message;
                    while ((message=in.readLine())!=null) {
                        System.out.println("Client: "+message);
                        
                        synchronized(clientWriters){
                            for (int i = 0; i < clientWriters.size(); i++) {
                                clientWriters.get(i).println(message); 
                            }
                        }
                    }
                }
                catch(IOException e){
                    System.out.println("Client disconnected");
                }
                finally{
                    synchronized(clientWriters){
                        clientWriters.remove(out);
                    }
                    try{
                        socket.close();
                    }
                catch(IOException e){
                    System.out.println("Error");
                }
                }
            }
        }
    }

