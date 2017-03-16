package cn.bistu.icdd.gpf.evaluate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 文本分类性能评测类
 * @author 关鹏飞
 *
 */
public class Evaluate {
	
	// 表示分类器将输入文本正确地分类到某个类别的个数 
	int a =0;
	// 表示分类器将输入文本错误地分到某个类别里
	int b = 0;
	// 表示分类器将输入文本错误地排除在某个类别之外的个数
	int c = 0;
	
	// 每类中的分配情况  格式：   类别   分类后每类总数  分对个数
	Map<String,ArrayList<Integer>> classMap = new HashMap<String,ArrayList<Integer>>();
	
	// 测试数据的分配情况   格式：  测试类别   每类测试总数  分对个数   
	Map<String,ArrayList<Integer>> TestMap = new HashMap<String,ArrayList<Integer>>();
	
	public Evaluate(String filePath){
		BufferedReader br = null;
		try {
			
			br=  new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] str = line.split("---");
				String judgedClass = str[0];
				String trueClass = str[1].substring(0, str[1].indexOf(" ("));
				
				// 类别   类中总数
				if (classMap.containsKey(judgedClass)) {
					List<Integer> list = classMap.get(judgedClass);
					list.set(0, list.get(0)+1);
				} else {
					ArrayList<Integer> list = new ArrayList<Integer>();
					list.add(0);
					list.add(0);
					classMap.put(judgedClass, list);
				}
				
				//  测试类别   测试类总数
				if (TestMap.containsKey(trueClass)) {
					List<Integer> list = TestMap.get(trueClass);
					list.set(0, list.get(0)+1);
				} else {
					ArrayList<Integer> list = new ArrayList<Integer>();
					list.add(0);
					list.add(0);
					TestMap.put(trueClass, list);
				}
				
				// 每个Map中分对的个数
				if (judgedClass.equals(trueClass)) {
					List<Integer> listClass = classMap.get(judgedClass);
					listClass.set(1, listClass.get(1)+1);
					
					List<Integer> listTest = TestMap.get(trueClass);
					listTest.set(1, listTest.get(1)+1);
				} 
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	
	public int getA(String type) {
		List<Integer> list = TestMap.get(type);
		return list.get(1);
	}

	public int getB(String type) {
		List<Integer> list = classMap.get(type);
		// 总数 - 分对个数
		return list.get(0)-list.get(1);
	}

	public int getC(String type) {
		List<Integer> list = TestMap.get(type);
		// 总数 - 分对个数
		return list.get(0)-list.get(1);
	}

	/**
	 * 计算正确率
	 * @return 返回正确率
	 */
	public Double getPrecision(String type){
		int a = getA(type);
		int b = getB(type);
		return (a*1.0)/(a+b);
		
	}
	
	/**
	 * 计算回归率
	 * @return 返回回归率
	 */
	public Double getRecall(String type){
		int a = getA(type);
		int c = getC(type);
		return (a*1.0)/(a+c);
	}
	
	/**
	 * 计算F1值
	 * @param p 正确率
	 * @param r 回归率
	 * @return 返回F1值
	 */
	public Double getF1(Double p, Double r){
		return (2 * p * r)/(p + r);
	}
	
	/**
	 * 打印测评结果
	 */
	public void print(){
		Set<String> typeSet = classMap.keySet();
		Iterator<String> iter = typeSet.iterator();
		
		// 每类的测评值
		while (iter.hasNext()) {
			String type = iter.next();
			Double p = getPrecision(type);
			Double r = getRecall(type);
			Double f1 = getF1(p,r);
			System.out.println(type + " 的正确率：" + p);
			System.out.println(type + " 的召回率：" + r);
			System.out.println(type + " 的F1值：" + f1);
		}
	}
}
