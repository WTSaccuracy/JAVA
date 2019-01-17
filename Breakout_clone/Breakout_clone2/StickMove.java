package Breakout_clone2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class StickMove {

	static int ReverseCount = 0;// 顛倒時間計數
	static final int ReverseTime = 30;// 顛倒時間長度
	static Boolean needReverse = false;// 需要顛倒
	static Boolean isReverse = false;// 是否正在顛倒
	static double fps;
	Timeline reverse = new Timeline(new KeyFrame(Duration.millis(100), e -> reverseCount()));

	Boolean RPressed = false;
	Boolean LPressed = false;

	double moveX = 10;
	Timeline animation;
	Rectangle Stick;

	StickMove(Rectangle Stick) {
		reverse.setCycleCount(Timeline.INDEFINITE);
		this.Stick = Stick;
		double time = 1000.0 / fps;

		animation = new Timeline(new KeyFrame(Duration.millis(time), e -> move()));
		animation.setCycleCount(Timeline.INDEFINITE);
	}

	void move() {
		// 沒有撞牆才移動

		double next = moveX;// 初始為正(向右)

		if (RPressed ^ LPressed) { // 只按左或只按右

			if (isReverse) { // 倒轉debuff
				next *= -1.0;
			}

			if (LPressed) { // 按左
				next *= -1.0;
			}

			double x = next + Stick.getX();// x = 位移後座標

			// 位移後座標若沒超出範圍
			if (!(x > 1000 - Stick.getWidth()) // 沒撞上右牆
					&& !(x < 0)) { // 沒撞上左牆
				Stick.setX(x);

				// 還有球
				if (Ball.getBalls().size() != 0) {
					// 球未丟出
					if (!Ball.isThrow) {
						Ball.getBalls().get(0).circle.setCenterX(Ball.getBalls().get(0).circle.getCenterX() + next);
					}
				}
			}

		}

	}

	void play() {
		reverse.play();
		animation.play();
	}

	void pause() {
		LPressed = false;
		RPressed = false;
		reverse.pause();
		animation.pause();
	}

	void reverseCount() {// 顛倒倒數

		if (needReverse) {// 接到debuff通知

			needReverse = false;
			ReverseCount += ReverseTime;

			if (!isReverse) {// 目前非debuff狀態 變顏色
				Stick.setFill(Color.PURPLE);
				isReverse = true;
			}

		} else {// 沒接到debuff通知

			if (ReverseCount == 0) {// 狀態正常

				if (isReverse) {// 從狀態中恢復 變顏色
					Stick.setFill(Color.GRAY);
					isReverse = false;
				}

			} else {// 狀態中 減少時間
				ReverseCount--;
			}

		}
	}
}
