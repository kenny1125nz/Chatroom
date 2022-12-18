
package chat01;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
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

	@FXML
	private ListView clients;

	private ObservableList<String> clientsList;

	private String receiver;

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


//		clients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
//			@Override
//			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//				receiver = newValue;
//				System.out.println("selected :" + receiver);
//			}
//		});

		clientsList = clients.getItems();

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


		new Thread(() -> sendMessageToServer(strSend + "\n")).start();

		messageText.setText("");
		messageDisplay.appendText(strSend + "\n");

	}

	// send message to server
	public void sendMessageToServer(String strSend) {
		
		if (isConnect) {			
			try {
				//System.out.println("sara1: " + strSend);
				//dos = new DataOutputStream(clientSocket.getOutputStream());
				String utf8EncodedString = new String(strSend.getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);
				System.out.println(userName +" said: " + utf8EncodedString);									
				// 发送给服务器端的message,相当于写过去，方法是dos.writeUTF，且服务器端必须有接收这个内容的方法

				Object selected = clients.getSelectionModel().getSelectedItem();
				if(selected==null)
					receiver = "ALL";
				else
					receiver = selected.toString();

				OutputStream os = clientSocket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				PrintWriter pw = new PrintWriter(osw);
				pw.println("M:" + receiver + "_" + userName + " said: " + strSend);
				pw.flush();
				//dos.writeUTF();
				//	dos.writeChars(strSend);	
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {		
			System.out.println("not connect server!");
		}

	}


	private void updateClientsMenu(String[] clients) {

		Platform.runLater(() -> {
			clientsList.clear();
			clientsList.add("ALL");
			for (String client : clients) {
				if (client != null && client.trim().length() != 0) {
					clientsList.add(client);
				}
			}
		});

	}
	
	class Receive implements Runnable {
		
	    
		@Override
		public void run() {
			
			try {
				while (isConnect) {
					InputStream inputStream = clientSocket.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					String receivedStr = bufferedReader.readLine();
					System.out.println("received from server:" + receivedStr);
//					if(receivedStr.length()<2){
//						continue;
//					}
//					receivedStr = receivedStr.substring(2);

					char messageType =receivedStr.charAt(0);
					String msg = receivedStr.substring(2);
					if (messageType == 'M') {
						messageDisplay.appendText(msg + "\r\n");
					} else if (messageType == 'C') {
						String[] clients = msg.split(",");
						updateClientsMenu(clients);
					}


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
