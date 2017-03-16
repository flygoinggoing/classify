package cn.bistu.icdd.gpf.preprocess;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


import edu.fudan.nlp.cn.tag.CWSTagger;


/**
 * 分词器
 * 可以封装不同的分类器
 * 
 * @author 关鹏飞
 *
 */
public class TokenizerFilter implements Filter<String,List<String>>{
	
	// FudanNLP
	private CWSTagger seg = null;
	
	// jieba
	// private JiebaSegmenter seg = new JiebaSegmenter();
	
	public TokenizerFilter() throws Exception {
		
		System.out.print("导入分词模型：");
		seg = new CWSTagger("models/seg.m");
		System.out.println("完成");
		
	}
	
	/**
	 * 分词器主程序
	 * 
	 * @param passage 待分词字符串
	 * @return 返回分词后结果
	 * 
	 */
	@Override
	public List<String> process(String passage) {
		// FudanNLP
		LinkedList<String> list = new LinkedList<String>(Arrays.asList(seg.tag2Array(passage)));
		
		// jieba分词
		// LinkedList<String> list = new LinkedList<String>(seg.sentenceProcess(passage));
		
		return list;
	}
}
