
package previousClass;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientChatUI extends Application{
	
	private TextField portInput;
	private TextField ipInput;
	private Button connectButton;
	
	private TextField messageText;
	private Button sendMessageButton;

	private Socket clientSocket = null;
//	private static final String CONNECTION_IP = "127.0.0.1";//
//	private static final int CONNECT_PORT = 2333;//
	String strSend;
	private DataOutputStream dos = null;
	TextArea messageDisplay;
	private boolean isConnect = false;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		portInput = new TextField();
		Label portLabel = new Label();
		portLabel.setText("Enter Server Port:");	
		
		ipInput = new TextField();
		Label ipLabel = new Label();
		ipLabel.setText("Enter Ip Adress:");
		
		connectButton = new Button("Connect");
		
		//Message Input
		messageText = new TextField();
		messageText.setPromptText("Input message to send");
		
		//Send message button
		sendMessageButton = new Button("Send");

		messageDisplay = new TextArea();
		messageDisplay.setEditable(false);

		messageDisplay.setPrefSize(200, 200);
		VBox bigBox = new VBox(10);
		bigBox.getChildren().addAll(portInput, portLabel, ipInput, ipLabel, connectButton, messageDisplay, messageText, sendMessageButton);
		
		//connect button 监听
		connectButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				connectToServer();				
			}	
		});
			
		// scene set up
		primaryStage.setScene(new Scene(bigBox, 400, 400));
		primaryStage.sizeToScene();
//		primaryStage.getIcons().add(imageAvo);
		primaryStage.setTitle("Clientside");
		primaryStage.show();
	
		setSendMessageButtonAction();
//		new Thread(new Receive()).start();
		
	}
	
	public void connectToServer() {
		
		int port = Integer.parseInt(portInput.getText());
		String ip = ipInput.getText();
		
		try {
//			clientSocket = new Socket(CONNECTION_IP, CONNECT_PORT);
			clientSocket = new Socket(ip, port);
			isConnect = true;
//			Client client = new Client();

		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		new Thread(new Receive()).start();
//		Client client = new Client(port, ip, this);
//		new Thread(client).start();
	}
	
	public void setSendMessageButtonAction() {
		
		sendMessageButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				
				strSend = messageText.getText();
				if(strSend.trim().length() == 0) {
					return;
				}
				sendMessageToServer(strSend + "\n");				
				messageText.setText("");
				//messageDisplay.appendText(strSend + "\n");					
			}		
		});		
	}
	
	//send message to server
	public void sendMessageToServer(String strSend) {
		
		try {
			dos = new DataOutputStream(clientSocket.getOutputStream());//
			//发送给服务器端的message,相当于写过去，方法是dos.writeUTF，且服务器端必须有接收这个内容的方法
			dos.writeUTF(strSend);			
		} catch (IOException e) {			
			e.printStackTrace();
		}	
	}
	
	class Receive implements Runnable{

		@Override
		public void run() {
			try {
				while(isConnect) {		
					DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
					String receivedStr = dis.readUTF();
					messageDisplay.appendText(receivedStr);
				}
			}catch (SocketException e) {
				System.out.println("Server has suddently broken off");
				messageDisplay.appendText("Server has suddently broken off");
			}catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
	
	public static void main(String[] args) {
		launch(args);
		
	}

//	@Override
//	public void append(String txt) {
//		String currentContent = messageDisplay.getText();
//		messageDisplay.setText(currentContent+ "\n" + txt);	
//	}
	
}
