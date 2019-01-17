package ANN1;

import java.util.Iterator;
import java.util.LinkedList;

//common factor 公因數
public class CFOP extends OP {
	LinkedList<Integer> OPL;// 記錄每個神經元輸出分幾類
	int OPLSize;
	int[][] GPA;// groupPositionArray
	double[][] GDA;// groupDivideArray
	double[][] GTA;// groupTeachArray
	
	CFOP(int GroupSize) {
		super(GroupSize);

		// 輸出層設定 找公因數
		int a = GroupSize;
		int b = a;
		int c = (int) Math.sqrt(b);
		OPL = new LinkedList<Integer>();

		for (int i = 2; i <= c; i++) {
			if (b % i == 0) {
				OPL.add(i);
				b /= i;
				c = (int) Math.sqrt(b);
				i = 1;
			}
		}
		if (b != 1) {
			OPL.add(b);
		}
		// 找公因數end
		OPLSize=OPL.size();
		
		//System.out.println(GroupSize+"  "+OPLSize);
		
		// 建分群輸出表 以4群為例
		GPA = new int[GroupSize][OPLSize];// 0,1,2,3
		GDA = new double[GroupSize][OPLSize];// 1/4,2/4,3/4,1
		GTA = new double[GroupSize][OPLSize];
		
		
		for (int i = 0; i < GroupSize; i++) {
			GPA[i] = findGroupPosition(i);
			for (int j = 0; j < OPLSize; j++) {
				// 假如第i類的第j個輸出神經元的輸出值為3,OPL.get(j)為5,0 1 2 3 4 共有5種輸出
				
				// 分割表為 1/8 3/8 5/8 7/8 1
				GDA[i][j] = (double) (GPA[i][j]*2+1) / (OPL.get(j)-1)/2 ;
				if(GDA[i][j]>1) {
					GDA[i][j]=1;
				}
				
				// 教師訊號值則為0/4 1/4 2/4 3/4 4/4
				GTA[i][j] = (double) GPA[i][j] / (OPL.get(j)-1);
			}
		}
		// 建分群輸出表 以4群為例 end

	}

	// 回傳輸出層神經元量
	public int OPLSize() {
		return OPLSize;
	}

	// 傳入輸出層結果，回傳分組結果
	public int OPtoGroup(Double[] opl) {

		// 確定預測輸出分群
		int ans[] = new int[OPLSize];// 輸出層層數

		for (int j = 0; j < ans.length; j++) {
			// 神經元輸出落點
			ans[j] = findPosition(opl[j], OPL.get(j));
		}

		int r = arrayToInt(ans);
		return r;
	}

	// Teacher Signal 傳入類別，神經元代號，回傳教師訊號
	public double TC(int g, int k) {
		return GTA[g][k];
	}

	public double calE(Double[] opl, int group) {
		double r = 0;
		
		for (int i = 0; i < OPLSize; i++) {
			r += Math.pow(opl[i] - GTA[group][i], 2);
		}
		return r;
	}

	// 找target種類在輸出層的落點 (用來紀錄種類的目標落點(教師信號))
	int[] findGroupPosition(int target) {
		int r[] = new int[OPLSize];
		Iterator<Integer> itr = OPL.iterator();
		int t = target;
		for (int i = 0; itr.hasNext(); i++) {
			int x = itr.next();
			r[i] = t % x;
			t /= x;
		}
		return r;
	}

	// 找target在range內的落點位置，回傳0 ~ range-1 (算辨識率用)
	int findPosition(double target, int range) {
		// System.out.println(target);
		double c = 1 / (double) range;

		double sum = c;
		int r = -1;
		for (int i = 0; i < range; i++, sum += c) {
			if (target <= sum) {
				r = i;
				break;
			}
		}

		return r;
	}

	// 落點ARRAY轉成輸出
	int arrayToInt(int[] a) {
		int r = 0, s = 1;

		for (int i = 0; i < a.length; i++) {
			r += a[i] * s;
			s *= OPL.get(i);
		}
		return r;
	}

}
