
package chat01;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClientController{ //implements Initializable
	
	private String userName = null;
	private Socket clientSocket = null;
	private String strSend;
	private DataOutputStream dos = null;
	private boolean isConnect;
	private Client client;
	
	private TextField messageText;
	@FXML
	private Button sendMessageButton;
	@FXML
	private TextArea messageDisplay;
	@FXML
	private Label welcomInfo;
	
	//Conncetion connection;

//	@Override
//	public void initialize(URL arg0, ResourceBundle arg1) {
//		isConnect = true;
//		welcomInfo.setText("Welcome!");
//		try {
//			clientSocket = new Socket("localhost", 2323);
//		} catch (IOException e) {
//		
//			e.printStackTrace();
//		}
//	}
	
//	public void setConnection(Conncetion newConncection) {
//		
//		connection = newConncection;
//		
//	}
	
//	private void receiveData(ActionEvent event) {
//		  // Step 1
//		  Node node = (Node) event.getSource();
//		  Stage stage = (Stage) node.getScene().getWindow();
//		  // Step 2
//		  Client client = (Client) stage.getUserData();
//		  // Step 3
//		  userName = client.getUserName();
//		  clientSocket = client.getClientSocket();
//		  
//	}


	public void setSendMessageButtonAction(ActionEvent event) {

		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		// Step 2
		client = (Client) stage.getUserData();
		
		System.out.println("get or not?" + client.getUserName());
		
		// Step 3
		userName = client.getUserName();
		clientSocket = client.getClientSocket();

		//welcomInfo.setText("");
		System.out.println("welcome: " + userName);
		System.out.println("password: "+ client.getPassword());
		strSend = messageText.getText();
		if (strSend.trim().length() == 0) {
			return;
		}
		sendMessageToServer("Sarah," + strSend + "\n");
		messageText.setText("");
		messageDisplay.appendText(strSend + "\n");

	}

	// send message to server
	public void sendMessageToServer(String strSend) {
		
		if (isConnect) {
			
			try {
				//System.out.println("sara1: " + strSend);
				dos = new DataOutputStream(clientSocket.getOutputStream());
						
				String utf8EncodedString = new String(strSend.getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);

				System.out.println(userName +"said: " + utf8EncodedString);				
				dos.writeChars(strSend);			
				// 发送给服务器端的message,相当于写过去，方法是dos.writeUTF，且服务器端必须有接收这个内容的方法
				dos.writeUTF(strSend);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			
			System.out.println("not connect server!");
		}

	}

	class Receive implements Runnable {

		@Override
		public void run() {
			try {
				while (isConnect) {
					DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
					String receivedStr = dis.readUTF();
					messageDisplay.appendText(receivedStr);
				}
			} catch (SocketException e) {
				System.out.println("Server has suddently broken off");
				messageDisplay.appendText("Server has suddently broken off");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}





}
