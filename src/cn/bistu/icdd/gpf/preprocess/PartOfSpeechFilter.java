package cn.bistu.icdd.gpf.preprocess;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.fudan.nlp.cn.tag.POSTagger;

/**
 * 
 * 1.词性过滤器   (分词——>标注——>词性过滤)
 * part of speech (POS)
 * 
 * 在支持词性处理的前提下，可以对文档分类贡献小的一些词进行过滤。（如形容词、副词在句子中表达类别信息的能力很差，可以滤出这些词）
 * 在词性处理中过滤掉: a-形容词、c-连词、d-副词、e-叹词、m-数词、o-拟声词、p-介词、t-时间词、w-标点符号、x-非语素字、y-语气词。
 * 保留：b-区别词、f、方位词、g-语素、h-前接成分、i-成语、j-简称略语、k-后接成分、l-习用语、n-名词、q-量词、r-代词、s-处所词、u-助词、v-动词、z-状态词 
 * 
 * @author 关鹏飞
 *
 */
public class PartOfSpeechFilter implements Filter<String, List<String>>{
	
	private POSTagger pos = null;
		
	public PartOfSpeechFilter() throws Exception {
		System.out.print("导入分词、标注模型：");
		pos = new POSTagger("models/seg.m","models/pos.m");
		pos.SetTagType("en");
		System.out.println("完成");
	}
	
	/**
	 * 词性过滤器主函数
	 * 
	 * @param passage 待处理字符串
	 * @return 返回处理后的集合
	 */
	@Override
	public List<String> process(String passage) {
		List<String> listWord = null;   // 存词
		try {
			String[][] str = pos.tag2Array(passage);
			List<String> words = Arrays.asList(str[0]);
			List<String> tags = Arrays.asList(str[1]);
			
			listWord = new LinkedList<String>(words);            // 存词
			List<String> listTag = new LinkedList<String>(tags); // 存标注
			
			// 应改为迭代器访问
			for (int i = 0; i < listTag.size(); i++) {
				/*
				* FudanNLP中的词性标注
				* 在词性处理中过滤掉: JJ-形容词、CC-连词、AD-副词、IJ-叹词、CD-数词、ON-拟声词、P-介词、NT-时间词、PU-标点符号、？？-非语素字、SP-语气词。
				* 保留：b-区别词、f、方位词、g-语素、h-前接成分、i-成语、j-简称略语、k-后接成分、l-习用语、n-名词、q-量词、r-代词、s-处所词、u-助词、v-动词、z-状态词 
				*/
				String tag = listTag.get(i);
				// 注意比价的顺序 ，把常出现放在前边
				if ("PU".equals(tag) || "JJ".equals(tag) || "CC".equals(tag) || "AD".equals(tag) || "CD".equals(tag) 
						|| "SP".equals(tag) || "P".equals(tag) || "ON".equals(tag) || "NT".equals(tag) || "IJ".equals(tag)) {
					listTag.remove(i);
					listWord.remove(i);
					i--;
				} else if ("M".equals(tag)) {

					/*
					 * 因为FudanNLP把 如："100个"记为量词  系统只要 “个”  
					 * 在此单独处理一下   只要单位 
					 * 大部分字符的单位是一位的所以取最后一位
					 */
					String word = listWord.get(i);
					int length = word.length();
					listWord.set(i, word.substring(length-1, length));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listWord;
	}
}
