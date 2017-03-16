package cn.bistu.icdd.gpf.classifier;

/**
 * 
 * @author 关鹏飞
 *
 */
public interface Classifier {
	
	/**
	 * 训练
	 */
	public void train();
	
	/**
	 * 分类
	 */
	public void processClassifier();
}
