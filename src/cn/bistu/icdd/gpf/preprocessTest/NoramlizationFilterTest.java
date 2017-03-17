package cn.bistu.icdd.gpf.preprocessTest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import cn.bistu.icdd.gpf.preprocess.NormalizationFilter;
import cn.bistu.icdd.gpf.preprocess.NormalizationHashFilter;

public class NoramlizationFilterTest {
	
	@Test
	public void process() throws IOException {
		long startTime = System.currentTimeMillis();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/NLP/tst/同义词缩减版.txt")));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:/NLP/tst/同义词.txt")));
		NormalizationFilter nf = NormalizationFilter.getInstance();
		String line = null;
		while ((line = br.readLine()) != null) {
			LinkedList<String> passage = new LinkedList<String>(Arrays.asList(line.split(" ")));
			String con = nf.process(passage);
			bw.write(con);
			bw.newLine();
		}
		long endTime = System.currentTimeMillis();
		System.out.println(endTime-startTime);
	}
	
	@Test
	public void processHash() throws IOException {
		long startTime = System.currentTimeMillis();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:/NLP/tst/同义词缩减版.txt")));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:/NLP/tst/同义词.txt")));
		NormalizationHashFilter nf = NormalizationHashFilter.getInstance();
		String line = null;
		while ((line = br.readLine()) != null) {
			LinkedList<String> passage = new LinkedList<String>(Arrays.asList(line.split(" ")));
			String con = nf.process(passage);
			bw.write(con);
			bw.newLine();
		}
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime-startTime);
	}
}
