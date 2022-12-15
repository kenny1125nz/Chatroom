/**
 * 
 */
package previousClass;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StartClientSide extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Client.fxml"));
			primaryStage.setResizable(false);
			Scene scene = new Scene(root);	
		//	Image icon = new Image("ticket.png");
			primaryStage.setTitle("Client Side");
		//	primaryStage.getIcons().add(icon);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		launch(args);
//	}
	
}
