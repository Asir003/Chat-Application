import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;

public class Client { 
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private PrintWriter out;
    private BufferedReader in;
    private String name;

    public Client(){
        ConnectToServer();
        setupGUI();
        new ReadThread(in,chatArea).start();

    }

    private void ConnectToServer() {
        try {
            Socket socket = new Socket("localhost", 12346);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        
            //First Start to enter name
            name=JOptionPane.showInputDialog(in.readLine()+" ");
            out.println(name);
            
            //socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupGUI(){

        frame=new JFrame(name);
        frame.setSize(400,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatArea=new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea),BorderLayout.CENTER);

        JPanel panel=new JPanel(new BorderLayout());
        messageField=new JTextField();
        sendButton=new JButton("Send");

        panel.add(messageField,BorderLayout.CENTER);
        panel.add(sendButton,BorderLayout.EAST);
        frame.add(panel,BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                sendMessage();
            }      
        });

        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                sendMessage();
            }
        });

        frame.setVisible(true);
    }
    

    private void sendMessage(){
            String message=messageField.getText().trim();
            if(!message.isEmpty()){
               out.println(message);
                messageField.setText("");
            }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(()->new Client()); 
    }
}

class ReadThread extends Thread{
    private BufferedReader in;
    private JTextArea chatArea;

    public ReadThread(BufferedReader in,JTextArea chatArea){
        this.in=in;
        this.chatArea=chatArea;
    }

    public void run(){
        try{
            String message;
            while((message = in.readLine())!=null){
                System.out.println(message);
                chatArea.append(message+"\n");
                //System.out.println("You ");
            }
        }
        catch(IOException e){
            System.out.println("Error client");
        }
    }
}