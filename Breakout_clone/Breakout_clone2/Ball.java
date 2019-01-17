package Breakout_clone2;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Ball {

	static final AudioClip HitSE = new AudioClip(Main.SoundPath+"hit.wav");
	static final AudioClip ConveySE = new AudioClip(Main.SoundPath+"convey.wav");

	static final AudioClip PassSE = new AudioClip(Main.SoundPath+"pass.wav");
	static final AudioClip FailSE = new AudioClip(Main.SoundPath+"fail.wav");

	static GamePanel GPanel;
	static Stage Stage;

	static Rectangle Stick;
	private static ArrayList<Ball> Balls = new ArrayList<Ball>();

	private static double time;

	static boolean isThrow = false;

	Random random = new Random();
	Circle circle;
	double vx = 1, vy = -1;

	static Rectangle Brick[][] = new Rectangle[10][14];// brick shapes
	static Color[] color = new Color[7];

	int ExpBallT = 0;// BUFFtime
	int BuffType = 0;//  0=non 1=fireball	
	
	//**機率用讀取
	double BuffP = 0.6; //probability
	Timeline animation;

	Timeline Expanimation;
	int c = 0;
	Pane Pane = new Pane();

	int fast = 3;

	Ball(Circle circle) {
		this.circle = circle;

		animation = new Timeline(new KeyFrame(Duration.millis(time / fast), e -> moveBall()));

		animation.setCycleCount(Timeline.INDEFINITE);

	}

	void moveBall() {
		// 球移動
		double newX = circle.getCenterX() + vx;
		double newY = circle.getCenterY() + vy;
		circle.setCenterX(newX);
		circle.setCenterY(newY);

		// ball doesnt fall
		if (circle.getCenterY() <= 1000) {

			// in the black hole 0~3
			for (int s = 0; s <= 3; s++) {
				double holeX = GPanel.holeIV[s].getX();
				double holeY = GPanel.holeIV[s].getY();

				// intersect the center of hole
				if (circle.intersects(holeX + 20, holeY + 20, 10, 10)) {

					ConveySE.play();
					// randomize speed after convey
					switch (random.nextInt(3)) {
					case 0:
						vx *= -1;
						break;
					case 1:
						vy *= -1;
						break;
					case 2:
						vx *= -1;
						vy *= -1;
						break;
					}

					// adjust position to prevent convey again
					int AdjustX;
					if (vx > 0) {// if vx > 0 ball appear in right side of hole center
						AdjustX = 40;
					} else {
						AdjustX = 10;
					}

					int AdjustY;
					if (vy > 0) {
						AdjustY = 40;
					} else {
						AdjustY = 10;
					}

					int h = 0;
					if (s != 3) {
						h = s + 1;
					}
					AdjustX += GPanel.holeIV[h].getX();
					AdjustY += GPanel.holeIV[h].getY();

					circle.setCenterX(AdjustX);
					circle.setCenterY(AdjustY);

					break;
				}
			}

			// 和橫桿碰撞 intersect stick
			if (circle.intersects(Stick.getBoundsInLocal()) && circle.getCenterY() <= 970) {
				vy *= -1.0;
			}

			// 左邊界反彈
			if (circle.getCenterX() - 10 <= 0) {
				vx *= -1;
			}
			// 右邊界反彈
			if (circle.getCenterX() + 10 >= 1000) {
				vx *= -1;
			}
			// 上邊界反彈
			if (circle.getCenterY() - 10 <= 0) {
				vy *= -1;
			}

			//if ball in bricks field
			if (circle.intersects(150, 200, 850, 350)) {

				boolean Sidecollision = false;// 磚塊邊碰撞 check brick Side collision
				boolean cornerCollosion = false;
				double vxNext = vx;
				double vyNext = vy;

				// 確認所有磚塊 check all bricks
				for (int i = 0; i <= 9; i++) {
					for (int j = 0; j <= 13; j++) {
						// 該位置磚塊是否存在 if brick[i][j] Exist
						if (GPanel.BrickExist[i][j] != 0) {

							if (circle.intersects(Brick[i][j].getBoundsInLocal())) {

								double cX = circle.getCenterX();
								double cY = circle.getCenterY();
								double bX = Brick[i][j].getX();
								double bY = Brick[i][j].getY();
								double bW = Brick[i][j].getWidth();
								double bH = Brick[i][j].getHeight();

								if (cY >= bY && cY <= bY + bH) {// 球的Y軸在磚塊內

									if (cornerCollosion) {
										vyNext = vy;
									}
									vxNext = vx * -1;
									Sidecollision = true;
								} else if (cX >= bX && cX <= bX + bW) {// 球的X軸在磚塊內
									if (cornerCollosion) {
										vxNext = vx;
									}
									vyNext = vy * -1;
									Sidecollision = true;

								} else if (!Sidecollision) {
									cornerCollosion = true;

									if (cY < bY && cX < bX) {// 左上
										vxNext = Math.abs(vx) * -1;
										vyNext = Math.abs(vy) * -1;
									} else if (cY < bY && cX > bX) {// 右上
										vxNext = Math.abs(vx);
										vyNext = Math.abs(vy) * -1;
									} else if (cY > bY && cX < bX) {// 左下
										vxNext = Math.abs(vx) * -1;
										vyNext = Math.abs(vy);
									} else if (cY > bY && cX > bX) {// 右下
										vxNext = Math.abs(vx);
										vyNext = Math.abs(vy);
									}

								}

								// 碰撞磚塊處理
								switch (BuffType) {// 確認球的狀態

								case 0: {// 無BUFF
									HitSE.play();
									GPanel.BrickExist[i][j]--;
									GPanel.brickBreak();
									if (GPanel.BrickExist[i][j] != 0) {// 磚塊被撞後還在則改變顏色
										Brick[i][j].setFill(color[GPanel.BrickExist[i][j] - 1]);
									} else {// 不在則消失
										Brick[i][j].setVisible(false);
										if (random.nextDouble() < BuffP) {// 機率出Buff
											new Buff(random.nextInt(5), Brick[i][j], this);
										}
									}
									break;
								}

								case 1: {// 火球(被撞磚塊的周圍(九宮格)都有被撞效果)
									new Explosion(Brick[i][j]);

									int x = j - 1;
									int xc = x;// for迴圈計數用
									int y = i - 1;
									int xm = j + 1;
									int ym = i + 1;
									if (x < 0) {// 如果超過邊界則修正
										x = 0;
										xc = x;
									}
									if (y < 0) {
										y = 0;
									}
									if (xm > 13) {
										xm = 13;
									}
									if (ym > 9) {
										ym = 9;
									}

									for (; y <= ym; y++) {// 判斷磚塊周圍(九宮格)
										for (; x <= xm; x++) {

											if (GPanel.BrickExist[y][x] > 0) {// 該位置有磚塊
												GPanel.BrickExist[y][x]--;
												GPanel.brickBreak();
												if (GPanel.BrickExist[y][x] > 0) {// 碰撞後磚塊還在則改變顏色
													Brick[y][x].setFill(color[GPanel.BrickExist[y][x] - 1]);
												} else {// 不在則消失
													Brick[y][x].setVisible(false);
													if (random.nextDouble() < BuffP) {// 機率出Buff
														new Buff(random.nextInt(5), Brick[y][x], this);
													}
												}

											}
										}
										x = xc;
									}

									ExpBallT--;
									if (ExpBallT == 0) {
										circle.setFill(Color.BLACK);
										BuffType = 0;
									}
									break;
								}

								}
								if (GPanel.getBrickCount() == 0) {// 勝利
									toWin();
								}
							}

						}

					}
				}
				vx = vxNext;
				vy = vyNext;

			}

		} else {// 沒接到球

			GPanel.Pane.getChildren().remove(circle);
			Balls.remove(this);// 砍掉這顆球
			if (Balls.size() == 0) {// 失敗
				toLose();
			}

			animation.stop();
		}
	}

	static void reset(double t) {
		time = 1000 / t;
		Balls.clear();
		isThrow = false;
	}

	static void pause() {

		for (Ball ball : Balls) {
			ball.animation.pause();
		}
	}

	static void resume() {
		if (isThrow) {
			for (Ball ball : Balls) {
				ball.animation.play();
			}
		}
	}

	static Ball add(Circle circle) {
		Ball nBall = new Ball(circle);
		Balls.add(nBall);
		GPanel.Pane.getChildren().add(circle);
		if (isThrow) {// 已丟第一顆球
			nBall.animation.play();
		}
		return nBall;
	}

	static void ExpBuff() {// 所有球施加BUFF
		for (Ball ball : Balls) {
			ball.circle.setFill(Color.RED);
			ball.ExpBallT += 1;// 可爆炸次數+1
			ball.BuffType = 1;
		}
	}

	static ArrayList<Ball> getBalls() {
		return Balls;
	}

	static void toWin() {
		GamePanel.BGM.stop();
		PassSE.play();
		Main.SwitchRoot("Win");
		GPanel.pause();
	}

	static void toLose() {
		GamePanel.BGM.stop();
		FailSE.play();
		Main.SwitchRoot("Lose");
		GPanel.pause();
	}

	void remove() {
		GPanel.Pane.getChildren().remove(circle);
		Balls.remove(this);// 砍掉這顆球
		animation.stop();
	}
}
