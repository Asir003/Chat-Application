import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;

public class Client { 
    private JFrame frame;
    private JPanel chatPanel;
    private JTextField messageField;
    private JButton sendButton;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    Box verticle=Box.createVerticalBox();

    public Client(){
        ConnectToServer();
        setupGUI();
        new ReadThread(in,chatPanel,this).start();

    }

    public String getName(){
        return name;
    }

    private void ConnectToServer() {
        try {
            Socket socket = new Socket("localhost", 12346);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        
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

        chatPanel=new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
       
        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatPanel.add(verticle);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel=new JPanel(new BorderLayout());
    
        messageField=new JTextField();
        sendButton=new JButton("Send");

        panel.add(messageField,BorderLayout.CENTER);
        panel.add(sendButton,BorderLayout.EAST);
        frame.add(panel,BorderLayout.SOUTH);
        
        //Send message by click the button
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                sendMessage();
            }      
        });

        //send message by pressing the enter 
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
               displayMessage(message, true);
                messageField.setText("");
            }
    }

    public void displayMessage(String message,boolean isUserMessage){
        JPanel messagePanel=new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));

        JLabel messageLabel = new JLabel("<html><p style='width: 200px;'>" + message + "</p></html>");
        messageLabel.setOpaque(true);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        if(isUserMessage){
            messageLabel.setBackground(Color.CYAN);
            messagePanel.add(Box.createHorizontalGlue()); 
            messagePanel.add(messageLabel);
        }
        else{
            messageLabel.setBackground(Color.LIGHT_GRAY);
            messagePanel.add(messageLabel);
            messagePanel.add(Box.createHorizontalGlue());
        }

        messagePanel.add(messageLabel);

        verticle.add(messagePanel);
        verticle.add(Box.createVerticalStrut(5));

        //chatPanel.add(messagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(()->new Client()); 
    }
}

class ReadThread extends Thread{
    private BufferedReader in;
    private JPanel chatPanel;
    private Client client;

    public ReadThread(BufferedReader in,JPanel chatPanel,Client client){
        this.in=in;
        this.chatPanel=chatPanel;
        this.client=client;
    }

    public void run(){
        try{
            String message;
            while((message = in.readLine())!=null){
                System.out.println(message);
                
                final String finalMessage=message;
                SwingUtilities.invokeLater(() -> client.displayMessage(finalMessage,false));
            }
        }
        catch(IOException e){
            System.out.println("Error client");
        }
    }
}