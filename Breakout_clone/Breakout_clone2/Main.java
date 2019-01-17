package Breakout_clone2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javafx.application.*;
import javafx.geometry.Pos;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.scene.media.*;


public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	double Volume_BGM;
	double Volume_SE;

	double fps;
	
	// read file
	static final String path = "file:bin/Breakout_clone2/";
	static final String SoundPath = path + "sound/";
	static final String PhotoPath = path + "photo/";
	
	
	// sound,photo
	static final String Sound = new String("-fx-background-image: url(\"" + PhotoPath + "Sound.png\");");
	static final String nSound = new String("-fx-background-image: url(\"" + PhotoPath + "nSound.png\");");
	
	static final AudioClip BGM = new AudioClip(SoundPath + "main.wav");
	static final AudioClip ChooseSound = new AudioClip(SoundPath + "choose.wav");
	
	
	
	static Boolean SoundCheck = true;

	// create panels
	static RulePanel RPanel;
	static PausePanel PPanel;
	static WinPanel WPanel;
	static LosePanel LPanel;
	static GamePanel GPanel;

	static final BorderPane MPane = new BorderPane();
	static final Scene Scene = new Scene(MPane, 1000, 1000);

	static Stage stage;



	public void start(Stage Stage) {
		
		// load properties
		Properties properties = new Properties();
		try {//getClass().getResourceAsStream("config.properties")
			properties.load(getClass().getResourceAsStream("config.properties"));
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Volume_BGM = Double.parseDouble(properties.getProperty("Volume_BGM")) * 0.1;
		Volume_SE = Double.parseDouble(properties.getProperty("Volume_SE")) * 0.1;
		fps = Double.parseDouble(properties.getProperty("fps"));
		
		
		
		Ball.HitSE.setVolume(Volume_SE);
		Ball.ConveySE.setVolume(Volume_SE);
		Ball.PassSE.setVolume(Volume_SE);
		Ball.FailSE.setVolume(Volume_SE);
		Buff.BuffSE.setVolume(Volume_SE);
		Buff.NerfSE.setVolume(Volume_SE);
		Explosion.ExplosionSE.setVolume(Volume_SE);
		GamePanel.BGM.setVolume(Volume_BGM);

		

		Buff.fps = fps;
		StickMove.fps = fps;
		GamePanel.fps = fps;

		RPanel = new RulePanel();
		PPanel = new PausePanel();
		WPanel = new WinPanel();
		LPanel = new LosePanel();
		GPanel = new GamePanel();
		Ball.GPanel = GPanel;
		Buff.GPanel = GPanel;

		stage = Stage;
		setMenu();
		stage.setScene(Scene);
		stage.setTitle("Menu");
		stage.show();

		// Pause return to game
		PPanel.Pane.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			case TAB: {
				ChooseSound.play();
				SwitchRoot("Game");
				GPanel.resume();
			}
			}
		});

		// Win to next level
		WPanel.NextLevelBTN.setOnAction(e -> {
			ChooseSound.play();
			if (GPanel.getLevel() < 5) {
				GPanel.setLevel(GPanel.getLevel() + 1);
			}
			GPanel.newgame(stage);
		});

		// Win to Menu
		WPanel.MenuBTN.setOnAction(e -> {
			ChooseSound.play();
			SwitchRoot("Menu");
		});

		// Lose to try again
		LPanel.RetryBTN.setOnAction(e -> {
			ChooseSound.play();
			GPanel.newgame(stage);
		});

		// Lose to Menu
		LPanel.MenuBTN.setOnAction(e -> {
			ChooseSound.play();
			SwitchRoot("Menu");
		});

		// close
		stage.setOnCloseRequest(e -> {
			stage.close();
			System.exit(0);
		});

	}

	static void SwitchRoot(String PaneStr) {

		Pane pane = new Pane();
		String title = "";
		switch (PaneStr) {
		case "Menu":
			pane = MPane;
			title = PaneStr;
			
			if (SoundCheck && !BGM.isPlaying()) {
				BGM.play();
			}
			
			break;
		case "Rule":
			pane = RPanel.Pane;
			title = PaneStr;
			break;
		case "Game":
			pane = GPanel.Pane;
			title = "Game Level :" + GPanel.getLevel();
			break;
		case "Pause":
			pane = PPanel.Pane;
			title = PaneStr;
			break;
		case "Win":
			pane = WPanel.Pane;
			title = PaneStr;
			break;
		case "Lose":
			pane = LPanel.Pane;
			title = PaneStr;
			break;
		default:
			break;
		}
		Scene.setRoot(pane);
		stage.setTitle(title);
		pane.requestFocus();
		System.out.println("換場");
	}

	void setMenu() {

		// main panel
		Button GameStartBTN = new Button("GameStart");
		GameStartBTN.setPrefSize(300, 100);
		ComboBox<String> ComboBox = new ComboBox<>();
		ComboBox.setPrefSize(300, 30);
		Button GameRuleBTN = new Button("GameRule");
		GameRuleBTN.setPrefSize(300, 100);
		Button ExitBTN = new Button("Exit");
		ExitBTN.setPrefSize(300, 100);

		VBox VBox = new VBox();
		VBox.setSpacing(4);
		VBox.setAlignment(Pos.CENTER);
		VBox.getChildren().add(GameStartBTN);
		VBox.getChildren().add(ComboBox);
		VBox.getChildren().add(GameRuleBTN);
		VBox.getChildren().add(ExitBTN);

		MPane.setCenter(VBox);

		ChooseSound.setVolume(Volume_SE);
		BGM.setCycleCount(AudioClip.INDEFINITE);
		BGM.setVolume(Volume_BGM);
		BGM.play();

		Button SoundBTN = new Button();//
		SoundBTN.setPrefSize(32, 32);
		SoundBTN.setStyle(nSound);
		MPane.setTop(SoundBTN);

		SoundBTN.setOnAction(e -> {
			ChooseSound.play();
			if (SoundCheck) {
				SoundBTN.setStyle(Sound);
				SoundCheck = false;
				BGM.stop();
			} else {
				SoundBTN.setStyle(nSound);
				SoundCheck = true;
				BGM.play();
			}
		});

		// game start
		GameStartBTN.setOnAction(e -> {
			ChooseSound.play();
			BGM.stop();
			GPanel.newgame(stage);
			SwitchRoot("Game");
		});

		String LevelChoose[] = { "1", "2", "3", "4", "5" };
		ComboBox.setValue("Level");
		ObservableList<String> items = FXCollections.observableArrayList(LevelChoose);
		ComboBox.getItems().addAll(items);

		// choose level
		ComboBox.setOnAction(e -> {
			ChooseSound.play();
			GPanel.setLevel(Integer.valueOf(ComboBox.getValue()));
		});

		// to rule
		GameRuleBTN.setOnAction(e -> {
			ChooseSound.play();
			SwitchRoot("Rule");
		});

		// Rule return to menu
		RPanel.OKBTN.setOnAction(e -> {
			ChooseSound.play();
			SwitchRoot("Menu");
		});

		// exit
		ExitBTN.setOnAction(e -> {
			stage.close();
			System.exit(0);
		});

	}

}