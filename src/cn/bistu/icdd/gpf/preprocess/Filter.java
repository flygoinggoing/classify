package cn.bistu.icdd.gpf.preprocess;

/**
 * 过滤器接口
 * 
 * @author 关鹏飞
 *
 * @param <T,R>  T:接受参数类型   R:返回数据类型
 */
public interface Filter<P,R> {
	R process(P passage);
}
