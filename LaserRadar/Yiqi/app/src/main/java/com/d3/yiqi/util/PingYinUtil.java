package com.d3.yiqi.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.*;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PingYinUtil {  
    
    /** 
        *  
        * @param inputString 
        * @return 
        */  
        public static String getPingYin(String inputString) {  
            HanyuPinyinOutputFormat format = new  
            HanyuPinyinOutputFormat();  
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
            format.setVCharType(HanyuPinyinVCharType.WITH_V);  
  
            char[] input = inputString.trim().toCharArray();  
            String output = "";  
  
            try {  
                for (int i = 0; i < input.length; i++) {  
                    if (Character.toString(input[i]).
                    matches("[\\u4E00-\\u9FA5]+")) {  
                        String[] temp = PinyinHelper.  
                        toHanyuPinyinStringArray(input[i],  
                        format);  
                        output += temp[0];  
                    } else  
                        output += Character.toString(
                        input[i]);  
                }  
            } catch (BadHanyuPinyinOutputFormatCombination e) {  
                e.printStackTrace();  
            }  
            return output;  
        }  
}  
