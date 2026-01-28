package thriving.softwood.common.core.util;

import static thriving.softwood.common.core.constant.PunctuationConstant.SPACE;

import cn.hutool.v7.core.text.StrUtil;

public class StringUtil {
    public static String getLastPart(String str, String splitter) {
        if (StrUtil.isBlank(str)) {
            return SPACE.equals(splitter) ? "" : str;
        }
        int lastIndex = str.lastIndexOf(splitter);
        if (lastIndex == -1) {
            return str;
        }
        return str.substring(lastIndex + 1);
    }

    public static String insertAfterLast(String original, String separator, String insertion) {
        if (StrUtil.isEmpty(original)) {
            return insertion;
        }
        int lastIndex = original.lastIndexOf(separator);
        if (lastIndex == -1) {
            return original + insertion;
        }
        int positon = lastIndex + 1;
        return original.substring(0, positon) + insertion + original.substring(positon);
    }
}
