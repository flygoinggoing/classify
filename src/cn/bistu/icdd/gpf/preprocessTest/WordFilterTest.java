package cn.bistu.icdd.gpf.preprocessTest;

import org.junit.Test;

import cn.bistu.icdd.gpf.preprocess.WordFilter;

/**
 * WordFilter 测试类
 * @author 关鹏飞
 *
 */
public class WordFilterTest {
	
	@Test
	public void isChineseTest(){
		WordFilterTest sf = new WordFilterTest();

		System.out.println("字母、数字、标点");
		System.out.println(sf.isChinese('a'));
		System.out.println(sf.isChinese('1'));
		System.out.println(sf.isChinese(','));
		
		
		System.out.println("全角");
		System.out.println(sf.isChinese('，'));
		System.out.println(sf.isChinese('ａ'));
		System.out.println(sf.isChinese('１'));
		
		
	}
	
	private boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		/*
		 * BASIC_LATIN (拉丁)   // 编程中的所有字母、数字、标点
		 * Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A    // 扩展区A 
		 * Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B    // 扩展区B 
		 */
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS     /*  CJk（中日韩）  */
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS     /* CJK补充 */
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION     /* 中文标点  〔  〕  〖  〗有待解决  */
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {    /* 汉语引号   ‰ 有待解决 */
			return true;
		}
		
		/*
		if ((c >= 0x4e00) && (c <= 0x9fbb)){
			return true;
		}
		*/
		return false;
	}
	
	@Test
	public void processTest(){
		WordFilter wf = new WordFilter();
		System.out.println(wf.process("μ本发明公开了一种副产盐酸的方法，该产品属于精细化工领域领域；本发明以三氯化磷、亚磷酸饱和液、水为原料，以水解反应釜、冷凝器、吸收罐、精馏浓缩釜为设备，通过三氯化磷水解、氯化氢气体的冷凝吸收、盐酸粗品的精馏浓缩等工序而制得盐酸，该方法的优点是：副产盐酸成本低、生产周期短、产品质量好、无有毒有害气体和液体排放、可批量生产，该方法既产生了一定的经济效益，又消除了污染，产生了较大的社会效益。|1.一种副产盐酸的方法，使用的原料包括：三氯化磷23-25%、亚磷酸饱和液4%、水71-73%；其特征是：步骤（1）将配方量的水和亚磷酸饱和液送入水解反应釜，开动搅拌器进行搅拌，搅拌器的转速为35-45转／分，将配方量的三氯化磷缓慢的滴入水解反应釜中，反应放热，通过控制滴加三氯化磷的速度来控制水解反应釜中的反应温度在82-86℃之间，三氯化磷滴加完毕后停止搅拌，保温反应1.2-1.4小时，反应得到的液体为亚磷酸水溶液排出储存待用，产生的氯化氢气体通过管道排出进入冷凝器的弯曲管道中，冷凝器的弯曲管道用0-3℃的冷水洗淋，使高温的氯化氢气体迅速降温至常温；步骤（2将降温后的氯化氢气体送入吸收罐，吸收罐中储有约为罐体积80%的水，对输入的氯化氢气体反复吸收而获得粗品盐酸，吸收结束后，将吸收罐中的粗品盐酸送入精馏浓缩釜，将盐酸的浓度浓缩至36-38%时结束浓缩，得到成品。2.如权利要求1所述的一种副产盐酸的方法，其步骤（1）的特征是：所述的水解反应釜中的反应温度为84℃；保温反应时间为1.3小时。3.如权利要求1所述的一种副产盐酸的方法，其步骤（2）的特征是：所述的成品盐酸的浓度为37%。"));
	}
}
