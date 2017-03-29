package cn.bistu.icdd.gpf.textRepresent;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 绝对词频（TF）-倒排文档频度（IDF）
 * @author 关鹏飞
 *
 */
public class TFIDFCalculater {
	
	// 使用内存模式，将表示的文件向量存储在内存
	public static final int USEMEMORY = 1;   
	
	// 使用外存模式，不适用内存存储向量
	public static final int NONUSEMEMORY = 0;
	
	// 存储用户选择的模式
	private int mode;
	
	private final int N ;   // 总文档数   此处应该为上一步传过来
	
	Map<String , List<Double>>  wsAll = null;  // 格式：文件名  向量（ws）
	
	private Map<String ,Double> ws = new HashMap<String ,Double>();   // 特征权 格式 ：特征项  权值 
	private Map<String ,Double> idf = new HashMap<String ,Double>();   // 特征项及IDF值
	private Map<String ,Integer> tf = new HashMap<String ,Integer>();  // 文本的特征词频
	
	// 将文本表示向量写出到文件缓存
	private BufferedWriter bw = null;   
	
	String corpusFilePath = null;   //预处理后文本路径

	
	/**
	 * 初始化
	 * @param corpusFilePath 预处理后的语料
	 * @param n 总文档数
	 * @param mode 是否使用内存模式。内存模式：速度快适合小规模的语料  外存模式：避免大规模语料导致占用内存过多，速度慢，适合大量文本
	 */
	public TFIDFCalculater(String corpusFilePath ,int n ,int mode) {
		
		System.out.println("TF-IDF初始化：");
		this.corpusFilePath = corpusFilePath;
		
		// 生成特征项的路径
		File corfile = new File(corpusFilePath); 
		String root = corfile.getParentFile().getAbsolutePath();
		String inPath = root + "/抽取特征项/特征项.txt";
		File dfFile = new File(inPath);

		// 判断模式
		if (mode == TFIDFCalculater.USEMEMORY) {
			// 内存模式创建存储所有向量的容器
			wsAll = new HashMap<String , List<Double>>();
			this.mode = TFIDFCalculater.USEMEMORY;
		} else {
			this.mode = TFIDFCalculater.NONUSEMEMORY;
			// 外存模式创建写出文本和流

			String outFilePath = root + "/文本表示/文本表示.txt";
			File outFile = new File(outFilePath);
			if (!outFile.exists()) {
				File parent = outFile.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				try {
					outFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		N = n;
		
		System.out.println("读入特征项");
		// 读入DF值
		initDF(dfFile);
		
	}
	
	/**
	 * 初始化（默认使用内存模式）
	 * @param corpusFilePath 预处理后的语料
	 * @param n 总文档数
	 */
	public TFIDFCalculater(String corpusFilePath ,int n){
		this(corpusFilePath, n, TFIDFCalculater.USEMEMORY);
	}

	/**
	 * 文件表示主程序
	 * @param corpusFile 预处理后的文件
	 * @return 返回所有文件的特征向量表示情况  格式：文件名  向量
	 */
	public Map<String , List<Double>> process() {
		Long startTime = System.currentTimeMillis();
		File corpusFile =new File(corpusFilePath);
		System.out.println("开始计算TF-IDF");
		// 计算TF_IDF
		calcTF_IDF(corpusFile);
		
		
		// 关闭文件写出流
		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Long endTime = System.currentTimeMillis();
		System.out.println("计算完成，共用时：" + (endTime-startTime));
			
		// 返回所有的特征向量
		return wsAll;
	}
	

	/**
	 * 读入特征项并计算对应的IDF存储文件
	 * @param dfFile 特征项文件
	 */
	private void initDF(File dfFile) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(dfFile),"utf-8"));
			String content = null;

			while ((content = br.readLine()) != null) {
				String[] con = content.split("  ");
				idf.put(con[0], Math.log(N/Double.parseDouble(con[1])));
			}
			
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

	/**
	 * 计算TF_IDF值 并输出
	 * @param file
	 */
	private void calcTF_IDF(File file) {
		if (file.isDirectory()){
			File[] lists = file.listFiles();
			for (File list : lists) {
				calcTF_IDF(list);
			}
		} else {
			clearTF();   //清空上一个文件的tf
			ws.clear();   //清空上一个文件的TF-IDF值
			
			//System.out.println(file.getAbsolutePath());
			
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
				String content = null;
				StringBuilder page = new StringBuilder();

				// 将文件全部读入
				while ((content = br.readLine()) != null) {
					page.append(content);
				}
				
				// 把词存入list集合
				List<String> lists = Arrays.asList(page.toString().split(" "));
				// 文本总词数
				int pageSize = lists.size();  
				for (String list : lists) {
					// 如果词在idf表中，说明是特征项
					if (idf.containsKey(list)) {
						//int value = 0;
						//if (tf.containsKey(list)) {
						//不用判断  根据tf的初始化得出，在idf中必在tf中
						int value = tf.get(list);
						//}
						tf.put(list, (value+1));   // 其实只是存了词出现的次数，不是真正的tf
					}
				}
				
				// 计算TF-IDF和词一同存入ws
				/*** 又用set输出是不是有问题  **/
				Set<Entry<String, Integer>> tfSet = tf.entrySet();  
				Iterator<Entry<String, Integer>> iterTf = tfSet.iterator();
				while (iterTf.hasNext()) {
					Entry<String, Integer> it = iterTf.next();
					String key = it.getKey();
					Double value = it.getValue().doubleValue()/pageSize * idf.get(key);  //tf*log(N/df)
					ws.put(key, value);
				}
				
				// 判断模式进行不同的处理
				if (mode == TFIDFCalculater.USEMEMORY){
					// 加入wsAll
					Collection<Double> coll =  ws.values();  // 是按顺序输出吗
					List<Double> list = new ArrayList<Double>(coll);
					wsAll.put(file.getName(), list);
				} else {
					// 将文件的ws打出
					printWS(file.getName());
					//System.out.println("处理完第" + (i++) + "篇");
				}
				
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

	// 清空tf并初始化
	private void clearTF() {
		/*****    idf的顺序会变吗           *****/
		Set<String> dfSet = idf.keySet();  
		Iterator<String> iter = dfSet.iterator();
		while (iter.hasNext()) {
			tf.put(iter.next(),0);
		}
	}

	/**
	 * 打印文件的特征权
	 * @param fileName 文件名
	 */
	private void printWS(String fileName) {
		StringBuilder line = new StringBuilder();
		line.append(fileName);
		Double[] list = (Double[]) ws.values().toArray();

		// 只保存不为零的
		for (int i = 1; i <= list.length; i++) {
			Double value = list[i];
			if (value != 0) {
				line.append(" " + i + ":" + list[i]);
			}
		}
		
		try {
			bw.write(line.toString());
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
