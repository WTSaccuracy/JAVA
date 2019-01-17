package ANN1;

//one to one
public class OTOOP extends OP {
	int OPLSize;
	OTOOP(int GroupSize) {
		super(GroupSize);
		OPLSize=GroupSize;
	}

	// 回傳輸出層神經元量
	public int OPLSize() {
		return OPLSize;
	}

	// 傳入輸出層結果，回傳分組結果
	public int OPtoGroup(Double[] opl) {
		int r = 0;
		double max = opl[0];
		
		for(int i=1;i<OPLSize;i++) {
			//System.out.println(opl[i]);
			if(opl[i]>max) {
				max=opl[i];
				r=i;
			}
		}
		return r;
	}

	// Teacher Signal 傳入類別，神經元代號，回傳教師訊號
	public double TC(int g, int k) {
		double r=0.0;
		if(g==k) {
			r=1.0;
			//System.out.println("sddfs");
		}
		return r;
	}

	// 計算均方差 傳入輸出層結果，教師訊號
	public double calE(Double[] opl, int group) {
		double r = 0.0;
		for (int i = 0; i < OPLSize; i++) {
			if(i == group) {
				r += Math.pow(opl[i] - 1, 2);
			}else {
				r += Math.pow(opl[i] - 0, 2);
			}
		}
		return r;
	}
}
