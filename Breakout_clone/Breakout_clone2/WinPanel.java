package Breakout_clone2;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class WinPanel extends Panel {
	Button NextLevelBTN = new Button("Next Level");
	Button MenuBTN = new Button("Menu");
	VBox VBox = new VBox();

	WinPanel() {
		NextLevelBTN.setPrefSize(300, 100);
		MenuBTN.setPrefSize(300, 100);
		VBox.setSpacing(2);
		VBox.setAlignment(Pos.CENTER);
		VBox.getChildren().add(NextLevelBTN);
		VBox.getChildren().add(MenuBTN);
		Pane.setCenter(VBox);
	}
}
