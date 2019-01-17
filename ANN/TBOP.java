package ANN1;
//twobit
public class TBOP extends OP{
	
	int GBA[][];//groupBinaryArray[][]
	int OPLSize;
	
	TBOP(int GroupSize){
		super(GroupSize);
		OPLSize=(int) Math.ceil(Math.log(GroupSize) / Math.log(2));
		// 建2bit的分群輸出
		GBA = new int[GroupSize][OPLSize];
		for (int i = 0; i < GroupSize; i++) {
			GBA[i] = toBinaryArray(i);
		}
		
	}
	
	//回傳輸出層神經元量
	public int OPLSize() {
		return OPLSize;
	}
	//傳入輸出層結果，回傳分組結果
	public int OPtoGroup(Double [] opl) {
		int r;
		int ans[] = new int[OPLSize];// 輸出層層數
		
		for (int j = 0; j < ans.length; j++) {
			if (opl[j] < 0.5) {
				ans[j] = 0;
			} else {
				ans[j] = 1;
			}
		}
		
		r = BinaryArraytoInt(ans);
		if (r > GroupSize - 1) {
			r = GroupSize - 1;
		}
		return r;
	}
	// Teacher Signal 傳入類別，神經元代號，回傳教師訊號
	public double TC(int g, int k) {
		int r[] = toBinaryArray(g);
		return r[k];
	}
	//計算均方差 傳入輸出層結果，教師訊號
	public double calE(Double [] opl, int group) {
		double r = 0;
		
		for (int i = 0; i < OPLSize; i++) {
			r += Math.pow(opl[i] - GBA[group][i], 2);
		}
		return r;
	}
	
	int[] toBinaryArray(int x) {

		// 轉2進位
		String s = Integer.toBinaryString(x);
		// 空位補0
		while (s.length() < OPLSize) {
			s = "0" + s;
		}
		// 轉整數ARRAY
		char groupCharArray[] = s.toCharArray();
		int r[] = new int[groupCharArray.length];
		for (int j = 0; j < groupCharArray.length; j++) {
			r[j] = groupCharArray[j] - 48;
		}

		return r.clone();// r = GBA
	}

	int BinaryArraytoInt(int[] x) {
		int r = 0;
		for (int i = 0; i < x.length; i++) {
			r <<= 1;// 用SHIFT
			r += x[i];
		}

		return r;
	}
}
