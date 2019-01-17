package ChatRoom;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application{
	
	// GUI
	static Scene scene;
	static GridPane Gpane;
	
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) throws Exception {
		Button serverB=new Button("Server");
		serverB.setPrefSize(300, 100);
		serverB.setOnAction(e -> {
			new Server(stage);
		});
		Button clientB=new Button("Client");
		clientB.setPrefSize(300, 100);
		clientB.setOnAction(e -> {
			new Client(stage);
		});
		Gpane = new GridPane();
		Gpane.add(serverB, 0, 0);
		Gpane.add(clientB, 0, 1);
		scene = new Scene(Gpane,300,800);
		stage.setScene(scene);
		stage.setTitle("Choose Type");
		stage.show();
		stage.setOnCloseRequest(e -> {
			System.exit(0);
		});
	}
}
