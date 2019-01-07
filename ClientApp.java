package clientservercommunication.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientApp {

	private JFrame appFrame;
	private JTextArea incomingMessageArea;
	private JTextField outgoingMessageField;
	private JButton sendMessage; 
	private Font font = new Font("Consolas",Font.BOLD,14);
	private DataInputStream inputMessages;
	private DataOutputStream outputMessages;
	private Socket connection;
	private int portNumber = 3344;
	private String  host = ""//IP Adress goes here;
	
	
	public ClientApp(){
		setLayout();
		makeConnection();
	}
	/**
	 * Make a connection between a server and this client. If the connection was successful, start the communication
	 */
	public void makeConnection() {
		try {
			//connect to the socket that server created
			this.connection = new Socket(this.host,this.portNumber);
			System.out.println("Connected Successfully");
			
			//set up the IO streams
			this.inputMessages = new DataInputStream(connection.getInputStream());
			this.outputMessages = new DataOutputStream(connection.getOutputStream());
			
			//start the communication
			String receivedMessage = "";
			while(!receivedMessage.equals("EXIT")) {
				receivedMessage = this.inputMessages.readUTF();
				this.incomingMessageArea.setText(incomingMessageArea.getText().trim() +"\nServer: " + receivedMessage);
			}
			this.incomingMessageArea.setText("\n" + "Server closed the connection.." );

		} catch (IOException e) {
			this.incomingMessageArea.setText("Unsuccessful connecting to the server..");
			e.printStackTrace();
		}
	}
	/**
	 * Sets up the entire layout of the application
	 */
	public void setLayout() {
		//setting up the frame of the application
		this.appFrame = new JFrame("Client");
		this.appFrame.setSize(new Dimension(400,300));
		this.appFrame.setLocationRelativeTo(null);
		this.appFrame.setResizable(false);
		this.appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.appFrame.setLayout(new BorderLayout() );
		
		//setting up the input message area
		this.incomingMessageArea = new JTextArea();
		this.incomingMessageArea.setFont(font);
		this.incomingMessageArea.setWrapStyleWord(true);
		this.incomingMessageArea.setForeground(Color.WHITE);
		this.incomingMessageArea.setBackground(Color.BLACK);
		this.incomingMessageArea.setEditable(false);
		this.appFrame.add(incomingMessageArea);
		
		//setting up the output message area
		this.outgoingMessageField = new JTextField();
		this.outgoingMessageField.setFont(font);
		this.appFrame.add(outgoingMessageField,BorderLayout.SOUTH);
		
		//setting up the send button
		this.sendMessage = new JButton("Send");
		this.sendMessage.addActionListener(new SendButtonListener());
		this.appFrame.add(sendMessage,BorderLayout.LINE_END);
		
		
		
		
		this.appFrame.setVisible(true);
	}
	/**
	 * Class that handles events with regards to sending the message using a button 
	 * 
	 *
	 */
	class SendButtonListener implements ActionListener{

		
		@Override
		public void actionPerformed(ActionEvent e) {
			//Grab the message that was typed 
			String outputMessage = outgoingMessageField.getText();
			//Write the message to the server and to itself
			try {
				outputMessages.writeUTF(outputMessage);
				outputMessages.flush();
				incomingMessageArea.setText(incomingMessageArea.getText().trim() + "\nClient: " + outputMessage);
				outgoingMessageField.setText("");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}
	public static void main(String[] args) {
		new ClientApp();
	}
}
