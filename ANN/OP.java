package ANN1;

//處理輸出層的物件
public abstract class OP {
	public int GroupSize;
	OP(int GroupSize){
		this.GroupSize=GroupSize;
	}
	//回傳輸出層神經元量
	public abstract int OPLSize();
	//傳入輸出層結果，回傳分組結果
	public abstract int OPtoGroup(Double [] opl);
	// Teacher Signal 傳入類別，神經元代號，回傳教師訊號
	public abstract double TC(int g, int k);
	//計算均方差 傳入輸出層結果，教師訊號
	public abstract double calE(Double [] opl, int group);
	
}
