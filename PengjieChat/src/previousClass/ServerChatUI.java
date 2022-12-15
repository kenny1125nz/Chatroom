
package previousClass;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import chat01.Observable;
import chat01.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ServerChatUI extends Application implements Observable {

//	private static final int PORT = 6578;
	private TextField portInput;
	private Label feedback;
//	private String port;
//	private int portGo;
	private TextArea serverDisplayText;

	private Button serverStartButton;
	private Button serverStopButton;
	
	private boolean isStart = false;
	Server server;
//	private ServerSocket serverSocket = null;//
//	private Socket socket = null;
//	private DataInputStream dis = null;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		portInput = new TextField();
		Label portLabel = new Label();
		portLabel.setText("Enter Server Port:");			
		
		serverDisplayText = new TextArea();
		
		
		serverStartButton = new Button();
		serverStartButton.setText("Start Server");

		serverStopButton = new Button();
		serverStopButton.setText("Stop Server");
		
		//feebacklabel
		feedback = new Label();
//		feedback.setText("invalid input, please Enter Port to connect");

		// border pane set up
		VBox bigBox = new VBox(10);
		bigBox.getChildren().addAll(portInput, portLabel, feedback, serverStartButton, serverDisplayText,  serverStopButton);//  
		
		// scene set up
		primaryStage.setScene(new Scene(bigBox, 400, 400));
		primaryStage.sizeToScene();
//		primaryStage.getIcons().add(imageAvo);
		primaryStage.setTitle("Severside");
		primaryStage.show();
		
		//start button 监听		
		serverStartButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				
				try {
					startServer();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
			}});
			
		serverStopButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
//				Platform.exit();
				stopServer();
			}			
		});
	}
	
	
	//服务器启动方法
	public void startServer() throws IOException{
		
		int port = Integer.parseInt(portInput.getText());
		server = new Server(port, this);
		new Thread(server).start();
		
	}
	
	public void stopServer() {
		
//		if(server.serverSocket != null) {
//			try {
//				server.serverSocket.close();
//				server.serverSocket = null;
//				serverDisplayText.appendText("Server disconnect!");
//				System.exit(0);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
		
	}
	
	
	public static void main(String[] args) {
		
		launch(args);

	}


	@Override
	public void append(String txt) {
	
		String currentContent = serverDisplayText.getText();
		serverDisplayText.setText(currentContent+ "\n" + txt);	
		
		
	}
}
