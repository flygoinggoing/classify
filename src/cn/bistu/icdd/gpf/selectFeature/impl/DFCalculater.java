package cn.bistu.icdd.gpf.selectFeature.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import cn.bistu.icdd.gpf.selectFeature.IFeatureSelectionCalculater;


/**
 * 选取特征词 （DF版本）
 * @author 关鹏飞
 * 步骤：
 * 1.遍历每个语料文本，取出每个文件中的词存储到集合中（集合：TreeMap<String,Integer> ,最好实现按值进行排序）
 * 2.计算每个词的DF值
 * 2.将结果导出到txt文件中，格式为：key	DF值
 * 
 * 目前不足：需要手动更改每次取特征项范围
 * 
 * 可尝试将程序改为多线程的
 * 在这个语料库中，各个类别中的文档数不一样，如果直接使用特征词在每个类别中出现的文档数来表示该特征词和类别的相关度显然是不合适的
 */
public class DFCalculater implements IFeatureSelectionCalculater{

	/**
	 * 原始特征表
	 */
	private static HashMap<String, Integer> characterItems = new HashMap<String, Integer>();
	
	String corpusFilePath = null;     // 预处理后文件路径
	String outPath = null;		// 输出文件路径
	String exportCharacterItemsPath = null; // 输出原始特征项路径
	
	/**
	 * 输入文件路径
	 * @param corpusFilePath 预处理后语料路径
	 */
	public DFCalculater(String corpusFilePath) {
		this.corpusFilePath = corpusFilePath;
		File file = new File(corpusFilePath);
		String root = file.getParentFile().getAbsolutePath();
		outPath = root + "/抽取特征项";
		exportCharacterItemsPath = root + "/原始特征项";
	}
	
	/**
	 * 统计DF值
	 */
	@Override
	public void calcWeight() {
		File file = new File(corpusFilePath);
		if (!file.exists()) {
			System.out.println("文件不存在，请检查！退出计算！");
			return;
		}

		calcDF(file);
	}

	/**
	 * 选取特征项
	 */
	@Override
	public void extractFeature() {
		String outFilePath = outPath+"/特征项.txt";
		File outFile = new File(outFilePath);
		if (!outFile.exists()){
			File parent = outFile.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			try {
				outFile.createNewFile();
				System.out.println("导出特征词，创建文件："+outFilePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile,true),"utf-8"));
			Set<Entry<String, Integer>> set = characterItems.entrySet();
			Iterator<Entry<String, Integer>> iter = set.iterator();
			while (iter.hasNext()) {
				Entry<String, Integer> it = iter.next();
				int value = it.getValue();
				if (value > 30 && value < 300) {
					bw.write(it.getKey() +"  "+ it.getValue());
					bw.newLine();
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bw != null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
	}

	
	
	/**
	 * 初始化characterItems+计算DF
	 * @param file
	 */
	private static void calcDF(File file){
		if (file.isDirectory()){
			File[] lists = file.listFiles();
			for (File list : lists) {
				calcDF(list);
			}
		} else {
			//System.out.println("正在处理："+it.getKey()+"   "+file.getName());
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
				
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				
				line = sb.toString();
				Set<String> set = new HashSet<String>(Arrays.asList(line.split(" ")));
				sb = null;
				
				//可以在这里实现   边插入Map 边计算DF
				Iterator<String> iter = set.iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					if (characterItems.containsKey(key)) {
						characterItems.put(key,characterItems.get(key)+1);
					} else {
						characterItems.put(key,1);
					}
					
				}
				//System.out.println("处理文件个数："+(i++));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null){
					br.close();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
			}
		}
	}
	
	/**
	 * 将原始特征输出到txt文本中
	 */
	public void exportCharacterItems() {
		File file = new File(exportCharacterItemsPath+"/原始特征项.txt");

		if (!file.exists()) {
			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"utf-8"));
			Set<Entry<String, Integer>> entrySet =  characterItems.entrySet();
			Iterator<Entry<String, Integer>> iter = entrySet.iterator();
			while (iter.hasNext()){
				Entry<String, Integer> item = iter.next();
				bw.write(item.getKey()+"  "+ item.getValue().toString());
				bw.newLine();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}
