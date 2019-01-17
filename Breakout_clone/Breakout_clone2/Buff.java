package Breakout_clone2;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Buff {
	static boolean isPause = false;

	static AudioClip BuffSE = new AudioClip(Main.SoundPath+"buff.wav");
	static AudioClip NerfSE = new AudioClip(Main.SoundPath+"nerf.wav");

	static double fps;
	static GamePanel GPanel;

	static ArrayList<Buff> buffs = new ArrayList<Buff>();

	static Rectangle Stick;

	ImageView buffIV;

	int BuffType;

	Rectangle brick;
	Ball ball;

	Timeline animation;
	Timeline reverseTime;
	Random random = new Random();

	Buff(int BuffType, Rectangle brick, Ball ball) {
		buffs.add(this);
		this.BuffType = BuffType;
		this.brick = brick;
		this.ball = ball;

		buffIV = new ImageView(new Image(Main.PhotoPath + BuffType + ".png"));
		buffIV.setLayoutX(brick.getX());
		buffIV.setLayoutY(brick.getY());
		GPanel.Pane.getChildren().add(buffIV);
		double t = 1000.0 / fps;
		animation = new Timeline(new KeyFrame(Duration.millis(t), e -> move()));
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.play();
	}

	void move() {
		if (!isPause) {
			buffIV.setY(buffIV.getY() + 4);
		}

		// 吃到Buff
		if (GamePanel.Stick.intersects(buffIV.getBoundsInParent())) {

			switch (BuffType) {

			case 0: {// make stick longer
				BuffSE.play();
				if (Stick.getWidth() < 480) {
					Stick.setWidth(Stick.getWidth() + 40);
					if (Stick.getWidth() + Stick.getX() > 1000) {// 超過右邊界向左移
						Stick.setX(Stick.getX() - 40);
					}
				}
				break;
			}
			case 1: {// make stick shorter
				NerfSE.play();
				if (Stick.getWidth() > 120) {
					Stick.setWidth(Stick.getWidth() - 40);
				}
				break;
			}
			case 2: {// plus one ball
				BuffSE.play();

				// Ball

				Ball nBall = Ball.add(new Circle(ball.circle.getCenterX(), ball.circle.getCenterY(), 10));

				switch (random.nextInt(2)) {
				case 0:
					nBall.vy = ball.vy * -1.0;
					break;
				case 1:
					nBall.vx = ball.vx * -1.0;
					break;
				case 2:
					nBall.vx = ball.vx * -1.0;
					nBall.vy = ball.vy * -1.0;
					break;
				}
				break;
			}
			case 3: {// explosive ball
				BuffSE.play();
				Ball.ExpBuff();

				break;
			}
			case 4: {// reverse
				NerfSE.play();
				StickMove.needReverse = true;
				break;
			}

			}
			// 刪除Buff
			remove();
		} else if (buffIV.getY() >= 1000) {
			remove();
		}
	}

	void remove() {
		GPanel.Pane.getChildren().remove(buffIV);
		buffs.remove(this);
		animation.stop();
	}

	static void clear() {
		for (Buff buff : buffs) {
			buff.animation.stop();
		}
		buffs.clear();
	}
}
