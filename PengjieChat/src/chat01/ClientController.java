
package chat01;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ClientController implements Initializable{ //
	
	private String userName = null;
	private Socket clientSocket = null;
	private String strSend;
	private DataOutputStream dos = null;
	private boolean isConnect;
	private Client client;

	//	private Message message;
	
	@FXML
	private Button sendMessageButton;
	@FXML
	private TextArea messageDisplay;
	@FXML
	private Label welcomInfo;
	@FXML
	private Button startChatButton;
	@FXML
	private TextField messageText;
	@FXML
	private Pane frameWindowPane;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
//	  
//		startChatButton.setOnAction(new EventHandler<ActionEvent>() {
//			
//			@Override
//			public void handle(ActionEvent event) {
//				
//				receiveDataFromLoginPage(event); 
//				
//				
//			}
//		});
		
		frameWindowPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent event) {
				
				if(!isConnect)
				receiveDataFromLoginPage(event); 
				
				
			}
		});
		

	}
	
	
	//https://dev.to/devtony101/javafx-3-ways-of-passing-information-between-scenes-1bm8#:~:text=Get%20the%20instance%20of%20the,pass%20it%20to%20the%20stage
	private void receiveDataFromLoginPage(Event event) {
		//Step 1
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		// Step 2
		client = (Client) stage.getUserData();		
		userName = client.getUserName();
		clientSocket = client.getClientSocket();	
		// Step 3		
		isConnect = true; 
//		new Thread(new Receive()).start();
		welcomInfo.setText("Welcome! " + userName + "!");
		messageDisplay.setEditable(false);
		new Thread(new Receive()).start();


	}


	public void setSendMessageButtonAction() {
		
		strSend = messageText.getText();		
	
		if (strSend.trim().length() == 0) {
			return;
		}
		sendMessageToServer(strSend + "\n");
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
				System.out.println(userName +" said: " + utf8EncodedString);									
				// 发送给服务器端的message,相当于写过去，方法是dos.writeUTF，且服务器端必须有接收这个内容的方法
				dos.writeUTF(userName + " said: " + strSend);				
				//	dos.writeChars(strSend);	
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
					InputStream inputStream = clientSocket.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					String receivedStr = bufferedReader.readLine();
					messageDisplay.appendText(receivedStr);
				}
			} catch (SocketException e) {
				System.out.println("Server has suddently broken off");
				messageDisplay.appendText("Server has suddently broken off");
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	

}
