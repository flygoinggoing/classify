package cn.bistu.icdd.gpf.selectFeature;

/**
 * 特征选取计算器接口
 * @author 关鹏飞
 *
 */
public interface IFeatureSelectionCalculater {
	public void calcWeight();        // 计算原始特征权重
	public void extractFeature();    // 抽取特征项
}
