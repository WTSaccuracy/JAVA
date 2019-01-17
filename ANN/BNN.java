package ANN1;

import java.util.*;

//Batch NN
public class BNN extends NN {
	int batchSize, batchNum;
	Double[][][] woBatch, deltaBatch;// 每層每個神經元輸出,每層每個神經元的delta
	// batch中第幾筆資料,第幾層,第幾個神經元
	Double[][] dataBatch;
	// batch中第幾筆資料,第幾維,

	Double[][][] dw;
	BN[][] bn;
	Boolean isBN;
	// 層,神經元,鍵結值

	BNN(int whichAF, int whichOP, int layers[], int groupArray[], int dataSize,int cyclePerOutput, int batchNum, boolean isBN) {
		super(whichAF, whichOP, layers, groupArray, dataSize,cyclePerOutput);
		this.batchNum = batchNum;
		this.isBN = isBN;
		dw = new Double[layerSize][max + 1][max + 1];
	}

	@Override
	public void train(int Iteration, Double lr, Double lDR, Double mlr, boolean isPocket,
			LinkedList<Double[]> traindatas) {

		batchSize = traindatas.size() / batchNum;

		woBatch = new Double[layerSize][max + 1][batchSize];
		deltaBatch = new Double[layerSize][max + 1][batchSize];
		bn = new BN[layerSize][max + 1];
		for (int i = 0; i < layerSize; i++) {
			for (int j = 0; j < layers[i]; j++) {
				bn[i][j] = new BN(batchNum, batchSize);
			}
		}

		Iterator<Double[]> dItr = traindatas.iterator();
		for (int i = 0; i < Iteration; i++) {// 訓練trainTime次
			for (int batchn = 0; batchn < batchNum; batchn++) {
				int correctGroups[] = new int[batchSize];
				dataBatch = new Double[dataSize][batchSize];
				for (int b = 0; b < batchSize; b++) {// batch中每組data
					if (!dItr.hasNext()) {
						dItr = traindatas.iterator();
					}
					Double[] x = dItr.next();

					for (int a = 0; a < dataSize; a++) {// x.length
						dataBatch[a][b] = x[a];
					}

				}

				correctGroups = calOutput_batch(dataBatch, w, woBatch);

				// 輸出層倒傳隱藏層

				for (int k = 0; k < layers[layerSize - 1]; k++) {
					for (int b = 0; b < batchSize; b++) {// batch中每組data

						double q = woBatch[layerSize - 1][k][b];

						// (op.TC(correctGroups[b], k) - q)
						deltaBatch[layerSize - 1][k][b] = (q - op.TC(correctGroups[b], k)) * diffAF(q);

					}

					// !!
					if (isBN) {
						bn[layerSize - 1][k].back(deltaBatch[layerSize - 1][k], lr);
					}
				}

				// 隱藏層內倒傳
				for (int n = layerSize - 2; n >= 0; n--) {
					for (int k = 0; k < layers[n]; k++) {

						for (int b = 0; b < batchSize; b++) {
							double q = woBatch[n][k][b];// 第j層第k個神經元輸出q
							Double sum = 0.0;
							for (int m = 0; m < layers[n + 1]; m++) {
								sum += deltaBatch[n + 1][m][b] * w[n + 1][m][k + 1];// 第j+1層第m個神經元的delta值 *
								// 第j+1層第m個神經元的第k+1維值(第1維是閥值項)

							}
							deltaBatch[n][k][b] = diffAF(q) * sum;
						}

						if (isBN) {
							bn[n][k].back(deltaBatch[n][k], lr);
						}
						// System.out.println("-------------");
					}
				}

				for (int n = 0; n < layerSize; n++) {
					for (int k = 0; k < layers[n]; k++) {
						for (int m = 0; m < dw[n][k].length; m++) {
							dw[n][k][m] = 0.0;

						}
					}
				}

				// 第1層調整鍵結值
				for (int b = 0; b < batchSize; b++) {
					// 第1層調整鍵結值
					for (int k = 0; k < layers[0]; k++) {

						dw[0][k][0] -= deltaBatch[0][k][b] * -1;// 閥值

						for (int m = 1; m < dataSize; m++) {
							dw[0][k][m] -= deltaBatch[0][k][b] * dataBatch[m - 1][b];
						}
					}
					// 第2層之後調整鍵結值
					for (int n = 1; n < layerSize; n++) {
						for (int k = 0; k < layers[n]; k++) {
							dw[n][k][0] -= deltaBatch[n][k][b] * -1;// 閥值
							for (int m = 1; m < layers[n - 1] + 1; m++) {

								dw[n][k][m] -= deltaBatch[n][k][b] * woBatch[n - 1][m - 1][b];

							}
						}
					}
				}

				// 第1層調整鍵結值
				for (int k = 0; k < layers[0]; k++) {
					for (int m = 0; m < dataSize; m++) {
						w[0][k][m] += dw[0][k][m] * lr / batchSize;
					}
				}
				//
				for (int n = 1; n < layerSize; n++) {
					for (int k = 0; k < layers[n]; k++) {
						for (int m = 0; m < layers[n - 1] + 1; m++) {

							w[n][k][m] += dw[n][k][m] * lr / batchSize;

						}
					}
				}
			}
			// 別刪掉!

			if (isPocket) {
				double r[] = calIdRate(w, traindatas, false);
				Irate = r[0];// 預測辨識率
				E = r[1];

				if (Irate > wBestIrate) {
					wBestIrate = Irate;

					wBest = w.clone();

				}
			}

			if (i % cyclePerOutput == cyclePerOutput - 1) {// i % 10 == 1
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
			// 處理矩陣複製問題
		}

		/*
		 * double r[] = calIdRate(wBest, testdatas, true);// 測試資料辨識率 Irate = r[0];//
		 * 預測辨識率 E = r[1];
		 */
	}

	protected int[] calOutput_batch(Double dataBatch[][], Double w[][][], Double woBatch[][][]) {

		for (int k = 0; k < layers[0]; k++) {// 輸入層進入隱藏層

			for (int b = 0; b < batchSize; b++) {

				double sum = -1 * w[0][k][0];// 處理閥值項 Threshold

				for (int m = 0; m < dataSize - 1; m++) {// Wm * Xm
					sum += dataBatch[m][b] * w[0][k][m + 1];

				}
				woBatch[0][k][b] = sum;

			}
			if (isBN) {
				bn[0][k].forward(woBatch[0][k]);
			}
			for (int b = 0; b < batchSize; b++) {
				woBatch[0][k][b] = AF(woBatch[0][k][b]);
			}

		}

		for (int j = 1; j < layerSize; j++) {// 隱藏層內
			for (int k = 0; k < layers[j]; k++) {

				for (int b = 0; b < batchSize; b++) {

					double sum = -1 * w[j][k][0];// 處理閥值

					for (int m = 0; m < layers[j - 1]; m++) {
						sum += woBatch[j - 1][m][b] * w[j][k][m + 1];
					}

					woBatch[j][k][b] = sum;
				}

				if (isBN) {

					// if (j < layerSize - 1) {// 輸出層不做批標準化

					bn[j][k].forward(woBatch[j][k]);// 測試時會變1維 注意!

					// }
				}

				for (int b = 0; b < batchSize; b++) {
					woBatch[j][k][b] = AF(woBatch[j][k][b]);
				}

			}
		}

		// 確定資料集輸出的分群
		int[] correctGroups = new int[batchSize];
		for (int b = 0; b < batchSize; b++) {
			for (int j = 0; j < groupArray.length; j++) {
				if (dataBatch[dataSize - 1][b] == groupArray[j]) {
					correctGroups[b] = j;
					break;
				}
			}
		}

		return correctGroups;
	}

	@Override
	protected int calOutput(Double data[], Double w[][][], Double wo[][]) {
		for (int k = 0; k < layers[0]; k++) {// 輸入層進入隱藏層
			double sum = -1 * w[0][k][0];// 處理閥值項 Threshold
			for (int m = 0; m < data.length - 1; m++) {// WmXm
				sum += data[m] * w[0][k][m + 1];
			}
			wo[0][k] = sum;
			if (isBN) {
				wo[0][k] = bn[0][k].forward(wo[0][k]);
			}
			wo[0][k] = AF(wo[0][k]);

		}
		for (int j = 1; j < layerSize; j++) {// 隱藏層內
			for (int k = 0; k < layers[j]; k++) {
				double sum = -1 * w[j][k][0];// 處理閥值
				for (int m = 0; m < layers[j - 1]; m++) {
					sum += wo[j - 1][m] * w[j][k][m + 1];
				}
				wo[j][k] = sum;

				if (isBN) {
					// if (j < layerSize - 1) {// 輸出層不做批標準化
					wo[j][k] = bn[j][k].forward(wo[j][k]);
					// }
				}
				wo[j][k] = AF(wo[j][k]);

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

	@Override
	public double[] test(LinkedList<Double[]> testdatas) {
		return calIdRate(wBest, testdatas, true);// 測試資料辨識率
	}

	@Override
	// 計算(預測辨識率,方均誤差)並回傳
	protected double[] calIdRate(Double wLocal[][][], LinkedList<Double[]> datasLocal, boolean isRdatas) {
		// 計算辨識率
		int correct = 0, output;
		Iterator<Double[]> itr = datasLocal.iterator();
		double E = 0;

		// 平均標準差更新問題

		if (isBN) {
			for (int i = 0; i < layerSize - 1; i++) {
				for (int j = 0; j < layers[i]; j++) {
					// bn[i][j].testSet();
				}
			}
		}
		// 平均標準差更新問題

		// System.out.println("-----------------------");

		while (itr.hasNext()) {
			data = itr.next();

			int correctGroup = calOutput(data, wLocal, wo);

			output = op.OPtoGroup(wo[layerSize - 1]);

			E += op.calE(wo[layerSize - 1], correctGroup);

			if (isRdatas) {// 如果要建預測結果資料集，取代輸出項
				data[dataSize - 1] = (double) groupArray[output];
			}

			if (output == correctGroup) {
				correct += 1;
			}

		}

		Irate = (double) correct / (double) datasLocal.size();// 預測辨識率
		E /= 2 * op.OPLSize() * datasLocal.size();

		double[] r = { Irate, E };
		return r;
	}

}
