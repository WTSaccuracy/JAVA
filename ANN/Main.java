package ANN1;

import java.io.*;
import java.util.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.text.*;

public class Main extends Application {
	File file;
	FileChooser fc = new FileChooser();

	
	OP op;

	DataSet dataSet;
	int whichAF;// activationFunction
	int whichOP;// output layer style

	double Irate, E;// 整體辨識率,均方差

	// GUI
	Scene scene;
	GridPane Gpane;
	GridPane RBpane;// 輸入欄位

	Label lrl;// LearnRateLabel
	TextField lrf;// LearnRateField

	Label itl;// IterationLabel
	TextField itf;// IterationField

	Label hll;// HiddenLayerLabel
	TextField hlf;// HiddenLayerField

	Button lf;// LoadFile
	Button cal;// Calculate
	Button clean;// Clean
	Text text = new Text();// 顯示結果

	// 繪圖
	final NumberAxis xAxisE = new NumberAxis();
	final NumberAxis yAxisE = new NumberAxis();
	ScatterChart<Number, Number> scE = new ScatterChart<Number, Number>(xAxisE, yAxisE);
	final NumberAxis xAxisR = new NumberAxis();
	final NumberAxis yAxisR = new NumberAxis();
	ScatterChart<Number, Number> scR = new ScatterChart<Number, Number>(xAxisR, yAxisR);

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) throws Exception {

		// GUI
		lrl = new Label("LearnRate :");
		lrf = new TextField();
		Label lrl2 = new Label("學習率");

		itl = new Label("Iteration :");
		itf = new TextField();
		Label ttl2 = new Label("1000=拿每組訓練資料各訓練1000次");

		Label hll = new Label("hiddenLayer :");// LayerLabel
		TextField hlf = new TextField();// LayerField
		Label hll2 = new Label("\"5 4\"=2層隱藏層,第1層5個神經元,第2層4個神經元");

		
		// 開啟檔案設定
		//String path = getClass().getResource("dataset/").getPath();// /bin/package/
		
		//fc.setInitialDirectory(new File(path));
		 
		
		// 讀取檔案,資料集整理,輸出資料圖
		lf = new Button("LoadFile");
		lf.setOnMouseClicked(e -> {
			
			file = fc.showOpenDialog(null);//stage
			try {
			dataSet = new DataSet(file);
			} catch (Exception ex) {
				ex.printStackTrace();
			} 
			
			
			// 繪圖
			scE.getData().clear();
			dataSet.draw(scE, dataSet.datas);
		});
		// 讀取檔案,資料集整理,輸出資料圖 end

		// 讀取參數，計算，輸出結果。
		cal = new Button("Calculate");
		cal.setOnMouseClicked(e -> {

			// load properties
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(getClass().getResource("config/config.properties").getPath()));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			Double lDR = Double.parseDouble(properties.getProperty("learnDecreaseRate"));
			Double mlr = Double.parseDouble(properties.getProperty("minLearnRate"));
			Double lr = Double.parseDouble(properties.getProperty("learnRate"));
			
			
			int Iteration = Integer.valueOf(properties.getProperty("Iteration"));// 訓練次數
			
			int whichAF = Integer.parseInt(properties.getProperty("whichAF"));// activationFunction
			int whichOP = Integer.parseInt(properties.getProperty("whichOP"));// output layer style
			int cyclePerOutput=Integer.parseInt(properties.getProperty("cyclePerOutput"));
			
			boolean isPocket;// 是否適用口袋鍵結值
			if (Integer.parseInt(properties.getProperty("isPocket")) == 0) {
				isPocket = false;
			} else {
				isPocket = true;
			}
			
			
			boolean isBatch;// 是否適用批次學習
			if (Integer.parseInt(properties.getProperty("isBatch")) == 0) {
				isBatch = false;
			} else {
				isBatch = true;
			}
			int batchNum = Integer.parseInt(properties.getProperty("batchNum"));
			//int batchSize = dataSet.dataNum/batchNum;
			
			boolean isBN;// 是否適用批標準化
			if (Integer.parseInt(properties.getProperty("isBN")) == 0) {
				isBN = false;
			} else {
				isBN = true;
			}
			
			
			String learnRate = properties.getProperty("learnRate");
			String IterationStr = properties.getProperty("Iteration");
			String hiddenLayer = properties.getProperty("hiddenLayer");
			lrf.setText(learnRate);
			itf.setText(IterationStr);
			hlf.setText(hiddenLayer);

			// load properties end

			// 讀取隱藏層層數,神經元數
			String [] str = hiddenLayer.trim().split("\\s+");
			int layerSize = str.length + 1;// 隱藏層+輸出層 總層數

			// 記錄每層有多少神經元

			// 隱藏層神經元數
			int[] layers = new int[layerSize]; // 記錄每層有多少神經元
			for (int i = 0; i < layerSize - 1; i++) {
				layers[i] = Integer.valueOf(str[i]);
			}

			NN nn;
			
			
			
			LinkedList<Double[]> datasAll=(LinkedList<Double[]>) dataSet.datasClone(dataSet.datas);
			
			long alltime=0;
			double[] allrTest=new double[2];
			double[] allrAll=new double[2];
			allrTest[0]=0;
			allrTest[1]=0;
			allrAll[0]=0;
			allrAll[1]=0;
			
			int cycles=Integer.parseInt(properties.getProperty("cycles"));
			
			for(int q=0;q<cycles;q++) {
			
				
			if(isBatch) {
				nn = new BNN(whichAF, whichOP, layers, dataSet.groupArray, dataSet.dataSize,cyclePerOutput,batchNum,isBN);
			}else {
				nn = new NN(whichAF, whichOP, layers, dataSet.groupArray, dataSet.dataSize,cyclePerOutput);
			}
			
			long time =System.currentTimeMillis();
			nn.train(Iteration, lr, lDR, mlr, isPocket, dataSet.traindatas);
			time = System.currentTimeMillis()-time;
			
			LinkedList<Double[]> datasTest = (LinkedList<Double[]>) dataSet.datasClone(dataSet.testdatas);
			datasAll=(LinkedList<Double[]>) dataSet.datasClone(dataSet.datas);
			double rTest[] = nn.test(datasTest);
			double rAll[] = nn.test(datasAll);
			
			
			alltime+=time;
			allrTest[0] += rTest[0];
			allrTest[1] += rTest[1];
			allrAll[0] += rAll[0];
			allrAll[1] += rAll[1];
			
			
			/*
			
			System.out.println("測資辨識率\t:\t" + rTest[0]
					+"\n測資誤差\t:\t" + rTest[1]
					+"\n-----------------"
					+"\n整體辨識率\t:\t" + rAll[0]
					+"\n整體誤差\t:\t" + rAll[1]
					+"\n-----------------"
					+"\n耗時\t:\t"+time
					+"\n-------------------------------------");
			*/
			}
			alltime/=cycles;
			allrTest[0] /=cycles;
			allrTest[1] /=cycles;
			allrAll[0] /=cycles;
			allrAll[1] /=cycles;
			
			
			
			//System.out.println("---------------");

			/*
			System.out.println("測資辨識率\t:\t" + allrTest[0]
					+"\n測資誤差\t:\t" + allrTest[1]
					+"\n-----------------"
					+"\n整體辨識率\t:\t" + allrAll[0]
					+"\n整體誤差\t:\t" + allrAll[1]
					+"\n-----------------"
					+"\n耗時\t:\t"+alltime
					+"\n*******************************************************");
					
			*/
			
			text.setText("Recognition for TestData\t:\t" + allrTest[0]
					+"\nMSE for TestData\t:\t" + allrTest[1]
					+"\n-----------------"
					+"\nRecognition for AllData\t:\t" + allrAll[0]
					+"\nMSE for AllData\t:\t" + allrAll[1]
					+"\n-----------------"
					+"\nTime Consuming\t:\t"+alltime+"\tms");
			
			
			
			// 繪圖
			scR.getData().clear();
			dataSet.draw(scR, datasAll);
		});

		// 繪圖設定
		xAxisE.setLabel("Para1");
		yAxisE.setLabel("Para2");
		scE.setTitle("Expected Output");

		xAxisR.setLabel("Para1");
		yAxisR.setLabel("Para2");
		scR.setTitle("Real Output");

		// 排版
		Gpane = new GridPane();
		Gpane.setHgap(100); // 水平距離
		Gpane.setVgap(0); // 垂直距離
		Gpane.setPadding(new Insets(10, 10, 10, 10)); // 填充邊界。Insets定義上、右、下、左四個方向的長度。
		Gpane.add(text, 0, 0);// 顯示辨識率等等...
		Gpane.add(scE, 0, 1);
		Gpane.add(scR, 1, 1);

		RBpane = new GridPane();
		RBpane.add(lrl, 0, 0);
		RBpane.add(lrf, 1, 0);
		RBpane.add(lrl2, 2, 0);
		RBpane.add(itl, 0, 1);
		RBpane.add(itf, 1, 1);
		RBpane.add(ttl2, 2, 1);
		RBpane.add(hll, 0, 2);
		RBpane.add(hlf, 1, 2);
		RBpane.add(hll2, 2, 2);
		RBpane.add(lf, 0, 3);
		RBpane.add(cal, 1, 3);
		Gpane.add(RBpane, 1, 0);

		scene = new Scene(Gpane, 1400, 800);

		scE.setId("Main-diagram");
		scE.setMinSize(600, 600);
		scR.setId("Main-diagram");
		scR.setMinSize(600, 600);

		// .toExternalForm()
		scene.getStylesheets().add(getClass().getResource("config/Main.css").toExternalForm());

		stage.setScene(scene);
		stage.setTitle("多層感知機");
		stage.show();
	}


}