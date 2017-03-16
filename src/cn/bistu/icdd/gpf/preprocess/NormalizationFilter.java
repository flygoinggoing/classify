package cn.bistu.icdd.gpf.preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64.Decoder;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

/**
 * 归一化过滤器(单例模式)
 * 在语义上进行处理
 * 
 * 1.同义词过滤器（SynonymFilter）
 * 作用：合并同义词
 * 
 * 2.在英文中，经常要词干提取。就是同一个词的不同变形表示为统一的格式。（此处暂时不考虑英文）
 * 如：cat 和 cats 应该统一表示为 cat。
 * 
 * @author 关鹏飞
 *
 */
public class NormalizationFilter implements Filter<List<String>, String>{
	/**
	 *  同义词表
	 */
	private static LinkedList<LinkedList<String>> sysnonym = new LinkedList<LinkedList<String>>();
	
	// 实例
	private static NormalizationFilter instance = null;
	
	/**
	 *  初始化（读入同义词表）
	 */
	private NormalizationFilter(){
		System.out.print("开始加载同义词——");
		BufferedReader br = null;
		Properties pro = new Properties();
		try {
			// 获取停用词典路径
			pro.load(new InputStreamReader(StopWordFilter.class.getResourceAsStream("/config.properties")));
			String synonymPath = pro.getProperty("synonymPath");
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(synonymPath)),"utf-8"));
			String line = null;
			while ((line = br.readLine())!=null) {
				LinkedList<String> list =new LinkedList<String>(Arrays.asList(line.split(" ")));
				sysnonym.add(list);
			}
			
			System.out.println("完成");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
	public static NormalizationFilter getInstance() {
		
		// 加锁
		synchronized (StopWordFilter.class) {
			if (instance == null) {
				instance = new NormalizationFilter();
			}
		}
		
		return instance;
	}
	
	/**
	 * 归一化主函数
	 * @param passage 待处理的集合
	 * @return 返回处理好的字符串
	 */
	@Override
	public String process(List<String> passage) {
		
		/*
		 * 
		 */
		/*
		ListIterator<String> iter = passage.listIterator();
		OUT:
		while (iter.hasNext()) {
			String word = iter.next();
			for (LinkedList<String> list : sysnonym) {
				// 从每行的第二个开始 ，有一样的用行首词代替
				for (String str : list) {
					if (str.equals(word)) {
						iter.set(list.get(0));
						continue OUT;  // 找到后跳到最外层循环
					}
				}
			}
		}
		*/
		
		
		ListIterator<String> iter;
		for (LinkedList<String> list : sysnonym) {
			String firstWord = list.get(0);
			// 从每行的第二个开始 ，有一样的用行首词代替
			for (String str : list) {
				iter = passage.listIterator();
				while (iter.hasNext()) {
					String word = iter.next();
					if (str.equals(word)) {
						iter.set(firstWord);
					}
				}
			}
		}
		
		
		return passage.toString().replace("[", "").replace("]", " ").replace(",", "");
	}

}
