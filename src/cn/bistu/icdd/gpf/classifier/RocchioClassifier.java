package cn.bistu.icdd.gpf.classifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.bistu.icdd.gpf.preprocess.Preprocessor;
import cn.bistu.icdd.gpf.textRepresent.TFIDFCalculater;

/**
 * Rocchio分类器
 * 
 * @author 关鹏飞
 *
 */
public class RocchioClassifier implements Classifier {

	private static Map<String , List<Double>> avgW = new HashMap<String , List<Double>>(); //中心向量
	private static Map<String , List<Double>> wsAll = null;  // 训练集的向量
	private static Map<String , List<Double>> wsTest = null;   // 测试集的向量
	
	String corpusFilePath = null;// 预处理后的语料
	String testFilePath = null; // 测试文件路径
	
	BufferedWriter bw ;
	
	// 总文档数
	private int N = 0;
	
	// 预测文本的编码
	private String charSet;

	/**
	 * 初始化
	 * @param corpusFilePath 预处理后的语料
	 * @param inPath 特征项路径
	 * @param testFilePath 测试文件路径
	 * @param charSet 预测文本编码
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public RocchioClassifier(String corpusFilePath, String testFilePath, String charSet) throws UnsupportedEncodingException, FileNotFoundException {
		this.charSet = charSet;
		this.corpusFilePath = corpusFilePath;
		this.testFilePath = testFilePath;
		String rootPath = corpusFilePath.substring(0,corpusFilePath.lastIndexOf("/"));   //结果输出路径
		
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(rootPath+"/分类结果.txt")),"utf-8"));
		
		File file = new File(corpusFilePath);
		countFileNum(file);
	}
	
	private void countFileNum(File file) {
		
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				countFileNum(f);
			}
		} else {
			N++;
		}
		
	}
	
	/**
	 * 初始化 (默认utf-8编码)
	 * @param corpusFilePath 预处理后的语料
	 * @param inPath 特征项路径
	 * @param testFilePath 测试文件路径
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public RocchioClassifier(String corpusFilePath, String testFilePath) throws UnsupportedEncodingException, FileNotFoundException{
		this(corpusFilePath,testFilePath,"utf-8");
	}

	/**
	 * 训练
	 */
	@Override
	public void train() {
		TFIDFCalculater calcTF_IDF = new TFIDFCalculater(corpusFilePath, N);
		wsAll = calcTF_IDF.process();
		
		if (wsAll == null) {
			System.out.println("wsAll为空");
		}
		
		// 这就叫训练模型  
		// 在文件中读一条就计算一条
		// 求中心向量
		calcCenterVector();
	}

	/**
	 * 分类
	 */
	@Override
	public void processClassifier() {
		// 1.文件预处理
		try {
			Preprocessor predeal = new Preprocessor(charSet);
			predeal.start(testFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File file = new File(testFilePath);
		// 生成输出文件路径
		String testcorpusFilePath = file.getParentFile().getAbsolutePath() + "/" + file.getName()+"预处理";
		
		// 2.计算TF-idf值
		TFIDFCalculater tfidf = new TFIDFCalculater(testcorpusFilePath , N);
		wsTest = tfidf.process();

		
		// 3.计算相似度
		System.out.println("开始计算相似度，得出分类：");
		Set<Entry<String , List<Double>>> wsTestSet = wsTest.entrySet(); /// 顺序问题
		Iterator<Entry<String , List<Double>>>  wsTestIter = wsTestSet.iterator();
		
		int N = 0;  //计算测试文件总数
		int right = 0; // 计算正确个数
		int wrong = 0;  // 计算错误个数
		
		while (wsTestIter.hasNext()) {
			Entry<String , List<Double>> wsTestIt = wsTestIter.next();
			List<Double> ws = wsTestIt.getValue();
			String type = similar(ws);
			
			// 输出分类  算一个输出一个
			String fileName = wsTestIt.getKey();
			//System.out.println(fileName+" "+type);
			
			try {
				bw.write(type + "---" + fileName);
				bw.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			// 计算正确率
			N++;
			if (type.equals(fileName.substring(0, fileName.indexOf(" (")))) {
			//if (type.equals(fileName.substring(0, fileName.indexOf("-")))) {
				right++;
			}else {
				wrong++;
			}
			
			
		}
		
		
		System.out.println("测试文件总数为：" + N);
		System.out.println("正确个数：" + right);
		System.out.println("错误个数：" + wrong);
		System.out.println("正确率为：" + ((right*1.0)/N));
		

		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 计算中心向量
	 * 求每类的平均值导入avgW
	 */
	private void calcCenterVector() {
		Set<Entry<String , List<Double>>> set = wsAll.entrySet();
		Iterator<Entry<String , List<Double>>> iter = set.iterator();
		while (iter.hasNext()) {
			Entry<String , List<Double>> it = iter.next();
			String type = it.getKey().substring(0, it.getKey().indexOf(" ("));
			//String type = it.getKey().substring(0, it.getKey().indexOf("-"));
			List<Double> itList = it.getValue();
			if (avgW.containsKey(type)) {
				List<Double> avgWList = avgW.get(type);
				for (int i = 0; i < itList.size(); i++) {
					avgWList.set(i, ((avgWList.get(i)+itList.get(i))/2));
				}
				avgW.put(type, avgWList);
			} else {
				avgW.put(type, itList);
			}
		}
		
//		System.out.println(avgW.keySet());
//		System.out.println(avgW.get("5"));
//		System.out.println(avgW.get("10"));
//		System.out.println(avgW.get("7"));
		
		wsAll.clear();
		wsAll = null;
	}
	
	/**
	 * 计算文件和类中心向量的相似度，将文件归为相似度最大的那一类
	 * @param ws
	 * @return 返回文件分类
	 */
	private static String similar(List<Double> A) {
		String type = null;       // 类别
		Double maxCos = 0.0;      // 最大的相似度
		Double threshold = 0.0;   // 阈值
		Double aAbsolute = 0.0;  // 待测文件A向量的绝对值
		Set<Entry<String , List<Double>>> set = avgW.entrySet();
		Iterator<Entry<String , List<Double>>> iter = set.iterator();
		boolean flag = true;  //记录是否是第一次
		while (iter.hasNext()) {
			Entry<String , List<Double>> it = iter.next();
			List<Double> B = it.getValue();
			
			// 第一次赋初值
			if (flag) {
				type = it.getKey();   
				// 计算  |A|  计算一次就好了
				Double a = 0.0;
				for (int i = 0; i < A.size(); i++) {
					a += Math.pow(A.get(i), 2);
				}
				aAbsolute = Math.sqrt(a);
			}
			
			Double ab = 0.0;   // A*B的值
			Double b = 0.0;   
			for (int i = 0; i < B.size(); i++) {
				Double bi = B.get(i);
				// 计算两个向量的点积 A*B
				ab += A.get(i) * bi;
				
				// 计算  |B|
				b += Math.pow(bi,2);
			}
			Double bAbsolute = Math.sqrt(b);    // 中心向量的绝对值
			
			Double cosAB = ab/(aAbsolute*bAbsolute) - threshold;   // 余弦表示相似度
			
			if (cosAB > maxCos) {
				type = it.getKey();
				maxCos = cosAB;
			}
			
			flag = false;   
			
		}
		
		return type;
	}

}
