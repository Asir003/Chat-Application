import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Client { 
    private JFrame frame;
    private JPanel chatPanel;
    private JTextField messageField;
    private JButton sendButton;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    Box verticle=Box.createVerticalBox();
    private JScrollPane scrollPane;
    private JButton usersButton;
    private JPanel onlineUsersPanel;
    private JTextArea onlineUsersTextArea;
    private boolean isOnlinePanelVisible = false;

    public Client(){
        ConnectToServer();
        setupGUI();
        initializeUI();
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
       
         scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel=new JPanel(new BorderLayout());
        messageField=new JTextField();
        sendButton=new JButton("Send");
        usersButton=new JButton("Online Users");

        panel.add(usersButton,BorderLayout.WEST);
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

        usersButton.addActionListener(e -> {
            requestOnlineUsers();
            showOnlinePanel(); 
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

    private void requestOnlineUsers(){
        out.println("@users");
    }

    public void initializeUI() { 
        
        onlineUsersPanel = new JPanel(new BorderLayout());
        onlineUsersTextArea = new JTextArea(10, 20);
        onlineUsersTextArea.setEditable(false);
        onlineUsersTextArea.setBorder(BorderFactory.createTitledBorder("Online Users"));
    
        JButton closeButton = new JButton("X");
        closeButton.addActionListener(e -> hideOnlinePanel());
        
        onlineUsersPanel.add(new JScrollPane(onlineUsersTextArea), BorderLayout.CENTER);
        onlineUsersPanel.add(closeButton, BorderLayout.NORTH);
        
       
        frame.add(onlineUsersPanel, BorderLayout.EAST); 
        onlineUsersPanel.setVisible(false);
        
        frame.revalidate();
        frame.repaint();
    }

    private void showOnlinePanel() {
        isOnlinePanelVisible = true;
        onlineUsersPanel.setVisible(true);
        frame.revalidate();
        frame.repaint();
    }

    private void hideOnlinePanel() {
        isOnlinePanelVisible = false;
        onlineUsersPanel.setVisible(false);
        frame.revalidate();
        frame.repaint();
    }
    
   

    public void displayMessage(String message,boolean isUserMessage){
        if (message.startsWith("[") && message.endsWith("]") && !isUserMessage) {
            String userList = message.replaceAll("[\\[\\]]", "").replace(",", "\n");
            onlineUsersTextArea.setText("Online Users:\n" + userList); 
            return; 
        }

        JPanel messagePanel=new JPanel(new BorderLayout());
        
        JLabel messageLabel=new JLabel("<html><p style='width: 200px;'>" + message + "</p></html>");
        messageLabel.setOpaque(true);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");  
        String currentTime = sdf.format(new Date());  
        JLabel timeLabel = new JLabel(currentTime);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));  
        timeLabel.setForeground(Color.GRAY); 
    
        if(isUserMessage){
            messageLabel.setBackground(Color.CYAN);
            messageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            messagePanel.add(messageLabel, BorderLayout.EAST);
            messagePanel.add(timeLabel, BorderLayout.SOUTH);
        } else {
            messageLabel.setBackground(Color.LIGHT_GRAY);
            messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
            timeLabel.setHorizontalAlignment(SwingConstants.LEFT);
            messagePanel.add(messageLabel, BorderLayout.WEST);
            messagePanel.add(timeLabel, BorderLayout.SOUTH); 
        }
    
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,messageLabel.getPreferredSize().height +timeLabel.getPreferredSize().height+ 10));
        chatPanel.add(messagePanel);
        chatPanel.add(Box.createVerticalStrut(5));
    
        chatPanel.revalidate();
        chatPanel.repaint();
    
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });

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