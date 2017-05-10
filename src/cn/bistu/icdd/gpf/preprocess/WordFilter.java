package cn.bistu.icdd.gpf.preprocess;

/**
 * 文本过滤器
 * 
 * 1.基于文本内容的转换和映射。
 * 对文本的不同表示形式进行映射和过滤。这步主要处理的是文本的异构问题。
 * 
 * 如：中文中可能有繁体和简体的混编，这样就要把所有的内容都转换成简体。
 * 	     英文中可能有大小写的不同，需要把所有的表示成小写字符。法文中可能需要首先把所有音标过滤掉。 
 * 
 * 2.过滤文本内的无用符号、乱码等内容
 * 
 * 
 * @author 关鹏飞
 *
 */
public class WordFilter implements Filter<String,String>{
	
	/**
	 * 文本过滤器主程序
	 * 
	 * @param passage 待过滤字符串
	 * @return 过滤后字符串
	 */
	@Override
	public String process(String passage) {
		char[] words = passage.toCharArray();
		// 过滤掉除汉字和标点的符号
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i  < words.length ; i++) {
			char w = words[i];
			/*if (isChinese(w)) {
				// 汉字
				sb.append(w);
			} else if (isHALFWIDTH_AND_FULLWIDTH_FORMS(w)){
				// 全角字符转半角
				w = toSemiangle(w);
				if (isChinese(w)) {
					// 汉字
					sb.append(w);
				}
			}*/
			
			if (isChinese(w)) {
				// 汉字
				sb.append(w);
			} else {
				// 不是汉字插入空格
				sb.append(' ');
			}
		}
		return sb.toString();
	}
	
	/**
	 * 当文本格式内容比较乱时使用（语料不太好）
	 * @param passage 待处理字符串
	 * @return 处理好的字符串
	 */
	private String roughTreatment(String passage){
		return null;
		
	}
	
	/**
	 * 当文本格式内容比较统一时（语料好）
	 * @param passage 待处理字符串
	 * @return 处理好的字符串
	 */
	private String precisionTreatment(String passage){
		return null;
		
	}
	
	/**
	 * 全角验证
	 * 
	 * @param c 带判断字符
	 * @return 返回结果
	 */
	private boolean isHALFWIDTH_AND_FULLWIDTH_FORMS(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		
		if (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS  /* 全角字符（全角占位多） 'ｎ' '１' 'ａ' '，'　'（'　'＂'   */) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是中文字符
	 * @param c 需验证的字符
	 * @return 是中文返回true 否则返回false
	 */
	private boolean isChinese(char c) {
		/*
		 * BASIC_LATIN (拉丁)   // 编程中的所有字母、数字、标点
		 * Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A    // 扩展区A 
		 * Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B    // 扩展区B 
		 */
		/*
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS       CJk（中日韩）  
				|| ub == Character.UnicodeBlock.BASIC_LATIN      拉丁 
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS      CJK补充 
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION      中文标点  〔  〕  〖  〗有待解决  
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {     汉语引号   ‰ 有待解决 
			return true;
		}
		return false;
		*/
		
		/*
		if ((c >= 0x4e00) && (c <= 0x9fbb)){    
			//汉字
			return true;
		} else if ((c >= 0xFF00) && (c <= 0xFF5E)) {
			// ANSI对应的全角字符
			return true;
		} else {
			return false;
		}
		*/
		
		if ((c >= 0x4e00) && (c <= 0x9fbb)){    
			//汉字
			return true;
		} else {
			return false;
		}
		
	}
	
	
    /**
     * 将全角字符转为半角
     * 全角空格为12288，半角空格为32
     * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     * 
     * @param c 要转换的包含全角的任意字符
     * @return  转换之后的字符
     */
    private char toSemiangle(char c) {

        if (c == 12288) {// 全角空格
            c = (char) 32;
        } else if (c > 65280 && c < 65375) {// 其他全角字符
            c = (char) (c - 65248);
        }
        
        return c;
    }
    
    /**
     * 将数字从全角转换为半角
     * @param c 全角字符
     * @return 半角字符
     */
    private char numToSemiangle(char c){
    	switch(c){
    	case '０':
    		return '0';
    	case '１':
    		return '1';
    	case '２':
    		return '2';
    	case '３':
    		return '3';
    	case '４':
    		return '4';
    	case '５':
    		return '5';
    	case '６':
    		return '6';
    	case '７':
    		return '7';
    	case '８':
    		return '8';
    	case '９':
    		return '9';
    	default:
    		return c;
    	}
    }
}
