package Breakout_clone2;

import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GamePanel extends Panel {
	
	static AudioClip BGM = new AudioClip(Main.SoundPath+"GameBGM.mp3");
	Image holeImage = new Image(Main.PhotoPath + "hole.png");

	static final Rectangle Stick = new Rectangle(400, 980, 200, 20);

	StickMove stickMove;
	Random random = new Random();

	int BrickExist[][] = new int[10][14];
	Rectangle Brick[][] = new Rectangle[10][14];

	private int BrickCount = 0, level = 1;
	static double fps;

	void setLevel(int lv) {
		level = lv;
	}

	int getLevel() {
		return level;
	}

	void brickBreak() {
		BrickCount--;
	}

	int getBrickCount() {
		return BrickCount;
	}

	Color color[] = new Color[7];// 磚塊分7等級，各不同顏色
	int hole[][] = new int[4][2];// 4個黑洞的XY座標

	ImageView[] holeIV = new ImageView[4];

	GamePanel() {
		String BrickColor[] = { "#c9c9c9", "#FF8C00", "#FFFF00", "#FF0000", "#63B8FF", "#4B0082", "#000000" };
		for (int i = 0; i <= 6; i++) {
			color[i] = Color.web(BrickColor[i]);
		}

		BGM.setCycleCount(AudioClip.INDEFINITE);

		stickMove = new StickMove(Stick);

		Stick.setFill(Color.GRAY);
		Stick.setStroke(Color.BLACK);

		Buff.Stick = Stick;

		Pane = new BorderPane();
		Pane.getChildren().add(Stick);
	}

	void newgame(Stage stage) {
		// 要在建pane前清除
		Ball.getBalls().clear();
		Buff.clear();

		Pane = new BorderPane();
		Pane.getChildren().add(Stick);

		if (Main.SoundCheck) {
			BGM.play();
		}

		// Brick
		BrickCount = 0;
		for (int i = 0, c = 0; i <= 9; i++) {// 建立關卡 10*14 bricks
			for (int j = 0; j <= 13; j++) {
				c = random.nextInt(2 + level);
				BrickExist[i][j] = c;

				if (c > 0) {
					BrickCount += c;
					Brick[i][j] = new Rectangle(150 + 50 * j, 200 + 30 * i, 50, 30);
					Brick[i][j].setFill(color[c - 1]);
					Brick[i][j].setStroke(Color.BLACK);
					Pane.getChildren().add(Brick[i][j]);
				}
			}
		}
		Ball.Brick = Brick;
		Ball.color = color;

		// GameSpeed
		double GameSpeed = fps;
		for (int i = 1; i < level; i++) {
			GameSpeed *= 1.05;
		}

		Stick.setX(400);
		Stick.setY(980);
		Stick.setWidth(200);
		Stick.setFill(Color.GRAY);
		Stick.setArcHeight(20.0);
		Stick.setArcWidth(20.0);
		// Ball
		Ball.Stage = stage;
		Ball.Stick = Stick;
		Ball.reset(GameSpeed);
		Ball.add(new Circle(500, 970, 10));

		StickMove.ReverseCount = 0;// 顛倒時間計數
		StickMove.needReverse = false;// 需要顛倒
		StickMove.isReverse = false;// 是否正在顛倒

		// 開始遊戲狀態
		resume();

		// hole
		for (int i = 0; i <= 3; i++) {
			holeIV[i] = new ImageView(holeImage);
		}
		// top hole
		hole[0][0] = random.nextInt(14) + 3;// x
		hole[0][1] = random.nextInt(2) + 1;// y
		// botton hole
		hole[1][0] = random.nextInt(14) + 3;
		hole[1][1] = 11;
		// left hole
		hole[2][0] = 1;
		hole[2][1] = random.nextInt(5) + 4;
		// right hole
		hole[3][0] = 18;
		hole[3][1] = random.nextInt(5) + 4;

		for (int i = 0; i <= 3; i++) {
			holeIV[i].setX(hole[i][0] * 50);
			holeIV[i].setY(hole[i][1] * 50);
			Pane.getChildren().add(holeIV[i]);
		}

		Pane.setOnKeyReleased(e -> {// 放開按鍵
			switch (e.getCode()) {
			case RIGHT: {
				if (stickMove.RPressed)
					stickMove.RPressed = false;
				break;
			}
			case LEFT: {
				if (stickMove.LPressed)
					stickMove.LPressed = false;
				break;
			}
			}
		});

		Pane.setOnKeyPressed(e -> {// 遊戲中按按鍵

			switch (e.getCode()) {
			case RIGHT: {
				if (!stickMove.RPressed)
					stickMove.RPressed = true;
				break;
			}
			case LEFT: {
				if (!stickMove.LPressed)
					stickMove.LPressed = true;
				break;
			}

			case SPACE: {// Throw the ball
				if (!Ball.isThrow) {
					Ball.isThrow = true;
					Ball.getBalls().get(0).animation.play();
				}

				break;
			}

			case TAB: {// to Pause
				Main.SwitchRoot("Pause");
				pause();
				break;
			}

			}

		});
		Main.SwitchRoot("Game");
	}

	// 開始遊戲狀態
	void resume() {
		Ball.resume();
		stickMove.play();
		Buff.isPause = false;
	}

	// 暫停狀態
	void pause() {
		Ball.pause();
		stickMove.pause();
		Buff.isPause = true;
	}
}
