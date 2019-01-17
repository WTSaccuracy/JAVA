package Breakout_clone2;

import javafx.animation.Transition;
import javafx.animation.Interpolator;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Rectangle;

public class Explosion extends Transition {

	private static Image[] expIs;
	private static int count;// photo num
	static {
		expIs = new Image[10];
		count = expIs.length;
		for (int i = 1; i <= count; i++) {
			expIs[i - 1] = new Image(Main.PhotoPath + "/exp" + i + ".png");
		}
	}
	static AudioClip ExplosionSE = new AudioClip(Main.SoundPath + "Explosion.wav");
	private ImageView expIV = new ImageView();
	private int previousIndex;// index of previous photo

	Rectangle brick;

	Explosion(Rectangle brick) {
		this.brick = brick;
		double durationMs = 600;

		setCycleCount(1);
		setCycleDuration(Duration.millis(durationMs));
		setInterpolator(Interpolator.LINEAR);

		Main.GPanel.Pane.getChildren().add(expIV);
		expIV.setLayoutX(brick.getX() - 50);
		expIV.setLayoutY(brick.getY() - 30);
		this.play();
		ExplosionSE.play();
	}

	@Override
	protected void interpolate(double ms) {
		// 當前圖片index
		final int nowindex = Math.min((int) Math.floor(ms * count), count);

		if (nowindex == count) {// 動畫結束，回收   animation end,remove
			Main.GPanel.Pane.getChildren().remove(expIV);
		} else if (nowindex != previousIndex) {// put next photo
			expIV.setImage(expIs[nowindex]);
			previousIndex = nowindex;
		}

	}
}
