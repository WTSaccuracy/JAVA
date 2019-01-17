package ANN1;

import java.io.*;
import java.util.*;

import javafx.scene.chart.*;

public class DataSet {
	File file;
	LinkedList<Double[]> datas, traindatas, testdatas, Rdatas;
	// 總資料集, 訓練資料集 , 測試資料集 ,預測結果資料集
	
	
	Double data[];// 單筆資料
	int dataSize,dataNum;
	LinkedList<Integer> group;// 每類代表數字
	
	int groupArray[];
	
	DataSet(File file) {
		this.file = file;

		// 處理檔案
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			datas = new LinkedList<Double[]>();
			traindatas = new LinkedList<Double[]>();
			testdatas = new LinkedList<Double[]>();
			dataSize = 0;
			group = new LinkedList<Integer>();

			// 對每個data做處理
			while (br.ready()) {
				// split
				String[] str = br.readLine().trim().split("(\\s+|,|;)");
				if (dataSize == 0) {
					dataSize = str.length;
				}
				// split end

				// to double array
				Double[] data = new Double[dataSize];
				for (int i = 0; i < dataSize; i++) {
					data[i] = Double.parseDouble(str[i]);
				}
				// to double array end

				// 分群(確定有幾類)
				Iterator<Integer> gitr = group.iterator();
				boolean isNewGroup = true;// 判斷此種類是否存在
				while (gitr.hasNext()) {
					Integer x = new Integer(gitr.next());
					if (data[dataSize - 1].intValue() == x) {// 已有族群
						isNewGroup = false;
						break;
					}
				}
				if (isNewGroup) {// 增加新分類
					group.add(data[dataSize - 1].intValue());
				}
				// 分群end

				datas.add(data);
			}
			// 對每個data做處理 end
		} catch (Exception ex) {
			System.out.println(ex);
			System.out.println("loadfile error");
		}
		// 處理檔案 end

		dataNum =datas.size();
		// 依類別分開
		groupArray = new int[group.size()];// groupArray[0]=1 => 第0類代表數字為1
		Iterator<Integer> gitr = group.iterator();
		for (int i = 0; gitr.hasNext(); i++) {
			groupArray[i] = gitr.next();
		}
		Arrays.sort(groupArray);

		LinkedList<LinkedList<Double[]>> datasByGroup = new LinkedList<LinkedList<Double[]>>();// 資料集依種類分開

		for (int i = 0; i < group.size(); i++) {
			datasByGroup.add(new LinkedList<Double[]>());
		}

		Iterator<Double[]> dItr = datas.iterator();
		while (dItr.hasNext()) {
			Double[] data = dItr.next();
			for (int i = 0; i < groupArray.length; i++) {
				if (data[dataSize - 1] == groupArray[i]) {
					datasByGroup.get(i).add(data);
					break;
				}
			}
		}
		// 依類別分開 end

		// 分出訓練資料和測試資料
		if (dataNum < 30) {
			traindatas = datas;
			testdatas = datas;
		} else {
			for (int i = 0; i < group.size(); i++) {// 每個group
				dItr = datasByGroup.get(i).iterator();

				while (dItr.hasNext()) {// 將該group分訓練和測試
					Double[] data = dItr.next();
					if (traindatas.size() <= testdatas.size() * 2) {
						traindatas.add(data);
					} else {
						testdatas.add(data);
					}
				}
			}
		}
		// 分出訓練資料和測試資料 end

	}
	// 繪圖
	public void draw(ScatterChart<Number, Number> sc, LinkedList<Double[]> datas) {
		XYChart.Series series[] = new XYChart.Series[group.size()];

		for (int i = 0; i < group.size(); i++) {
			series[i] = new XYChart.Series();
			series[i].setName(Integer.toString(groupArray[i]));
		}

		// 把所有資料依種類存入Series
		Iterator<Double[]> itr = datas.iterator();
		while (itr.hasNext()) {
			Double[]data = itr.next();
			for (int i = 0; i < group.size(); i++) {
				if (groupArray[i] == data[dataSize - 1].intValue()) {
					series[i].getData().add(new XYChart.Data(data[0], data[1]));
					break;
				}
			}
		}

		for (int i = 0; i < group.size(); i++) {
			sc.getData().add(series[i]);
		}
	}
	LinkedList<Double[]> datasClone(LinkedList<Double[]> a) {
		
		
		LinkedList<Double[]> b = new LinkedList<Double[]>();
		Iterator<Double[]> itr =a.iterator();
		while(itr.hasNext()) {
			Double[] x =itr.next();
			Double[] y =new Double[x.length];
			for(int i=0;i<x.length;i++) {
				y[i]=x[i];
			}
			b.add(y);
		}
		
		return b;
	}
}
