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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import cn.bistu.icdd.gpf.selectFeature.IFeatureSelectionCalculater;

/**
 * 互信息法提取特征项
 * @author 关鹏飞
 *
 */
public class MICalculater implements IFeatureSelectionCalculater {

	// 定义存储形式 (原始特征项表) 格式： key:String(特征项)  value: FeatureInfo（int(DF)  List(类内DF)）
	HashMap<String, FeatureInfo> featureMap = new HashMap<String, FeatureInfo>();	
	
	// 文本总数
	int N;
	
	// 定义类别枚举类
	enum Classs {
		A,B,C,D,E,F,G,H;
		
		/**
		 * @return 返回种类个数
		 */
		public static int getSize(){
			return Classs.values().length;
		}
	}
	
	// 每类的文本数
	int[] Nj = {0,0,0,0,0,0,0,0};        // A B C D E F G H
	
	String corpusFilePath = null;     // 预处理后文件路径
	String outPath = null;		// 输出文件路径
	String exportFeaturePath = null; // 输出原始特征项路径
	
	/**
	 * 初始化
	 * @param corpusFilePath 预处理后文件的路径
	 */
	public MICalculater(String corpusFilePath){
		this.corpusFilePath = corpusFilePath;
		File corpusFile = new File(corpusFilePath);
		String root = corpusFile.getParentFile().getAbsolutePath();
		outPath = root + "/抽取特征项";
		exportFeaturePath = root + "/原始特征项";
		
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
			// 记录总文件个数
			N++;
			
			BufferedReader br = null;
			Set<String> features = null;
			try {
				// 读出文本中的特征项
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				
				line = sb.toString();
				features = new HashSet<String>(Arrays.asList(line.split(" ")));
				sb = null;
				
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
			
			// 读出文本中的特征项初始化featureMap
			Iterator<String> iter = features.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				// featureMap中不包含key时，插入新行
				if (!featureMap.containsKey(key)) {
					FeatureInfo initValue = new FeatureInfo();
					featureMap.put(key, initValue);
				}
				// 更新DF与每类的DF
				FeatureInfo value = featureMap.get(key);
				value.setDfIncrease();  // DF++
				int index = classs.ordinal();
				value.setDfOfEachClassIncrease(index); // 每类的DF++
			}
		}
	}
	
	/**
	 * 计算MI
	 * 核心算法
	 */
	@Override
	public void calcWeight() {
		Set<Entry<String, FeatureInfo>> featureMapSet = featureMap.entrySet();
		Iterator<Entry<String, FeatureInfo>> featureMapIter = featureMapSet.iterator();
		while (featureMapIter.hasNext()) {
			Entry<String, FeatureInfo> featureEntry = featureMapIter.next();
			FeatureInfo fInfo = featureEntry.getValue();
			// 每类分别计算
			for (int i = 0 ; i < Classs.getSize(); i++ ) {
				int A = fInfo.getDfOfEachClass(i);
				int B = fInfo.getDF() - A;
				int C = Nj[i] - A;
				Double pc = (Nj[i] * 1.0)/N;   // P(Cj)
				
				Double I_i = pc * Math.log(( (A*N) * 1.0) / ( (A+C) * (A+B) ));
				fInfo.setMiOfeachClass(i, I_i);
			}
		}
	}


	/**
	 * 提取特征项
	 * 按排名输出
	 */
	@Override
	public void extractFeature() {
		Double threshold = 0.2; // 阈值
		
		File file = new File(outPath+"/抽取特征项.txt");

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
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			List<Entry<String, FeatureInfo>> featureList = sort();
			
			for (Entry<String, FeatureInfo> entry : featureList) {
				Double maxMi = entry.getValue().getMaxMi();
				if (maxMi > threshold) {
					
					bw.write(entry.getKey() + " " + entry.getValue().getDF() + " " + entry.getValue().getDfOfEachClass(0)+ " "+ entry.getValue().getDfOfEachClass(1)+ " "+ entry.getValue().getDfOfEachClass(2)+ " "+ entry.getValue().getDfOfEachClass(3)+ " "+ entry.getValue().getDfOfEachClass(4)+ " "+ entry.getValue().getDfOfEachClass(5)+ " "+ entry.getValue().getDfOfEachClass(6)+ " " + entry.getValue().getMaxMi());
					bw.newLine();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 输出原始特征项，输出格式：特征项 DF MI
	 * 按MI值大小排序
	 */
	public void printOriginFeature(){
		File file = new File(exportFeaturePath+"/原始特征项.txt");

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
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			List<Entry<String, FeatureInfo>> featureList = sort();
			
			for (Entry<String, FeatureInfo> entry : featureList) {
				bw.write(entry.getKey() + " " + entry.getValue().getDF() + " " + entry.getValue().getDfOfEachClass(0)+ " "+ entry.getValue().getDfOfEachClass(1)+ " "+ entry.getValue().getDfOfEachClass(2)+ " "+ entry.getValue().getDfOfEachClass(3)+ " "+ entry.getValue().getDfOfEachClass(4)+ " "+ entry.getValue().getDfOfEachClass(5)+ " "+ entry.getValue().getDfOfEachClass(6)+ " " + entry.getValue().getMaxMi());
				bw.newLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 排序（有大到小）
	 * @return 返回list集合
	 */
	private List<Entry<String, FeatureInfo>> sort() {
		List<Entry<String, FeatureInfo>> featureList = new ArrayList<Entry<String, FeatureInfo>>(featureMap.entrySet());
		Collections.sort(featureList, new Comparator<Entry<String, FeatureInfo>>() {
			@Override
			public int compare(Entry<String, FeatureInfo> o1, Entry<String, FeatureInfo> o2) {
				/*if ((o1.getValue().getMaxMi() - o2.getValue().getMaxMi()) > 0) {
					return 1;
					
				} else {
					return -1;
				}*/
				
				// 加负号从大到小排序
				return -(o1.getValue().getMaxMi().compareTo(o2.getValue().getMaxMi()));
			}
		});
		return featureList;
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

	/**
	 * 特征值对应的信息
	 * 
	 * @author 关鹏飞
	 */
	class FeatureInfo {
		int df;    // 文档频率
		ArrayList<Integer> dfOfEachClass;  // 记录每类中的df值
		ArrayList<Double> miOfEachClass;   // 记录互信息
		
		public FeatureInfo(){
			df = 0;
			dfOfEachClass = new ArrayList<Integer>();
			miOfEachClass = new ArrayList<Double>();
			for (int i = 0; i <= Classs.values().length; i++ ) {
				dfOfEachClass.add(0);
				miOfEachClass.add(0.0);
			}
		}
		
		/**
		 * @return 返回DF
		 */
		public int getDF(){
			return df;
		}
		
		/**
		 * DF++
		 * @param value 值
		 */
		public void setDfIncrease(){
			df++;
		}
		
		/**
		 * 返回对应位置的类内df
		 * @param index 序号
		 * @return 类内df
		 */
		public int getDfOfEachClass(int index){
			return dfOfEachClass.get(index);
		}
		
		/**
		 * 返回MI （最大值） 
		 * 
		 * @return
		 */
		public Double getMaxMi(){
			return Collections.max(this.miOfEachClass);
		}
		
		/**
		 * 类内df值 ++
		 * @param index 序号
		 */
		public void setDfOfEachClassIncrease(int index){
			dfOfEachClass.set(index, getDfOfEachClass(index) + 1);
		}
		
		/**
		 * 设置MI值
		 * @param index 序号
		 * @param value 值
		 */
		public void setMiOfeachClass(int index, Double value){
			miOfEachClass.set(index, value);
		}
		
		
	}
}

