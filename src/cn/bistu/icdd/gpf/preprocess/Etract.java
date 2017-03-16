package cn.bistu.icdd.gpf.preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * 处理同义词文件
 * @author 关鹏飞
 *
 */
public class Etract {
	
	public static void main(String[] args) throws Exception {
		StopWordFilter sw = StopWordFilter.getInstance();
		
		File inFile = new File("D:/NLP/哈工大社会计算与信息检索研究中心同义词词林扩展版/哈工大社会计算与信息检索研究中心同义词词林扩展版.txt");
		File outFile = new File("D:/NLP/哈工大社会计算与信息检索研究中心同义词词林扩展版/同义词缩减版.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile),"gb2312"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
		String str = null;
		while ((str = br.readLine())!=null) {
			if (str.contains("=")){
				// 截取等号以后的
				str = str.substring(str.indexOf("=")+2, str.length()); 
				LinkedList<String> list = new LinkedList<String>(Arrays.asList(str.split(" ")));
				// 去同义词
				list = (LinkedList<String>) sw.process(list);
				if (!list.isEmpty() && list.size()!=1) {
					str = list.toString().replace("[", "").replace("]", "").replace(",", "");
					
					// 写入 
					bw.write(str);
					bw.newLine();
				}
			}
			
		}
	}
}
