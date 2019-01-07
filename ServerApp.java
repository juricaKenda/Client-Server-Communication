package clientservercommunication.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class ServerApp {
	private JFrame appFrame;
	private JTextArea incomingMessageArea;
	private JTextField outgoingMessageField;
	private JButton sendMessage; 
	private Font font = new Font("Consolas",Font.BOLD,14);
	private DataInputStream inputMessages;
	private DataOutputStream outputMessages;
	private ServerSocket serverSocket;
	private Socket connection;
	private int portNumber = 3344;

	
	
	public ServerApp(){
		setLayout();
		setServer();
	}
	/**
	 * Set up the server and wait for the connection of a client. When a connection is established, start the
	 * communication.
	 */
	public void setServer() {
		try {
			//create a new server socket on a specified port
			this.serverSocket = new ServerSocket(this.portNumber);
			//wait for a client to connect
			this.connection = this.serverSocket.accept();
			
			//set up IO streams
			this.inputMessages = new DataInputStream(this.connection.getInputStream());
			this.outputMessages = new DataOutputStream(this.connection.getOutputStream());
			
			//start the communication
			String receivedMessage = "";
			while(!(receivedMessage.equals("EXIT"))) {
				receivedMessage = this.inputMessages.readUTF();
				this.incomingMessageArea.setText(incomingMessageArea.getText().trim() + "\nClient: " + receivedMessage);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			closeStreams();
		}
	}
	
	/**
	 * Function that closes all used streams
	 */
	public void closeStreams() {
		
		try {
			this.outputMessages.close();
			this.inputMessages.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sets up the entire layout of the application
	 */
	public void setLayout() {
		//setting up the frame of the application
		this.appFrame = new JFrame("Server");
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
			//Write the message to the client and to itself
			try {
				outputMessages.writeUTF(outputMessage);
				outputMessages.flush();
				incomingMessageArea.setText(incomingMessageArea.getText().trim() + "\nServer: " + outputMessage);
				outgoingMessageField.setText("");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}
	public static void main(String[] args) {
		new ServerApp();
	}
}
