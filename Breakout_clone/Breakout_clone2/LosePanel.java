package Breakout_clone2;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class LosePanel extends Panel {
	Button RetryBTN = new Button("Retry");
	Button MenuBTN = new Button("Menu");
	VBox VBox = new VBox();

	LosePanel() {
		RetryBTN.setPrefSize(300, 100);
		MenuBTN.setPrefSize(300, 100);
		VBox.setSpacing(2);
		VBox.setAlignment(Pos.CENTER);
		VBox.getChildren().add(RetryBTN);
		VBox.getChildren().add(MenuBTN);
		Pane.setCenter(VBox);
	}
}
