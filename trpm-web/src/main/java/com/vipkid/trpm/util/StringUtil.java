package com.vipkid.trpm.util;

/**
 * Created by pankui on 2017-04-20.
 */
public class StringUtil {
    /**
     * 过滤大部分iOS与android emoji表情符号
     * @author pankui
     * */

    public static String filterEmoji(String input) {
        if (input == null){
            return null;
        }
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            if (i < (input.length() - 1)) { // Emojis are two characters long in java, e.g. a rocket emoji is "\uD83D\uDE80";
                if (Character.isSurrogatePair(input.charAt(i), input.charAt(i + 1))) {
                    i += 1; //also skip the second character of the emoji
                    continue;
                }
            }
            sb.append(input.charAt(i));
        }

        return sb.toString();
    }
}
