package ANN1;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class NN {
	Double w[][][];// 鏈結值(第幾層,第幾單元,第幾項) 最後一層為輸出層
	Double wo[][], delta[][];// 每層每個神經元輸出,每層每個神經元的delta
	Double data[];// 單筆資料
	int dataSize;

	Iterator<Integer> gitr;

	int groupArray[];

	OP op;

	int layers[];// 記錄每層有多少單元
	int layerSize;// 共有幾層
	int whichAF;// activationFunction
	int whichOP;// output layer style
	int max;
	
	Double wBest[][][];// 目前最佳鍵結值
	double wBestIrate;// 目前最佳辨識率
	double Irate, E;// 整體辨識率,均方差
	int cyclePerOutput;
	Random ran = new Random();

	NN(int whichAF, int whichOP, int layers[], int groupArray[], int dataSize,int cyclePerOutput) {
		// 讀取隱藏層層數,神經元數
		this.layers = layers;
		this.groupArray = groupArray;

		this.whichAF = whichAF;
		this.whichOP = whichOP;

		SetOP(groupArray.length);

		this.dataSize = dataSize;
		this.cyclePerOutput =cyclePerOutput;
		layerSize = layers.length;

		max = 0;// 每層神經元數的最大值
		// 記錄每層有多少神經元

		// 隱藏層神經元數
		for (int i = 0; i < layerSize - 1; i++) {
			if (layers[i] > max) {// 更新最多神經元數
				max = layers[i];
			}
		}
		//

		// 輸出層神經元數
		layers[layerSize - 1] = op.OPLSize();
		//

		if (layers[layerSize - 1] > max) {// 如果輸出層較多
			max = layers[layerSize - 1];
		}

		if (dataSize - 1 > max) {// 如果輸入層較多
			max = dataSize - 1;
		}

		// 參數重設
		w = new Double[layerSize][max + 1][max + 1];// max為最大單元維度(第0個為閥值)
		wBest = new Double[layerSize][max + 1][max + 1];// 目前最佳鍵結值
		wo = new Double[layerSize][max + 1];// 目前各層各個神經元輸出
		delta = new Double[layerSize][max + 1];

		// 初始化所有鍵結值
		for (int i = 0; i < layerSize; i++) {
			for (int j = 0; j < layers[i]; j++) {
				int kk;// 該層神經元數
				if (i == 0) {// 輸入層
					kk = dataSize;
				} else {// 隱藏層 輸入參數為上一層輸出
					kk = layers[i - 1] + 1;// +1為閥值運算
				}
				for (int k = 0; k < kk; k++) {
					w[i][j][k] = ran.nextDouble() * 2 - 1;// 第i層第j個神經元的第k維數值
					
					
				}
			}
		}

	}

	public void train(int Iteration, Double lr, Double lDR, Double mlr, boolean isPocket,
			LinkedList<Double[]> traindatas) {

		for (int i = 0; i < Iteration; i++) {// 訓練trainTime次
			Iterator<Double[]> dItr = traindatas.iterator();

			while (dItr.hasNext()) {// 抓訓練資料集
				data = dItr.next();
				int correctGroup = calOutput(data, w,wo);

				// 倒傳遞與調整

				// 輸出層倒傳隱藏層
				for (int k = 0; k < layers[layerSize - 1]; k++) {
					double q = wo[layerSize - 1][k];
					delta[layerSize - 1][k] = (op.TC(correctGroup, k) - q) * diffAF(q);

					// System.out.println(groupPositionArray[correctGroup][k]+" "+q+" "+diffAF(q)+"
					// "+delta[layerSize - 1][k]);

				}

				// 隱藏層內倒傳
				for (int j = layerSize - 2; j >= 0; j--) {
					for (int k = 0; k < layers[j]; k++) {

						double q = wo[j][k];// 第j層第k個神經元輸出q
						Double sum = 0.0;
						for (int m = 0; m < layers[j + 1]; m++) {
							sum += delta[j + 1][m] * w[j + 1][m][k + 1];// 第j+1層第m個神經元的delta值 *
																		// 第j+1層第m個神經元的第k+1維值(第1維是閥值項)

						}
						delta[j][k] = diffAF(q) * sum;
					}
				}

				// 第1層調整鍵結值
				for (int k = 0; k < layers[0]; k++) {

					w[0][k][0] += lr * delta[0][k] * -1;// 閥值

					for (int m = 1; m < dataSize; m++) {
						w[0][k][m] += lr * delta[0][k] * data[m - 1];
					}
				}

				// 第2層之後調整鍵結值
				for (int j = 1; j < layerSize; j++) {
					for (int k = 0; k < layers[j]; k++) {
						w[j][k][0] += lr * delta[j][k] * -1;// 閥值
						for (int m = 1; m < layers[j - 1] + 1; m++) {
							w[j][k][m] += lr * delta[j][k] * wo[j - 1][m - 1];
						}
					}
				}
			}

			if (isPocket) {
				double r[] = calIdRate(w, traindatas, false);
				Irate = r[0];// 預測辨識率
				E = r[1];

				if (Irate > wBestIrate) {
					wBestIrate = Irate;
					wBest = clone(w);
				}
			}

			if (i % cyclePerOutput == cyclePerOutput-1) {
				double r[] = calIdRate(w, traindatas, false);
				Irate = r[0];// 預測辨識率
				E = r[1];
				System.out.println(Irate + "\t" + E);

			}
			if (lr > mlr) {// 判斷學習率是否小於最小值
				lr *= (1.0 - lDR);
			} else {
				lr = mlr;
			}
		}

		// 輸出最佳解
		if (!isPocket) {// 沒有紀錄最佳解，以當前鍵結值為最佳解
			wBest = clone(w);
		}

		/*
		 * double r[] = calIdRate(wBest, testdatas, true);// 測試資料辨識率 Irate = r[0];//
		 * 預測辨識率 E = r[1];
		 */
	}

	// 計算結果
	public double[] test(LinkedList<Double[]> testdatas) {
		return calIdRate(wBest, testdatas, true);// 測試資料辨識率
	}

	// 計算輸出(forward)
	protected int calOutput(Double data[], Double w[][][],Double wo[][]) {
		for (int k = 0; k < layers[0]; k++) {// 輸入層進入隱藏層
			double sum = -1 * w[0][k][0];// 處理閥值項 Threshold
			for (int m = 0; m < data.length - 1; m++) {// WmXm
				sum += data[m] * w[0][k][m + 1];
			}
			wo[0][k] = AF(sum);
			// System.out.println(sum+" "+wo[0][k]);
			// wo[0][k] = 1 / (1 + Math.exp(sum * -1));
		}
		for (int j = 1; j < layerSize; j++) {// 隱藏層內
			for (int k = 0; k < layers[j]; k++) {
				double sum = -1 * w[j][k][0];// 處理閥值
				for (int m = 0; m < layers[j - 1]; m++) {
					sum += wo[j - 1][m] * w[j][k][m + 1];
				}
				wo[j][k] = AF(sum);
				// System.out.println(sum+" "+wo[0][k]);
				// wo[j][k] = 1 / (1 + Math.exp(sum * -1));
			}
		}
		// 確定資料集輸出的分群
		int correctGroup = -1;
		for (int j = 0; j < groupArray.length; j++) {
			if (data[data.length - 1] == groupArray[j]) {
				correctGroup = j;
				break;
			}
		}
		return correctGroup;
	}

	// Activation Function
	protected double AF(double x) {

		double r = x;

		switch (whichAF) {
		case 0:// sigmoid
			r = 1 / (1 + Math.exp(r * -1));
			break;
			
		case 1:// tanh
			r = Math.tanh(r);
			break;
		case 2:// Relu
			if (x <= 0) {
				r = 0;
			} else {
				r = x;
			}
			break;
		case 3:// LRelu
			if (x <= 0) {

				r = 0.01 * x;
			} else {
				r = x;
			}
			break;
		}

		return r;
	}

	// Activation Function 微分
	protected double diffAF(double x) {
		double r = x;
		switch (whichAF) {
		case 0:// sigmoid'
			r = r * (1 - r);
			break;
			
		case 1:// tanh'
			r = 1 - r * r;
			break;

		case 2:// Relu'
			if (x <= 0) {
				r = 0;
			} else {
				r = 1;
			}
			break;
		case 3:// LRelu'
			if (x <= 0) {
				r = 0.01;
			} else {
				r = 1;
			}
			break;
		}
		return r;
	}

	// 設定輸出層參數
	protected void SetOP(int GroupSize) {
		switch (whichOP) {
		case 0:// 2bit
			op = new TBOP(GroupSize);
			break;
		case 1:// 公因數
			op = new CFOP(GroupSize);
			break;
		case 2:// 1對1
			op = new OTOOP(GroupSize);
			break;
		}
	}

	// 計算(預測辨識率,方均誤差)並回傳
	protected double[] calIdRate(Double wLocal[][][], LinkedList<Double[]> datasLocal, boolean isRdatas) {
		// 計算辨識率
		int correct = 0, output;
		Iterator<Double[]> itr = datasLocal.iterator();
		double E = 0;

		while (itr.hasNext()) {
			data = itr.next();

			int correctGroup = calOutput(data, wLocal,wo);// 計算各層輸出

			output = op.OPtoGroup(wo[layerSize - 1]);
			E += op.calE(wo[layerSize - 1], correctGroup);

			if (isRdatas) {// 如果要建預測結果資料集，取代輸出項
				data[dataSize - 1] = (double) groupArray[output];
			}

			if (output == correctGroup) {
				correct += 1;
			}

			// System.out.println(wo[layerSize - 1][0]+"\t"+wo[layerSize -
			// 1][1]+"\t"+output+"\t"+correctGroup);
		}
		Irate = (double) correct / (double) datasLocal.size();// 預測辨識率
		E /= 2 * op.OPLSize() * datasLocal.size();

		double[] r = { Irate, E };
		return r;
	}
	
	
	
	Double[][][] clone(Double[][][] a) {
		Double[][][] b = new Double[a.length][a[0].length][a[0][0].length];
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				for (int k = 0; k < b[0][0].length; k++) {
					b[i][j][k] = a[i][j][k];
				}
			}
		}
		return b;
	}
}
