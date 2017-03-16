package cn.bistu.icdd.gpf.selectFeature.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class NumTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filePath = "D:/NLP/每类个数类似文本分类/原始特征项/原始特征项.txt";
		//String filePath = "D:/NLP/分类测试/原始特征项/原始特征项.txt";
		//String filePath = "D:/NLP/test/原始特征项/原始特征项.txt";
		
		File file = new File(filePath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			int num10 = 0;
			int num20 = 0;
			int num30 = 0;
			int num40 = 0;
			int num50 = 0;   //计数
			int num100 = 0;   //计数
			int num150 = 0;   //计数
			int num200 = 0;   //计数
			int num250 = 0;   //计数
			int num300 = 0;   //计数
			int num350 = 0;   //计数
			int num400 = 0;   //计数
			int num450 = 0;   //计数
			int num500 = 0;   //计数
			int num600 = 0;
			int numMore600= 0;   //计数
			
			String content = null;
			while ((content = br.readLine()) != null) {
				if (10 > Integer.parseInt(content.split("  ")[1])){
					num10++;
				} else if ( 20 > Integer.parseInt(content.split("  ")[1])) {
					num20++;
				} else if ( 30 > Integer.parseInt(content.split("  ")[1])) {
					num30++;
				} else if ( 40 > Integer.parseInt(content.split("  ")[1])) {
					num40++;
				} else if ( 50 > Integer.parseInt(content.split("  ")[1])) {
					num50++;
				} else if(100 > Integer.parseInt(content.split("  ")[1])) {
					num100++;
				} else if(150 > Integer.parseInt(content.split("  ")[1])) {
					num150++;
				} else if(200 > Integer.parseInt(content.split("  ")[1])) {
					num200++;
				} else if(250 > Integer.parseInt(content.split("  ")[1])) {
					num250++;
				} else if(300 > Integer.parseInt(content.split("  ")[1])) {
					num300++;
				} else if(350 > Integer.parseInt(content.split("  ")[1])) {
					num350++;
				} else if(400 > Integer.parseInt(content.split("  ")[1])) {
					num400++;
				}else if(450 > Integer.parseInt(content.split("  ")[1])) {
					num450++;
				}else if(500 > Integer.parseInt(content.split("  ")[1])) {
					num500++;
				}else if(600 > Integer.parseInt(content.split("  ")[1])) {
					num600++;
				}else {
					numMore600++;
				}
			}
			
			System.out.println("10:" + num10);
			System.out.println("20:" + num20);
			System.out.println("30:" + num30);
			System.out.println("40:" + num40);
			System.out.println("50:" + num50);
			System.out.println("100:" + num100);
			System.out.println("150:" + num150);
			System.out.println("200:" + num200);
			System.out.println("250:" + num250);
			System.out.println("300:" + num300);
			System.out.println("350:" + num350);
			System.out.println("400:" + num400);
			System.out.println("450:" + num450);
			System.out.println("500:" + num500);
			System.out.println("600:" + num600);
			System.out.println("600:" + numMore600);
			

			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}

}
