package cn.bistu.icdd.gpf.selectFeature.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.bistu.icdd.gpf.selectFeature.IFeatureSelectionCalculater;

/**
 * 互信息法提取特征项
 * @author 关鹏飞
 *
 */
public class MICalculater implements IFeatureSelectionCalculater {

	// 定义存储形式 (原始特征项表) 格式： key:String(特征项)  value: List(DF  类内DF)
	HashMap<String, ArrayList<Integer>> featureMap = new HashMap<String, ArrayList<Integer>>();	
	
	// 文本总数
	int N;
	
	// 定义类别枚举类
	enum Classs {
		A,B,C,D,E,F,G,H;
	}
	
	// 每类的文本数
	int[] Nj = {0,0,0,0,0,0,0,0};        // A B C D E F G H
	
	String corpusFilePath = null;     // 预处理后文件路径
	String outPath = null;		// 输出文件路径
	String exportCharacterItemsPath = null; // 输出原始特征项路径
	
	/**
	 * 初始化
	 * @param corpusFilePath 预处理后文件的路径
	 */
	public MICalculater(String corpusFilePath){
		this.corpusFilePath = corpusFilePath;
		File corpusFile = new File(corpusFilePath);
		String root = corpusFile.getParentFile().getAbsolutePath();
		outPath = root + "/抽取特征项";
		exportCharacterItemsPath = root + "/原始特征项";
		
		InitFeatureMap(corpusFile);
	}
	
	/**
	 * 初始化 原始特征项表
	 * @param file 文件
	 */
	private void InitFeatureMap(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				InitFeatureMap(f);
			}
		} else {
			// 记录每类文件个数
			Classs classs= addNj(file.getName());
			// 返回空 表示文件不在提前定义的类范围内
			if (classs == null) {
				return;
			}
			
			BufferedReader br = null;
			try {
				// 读出文本中的特征项
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				
				line = sb.toString();
				Set<String> feature = new HashSet<String>(Arrays.asList(line.split(" ")));
				sb = null;
				
				// 读出文本中的特征项初始化featureMap
				Iterator<String> iter = feature.iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					// featureMap中不包含key时，插入新行
					if (!featureMap.containsKey(key)) {
						ArrayList<Integer> initValue = new ArrayList<Integer>();
						for (int i = 0; i <= Classs.values().length; i++ ) {
							initValue.add(0);
						}
						featureMap.put(key, initValue);
					}
					// 更新DF与每类的DF
					List<Integer> value = featureMap.get(key);
					value.set(0, value.get(0)+1);  //DF++
					int index = classs.ordinal();
					value.set(index, value.get(index)+1); // 每类的DF++
				}
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
	}
	
	/**
	 * 计算MI
	 */
	@Override
	public void calcWeight() {
		
	}


	/**
	 * 提取特征项
	 * 按排名输出
	 */
	@Override
	public void extractFeature() {
		
	}
	
	/**
	 * 输出原始特征项
	 */
	public void printOriginFeature(){
		
	}
	
	/**
	 * 记录每类的文本个数
	 * @param fileName 文件名
	 * @return 返回文件的类别，如文件不在提前定义的类型中返回空
	 * 
	 */
	private Classs addNj(String fileName){
		char first =  fileName.charAt(0);
		
		switch (first) {
			case 'A':
				Nj[Classs.A.ordinal()] ++;
				return Classs.A;
			case 'B':
				Nj[Classs.B.ordinal()] ++;
				return Classs.B;
			case 'C':
				Nj[Classs.C.ordinal()] ++;
				return Classs.C;
			case 'D':
				Nj[Classs.D.ordinal()] ++;
				return Classs.D;
			case 'E':
				Nj[Classs.E.ordinal()] ++;
				return Classs.E;
			case 'F':
				Nj[Classs.F.ordinal()] ++;
				return Classs.F;
			case 'G':
				Nj[Classs.G.ordinal()] ++;
				return Classs.G;
			case 'H':
				Nj[Classs.H.ordinal()] ++;
				return Classs.H;
			default:
				return null;
		}
	}

}
