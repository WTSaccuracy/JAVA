package Breakout_clone2;

import javafx.scene.text.Text;

public class PausePanel extends Panel {
	Text Text = new Text("Pause");

	PausePanel() {
		Pane.setCenter(Text);
	}
}
