package cn.bistu.icdd.gpf.preprocess;

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
import java.util.List;

/**
 * 预处理器
 * 
 * 输入：原始文件夹
 * 输出：在原始文件夹的同级目录下输出处理后的文档，文件目录保持原有格式
 * 
 * 内部处理流程：（在保证效率的前提下解耦）
 * 读入文件——>文件结构处理（标准格式可以忽略）——>文本过滤器——>分词（词性过滤）——>停用词处理——>归一化——>输出
 * 
 * @author 关鹏飞
 *
 */
public class Preprocessor {
	// 处理器流
	private List<Filter> filter = new ArrayList<Filter>();
	
	// 文件读写指针
	BufferedReader br = null;
	BufferedWriter bw = null;
	
	// 文件编码
	String code;
	
	// 记录文件总数
	private int N = 0;
	
	/**
	 * 预处理构造方法
	 * @param code 传入读入文件的编码格式
	 * @throws Exception
	 */
	public Preprocessor(String code) throws Exception{
		WordFilter wf = new WordFilter();
		PartOfSpeechFilter posf = new PartOfSpeechFilter();
		// TokenizerFilter tf = new TokenizerFilter();
		StopWordFilter swf = StopWordFilter.getInstance();
		//NormalizationFilter nf = NormalizationFilter.getInstance();
		NormalizationHashFilter nf = NormalizationHashFilter.getInstance();
		
		// 将处理器添加到流
		filter.add(wf);
		filter.add(posf);
		// filter.add(tf);
		filter.add(swf);
		filter.add(nf);
		
		this.code = code;
	}
	
	/**
	 * 预处理构造方法（默认以utf-8编码读入）
	 * @throws Exception
	 */
	public Preprocessor() throws Exception{
		WordFilter wf = new WordFilter();
		PartOfSpeechFilter posf = new PartOfSpeechFilter();
		// TokenizerFilter tf = new TokenizerFilter();
		StopWordFilter swf = StopWordFilter.getInstance();
		NormalizationFilter nf = NormalizationFilter.getInstance();
		
		// 将处理器添加到流
		filter.add(wf);
		filter.add(posf);
		// filter.add(tf);
		filter.add(swf);
		filter.add(nf);
		
		code = "utf-8";
	}
	
	/**
	 *  开始入口
	 * @param inPath 需要预处理的文件
	 * @return 返回文件总数
	 */
	public int start(String inPath) {
		Long startTime = System.currentTimeMillis();
		System.out.println("开始文件预处理");
		
		// 文件夹下所有文件全部分词		
		File file = new File(inPath);

		// 输出文件路径
		String outPath = file.getParentFile().getAbsolutePath() + "/" + file.getName()+"预处理";
		
		// 处理主函数
		process(file, outPath);
		
		Long endTime = System.currentTimeMillis();
		System.out.print("预处理完成，");
		System.out.println("用时：" + (endTime-startTime));
		
		return N;
		
				
	}

	private void process(File file, String outFilePath) {
		// 获取文件路径
		String filePath = file.getAbsolutePath();
		// 获取文件名
		String fileName = filePath.substring(filePath.lastIndexOf("\\")+1, filePath.length());
		
		// 拼接输出文件路径
		outFilePath += ("/"+fileName);
		// 构造输出文件
		File outFile = new File(outFilePath);
		
		if (file.isDirectory()){
			if(!outFile.exists()){
				System.out.println("创建文件夹：" + outFile.getAbsolutePath());
				outFile.mkdirs();
			}
			File[] lists = file.listFiles();
			
			for (File list : lists) {
				process(list, outFilePath);
			}
		} else{
			// 文本计数
			N++;
			
			try {

				br = new BufferedReader(new InputStreamReader(new FileInputStream(file), code)); // 
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"utf-8")); //以utf-8方式编码
				// 分词
				String content;
				StringBuilder sb = new StringBuilder();
				while ((content = br.readLine()) != null){
					sb.append(content);
				}
				
				String passage = sb.toString();
				sb = null;
				content = null;
				// 全部处理
				 passage = processString(passage);
				
				// 不归一处理
				//passage = filter.get(2).process(filter.get(1).process(filter.get(0).process(passage))).toString().replace("[", "").replace("]", " ").replace(",", "");
				
				// 结果写入新文件
				bw.write(passage);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null){
						bw.close();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				}
				
				
			}
			
		}
	}

	
	/**
	 * 处理字符串
	 * @param passage 文本内容
	 * @return 处理后的字符串
	 */
	private String processString(String passage){
		
		return (String) filter.get(3).process(filter.get(2).process(filter.get(1).process(filter.get(0).process(passage))));
	}

}
