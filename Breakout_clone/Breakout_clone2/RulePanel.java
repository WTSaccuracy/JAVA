package Breakout_clone2;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RulePanel extends Panel {
	Button OKBTN = new Button("OK");
	Text Text = new Text(
			"以左右方向鍵控制橫桿，\n空白鍵射出圓球，\nTAB為暫停，\n利用圓球擊破磚塊，\n擊破所有磚塊則獲勝，\n所有球掉落則遊戲結束。\n\n遊戲中有許多道具會在擊破磚塊後掉落，以橫桿碰觸則獲得道具效果，\n各道具有什麼效果請在遊戲中自行體驗，\n遊戲愉快！");
	VBox VBox = new VBox();

	RulePanel() {
		OKBTN.setAlignment(Pos.CENTER);
		OKBTN.setPrefSize(200, 50);
		VBox.setSpacing(2);
		VBox.setAlignment(Pos.CENTER);
		VBox.getChildren().add(Text);
		VBox.getChildren().add(OKBTN);
		Pane.setCenter(VBox);
	}
}
