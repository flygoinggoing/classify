package cn.bistu.icdd.gpf.evaluate;

public class EvaluateTest {

	public static void main(String[] args) {
		//String filePath = "D:/NLP/每类个数类似文本分类/分类结果.txt"; 
		String filePath = "D:/NLP/分类测试/分类结果.txt"; 
		Evaluate eval = new Evaluate(filePath);
		eval.print();
	}
}
