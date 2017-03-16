package cn.bistu.icdd.gpf.preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * 停用词过滤器（单例模式）
 * 作用：去除停用词
 * 
 * （在绝大多数文档中都会出现，并且对分类不起作用的词。如：“是”、“的”等）。
 * 它从一个停用词典中读入停用词，在过滤过程中，对停用词进行匹配并过滤。 
 * @author 关鹏飞
 *
 */
public class StopWordFilter implements Filter<List<String>,List<String>>{
	private static LinkedList<String> stopWords = new LinkedList<String>();   //存停用词
	
	private static StopWordFilter instance = null;   // 存单例的实例
	
	/**
	 * 初始化（读入停用词表）
	 */
	private StopWordFilter() {
		
		System.out.print("停用词装载：");
		
		BufferedReader br = null;
		Properties pro = new Properties();
		try {
			// 获取停用词典路径
			pro.load(StopWordFilter.class.getResourceAsStream("/config.properties"));
			String stopWordPath = pro.getProperty("stopWordPath");
			
			br = new BufferedReader(new FileReader(new File(stopWordPath)));
			// br = new BufferedReader(new FileReader(new File("models/stop-word.txt")));
			String word = null;
			while ((word = br.readLine())!=null) {
				stopWords.add(word);
			}
			System.out.println("装载完成");
			
			// 调整集合数组长度为当前元素的个数   ArrayList时可用
			// stopWords.trimToSize();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取实例
	 * @return 返回实例
	 */
	public static StopWordFilter getInstance() {
		
		// 加锁
		synchronized (StopWordFilter.class) {
			if (instance == null) {
				instance = new StopWordFilter();
			}
		}
		
		return instance;
	}
	
	/**
	 * 过滤器主方法
	 */
	@Override
	public List<String> process(List<String> passage) {
		
		/*
		 * 当大部分文件中的词次数——远大于——停用词表时用本循环
		 
		for (String word : stopWords) {
			Iterator<String> iter = passage.iterator();
			while (iter.hasNext()) {
				if (word.equals(iter.next())) {
					iter.remove();
				}
			}
			
		}
		*/
		/*
		 * 当大部分文件中的词次数——小于等于——停用词表时用本循环
		 * 而且还有跳出循环操作
		 */
		Iterator<String> iter = passage.iterator();
		while (iter.hasNext()) {
			String word = iter.next();
			for (String stopword : stopWords) {
				if (stopword.equals(word)) {
					iter.remove();
					break;
				}
			}
		}
		
		return passage;
	}
}
