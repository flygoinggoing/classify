package cn.bistu.icdd.gpf.preprocessTest;

import org.junit.Test;

import cn.bistu.icdd.gpf.preprocess.Preprocessor;

public class PreprocessorTest {
	
	@Test
	public void startTest() throws Exception{
		String fileRootPath = "D:/NLP/信息检索与搜索引擎/大作业";
		Preprocessor pre = new Preprocessor("utf-8");
		int fileNum = pre.start(fileRootPath+"/训练集");
	}

}
