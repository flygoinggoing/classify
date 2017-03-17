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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

/**
 * 同义词归一改进
 * 使用HashMap存
 * 比如说   它们 她们  他们是同义词
 * 这样建表：他们 它们
 * 		       她们 它们
 * 	value位置存每行的第一个词
 * 	占用空间也不多  不会超过原表的二倍
 * 
 * @author 关鹏飞
 *
 */
public class NormalizationHashFilter implements Filter<List<String>, String>{

	/*
	 * 同义词表
	 */
	private static HashMap<String,String> sysnonymHash = new HashMap<String, String>();
	
	// 实例
	private static NormalizationHashFilter instance = null;
	
	/**
	 *  初始化（读入同义词表）
	 */
	private NormalizationHashFilter(){
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
				ArrayList<String> list =new ArrayList<String>(Arrays.asList(line.split(" ")));
				String firstWord = list.get(0);
				for (int i = 1; i < list.size(); i++) {
					String word = list.get(i);
					sysnonymHash.put(word,firstWord);
				}
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
	public static NormalizationHashFilter getInstance() {
		
		// 加锁
		synchronized (StopWordFilter.class) {
			if (instance == null) {
				instance = new NormalizationHashFilter();
			}
		}
		
		return instance;
	}
	
	@Override
	public String process(List<String> passage) {

		ListIterator<String> iter = passage.listIterator();
		while (iter.hasNext()) {
			String word = iter.next();
			if (sysnonymHash.containsKey(word)) {
				iter.set(sysnonymHash.get(word));
			}
		}

		return passage.toString().replace("[", "").replace("]", " ").replace(",", "");
	}

}
