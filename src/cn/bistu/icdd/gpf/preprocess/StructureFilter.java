package cn.bistu.icdd.gpf.preprocess;

/**
 * 文档结构过滤器
 * 
 * 对不同格式，不同形式的文件进行解析。对各自的文件结构进行解析，过滤出基本的文本元信息。
 * 对于最基本的文本文件这步不需要作处理，但是其他文件就要按照各自的方式进行处理。
 * 如：XML 格式的文档需要过滤掉标签，或按照自己定义的结构进行解析。 
 * 
 * @author 关鹏飞
 *
 */
public class StructureFilter implements Filter<String,String>{
	
	/**
	 * 文档结构过滤器主程序
	 * 
	 * @param passage 文档内容
	 * @return 解析后的结果
	 */
	@Override
	public String process(String passage) {
		return null;
	}
}
