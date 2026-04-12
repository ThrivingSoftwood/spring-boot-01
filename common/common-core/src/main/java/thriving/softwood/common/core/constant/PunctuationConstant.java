package thriving.softwood.common.core.constant;

/**
 * 标点符号常量类
 * <p>
 * 定义常用中英文标点符号，用于避免代码中的“魔法字符串”，提高代码可读性、一致性和可维护性。 所有常量均使用 {@code public static final} 修饰，并通过私有构造方法防止实例化。
 * </p>
 *
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
public final class PunctuationConstant {

    /** 逗号 */
    public static final String COMMA = ",";

    /* ========== 常用分隔符 ========== */
    /** 句号 */
    public static final String PERIOD = ".";
    /** 分号 */
    public static final String SEMICOLON = ";";
    /** 冒号 */
    public static final String COLON = ":";
    /** 连字符/短横线 */
    public static final String HYPHEN = "-";
    /** 下划线 */
    public static final String UNDERSCORE = "_";
    /** 竖线 */
    public static final String VERTICAL_BAR = "|";
    /** 波浪号 */
    public static final String TILDE = "~";
    /** 与符号 */
    public static final String AMPERSAND = "&";
    /** @ 符号 */
    public static final String AT_SIGN = "@";
    /** 井号 */
    public static final String HASH = "#";
    /** 星号 */
    public static final String ASTERISK = "*";
    /** 斜杠 */
    public static final String SLASH = "/";
    /** 反斜杠 */
    public static final String BACKSLASH = "\\";
    /** 等号 */
    public static final String EQUAL_SIGN = "=";
    /** 加号 */
    public static final String PLUS_SIGN = "+";
    /** 百分号 */
    public static final String PERCENT_SIGN = "%";
    /** 美元符号 */
    public static final String DOLLAR_SIGN = "$";
    /** 人民币/日元符号 */
    public static final String YEN_SIGN = "¥";
    /** 左圆括号 */
    public static final String LEFT_PARENTHESIS = "(";

    /* ========== 括号类 ========== */
    /** 右圆括号 */
    public static final String RIGHT_PARENTHESIS = ")";
    /** 左方括号 */
    public static final String LEFT_BRACKET = "[";
    /** 右方括号 */
    public static final String RIGHT_BRACKET = "]";
    /** 左花括号 */
    public static final String LEFT_BRACE = "{";
    /** 右花括号 */
    public static final String RIGHT_BRACE = "}";
    /** 左尖括号 */
    public static final String LEFT_ANGLE_BRACKET = "<";
    /** 右尖括号 */
    public static final String RIGHT_ANGLE_BRACKET = ">";
    /** 双引号 */
    public static final String DOUBLE_QUOTE = "\"";

    /* ========== 引号类 ========== */
    /** 单引号 */
    public static final String SINGLE_QUOTE = "'";
    /** 反引号 */
    public static final String BACKTICK = "`";
    /** 空格 */
    public static final String SPACE = " ";

    /* ========== 空白与不可见字符 ========== */
    /** 制表符 (Tab) */
    public static final String TAB = "\t";
    /** 换行符 (系统相关，此常量表示通用新行) */
    public static final String NEWLINE = System.lineSeparator();
    /** 空字符串 */
    public static final String EMPTY = "";
    /** 中文逗号 */
    public static final String CN_COMMA = "，";

    /* ========== 中文标点符号 (全角) ========== */
    /** 中文句号 */
    public static final String CN_PERIOD = "。";
    /** 中文分号 */
    public static final String CN_SEMICOLON = "；";
    /** 中文冒号 */
    public static final String CN_COLON = "：";
    /** 中文问号 */
    public static final String CN_QUESTION_MARK = "？";
    /** 中文感叹号 */
    public static final String CN_EXCLAMATION_MARK = "！";
    /** 中文左圆括号 */
    public static final String CN_LEFT_PARENTHESIS = "（";
    /** 中文右圆括号 */
    public static final String CN_RIGHT_PARENTHESIS = "）";
    /** 中文左书名号 */
    public static final String CN_LEFT_ANGLE_BRACKET = "《";
    /** 中文右书名号 */
    public static final String CN_RIGHT_ANGLE_BRACKET = "》";
    /** 中文间隔号 (中点) */
    public static final String CN_MIDDLE_DOT = "·";

    // 私有构造方法，防止工具类被实例化
    private PunctuationConstant() {
        throw new UnsupportedOperationException("这是一个工具类常量，禁止实例化");
    }
}