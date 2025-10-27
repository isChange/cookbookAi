package com.ly.cookbook.common.constant;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/29 10:16
 * @email liuyia2022@163.com
 */
public class SysConstant {

    //*************************** 系统角色信息 ***************************
    public final static String SYS_ADMIN = "admin";
    public final static String SYSTEM_FACTORY = "SYSTEM";


    //*************************** 系统语言信息 ***************************
    public final static String LANGUAGE = "Language";
    public final static String LANGUAGE_ZH_CN = "zh-CN";
    public final static String LANGUAGE_EN_US = "en-US";


    //*************************** JWT信息 ***************************
    public final static String JWT_ACCESS = "ACCESS";
    public final static String JWT_REFRESH = "REFRESH";

    //*************************** 文件信息 ***************************
    public final static Long FILE_SIZE_1_M = 1024 * 1024 * 1L;
    public final static Long FILE_SIZE_2_M = 1024 * 1024 * 2L;
    public final static Long FILE_SIZE_3_M = 1024 * 1024 * 3L;
    public final static Long FILE_SIZE_4_M = 1024 * 1024 * 4L;
    public final static Long FILE_SIZE_5_M = 1024 * 1024 * 5L;
    public final static Long FILE_SIZE_6_M = 1024 * 1024 * 6L;
    public final static Long FILE_SIZE_7_M = 1024 * 1024 * 7L;
    public final static Long FILE_SIZE_8_M = 1024 * 1024 * 8L;
    public final static Long FILE_SIZE_9_M = 1024 * 1024 * 9L;
    public final static Long FILE_SIZE_10_M = 1024 * 1024 * 10L;


    //*************************** 系统常量信息 ***************************
    /**
     * Long类型的 是、否
     */
    public final static Long LONG_YES = 1L;
    public final static Long LONG_NO = 0L;

    /**
     * Integer类型的 是、否
     */
    public final static Integer INTEGER_YES = 1;
    public final static Integer INTEGER_NO = 0;

    /**
     * Char类型的 是、否
     */
    public final static char CHAR_YES = '1';
    public final static char CHAR_NO = '0';
    public final static char CHAR_EMPTY = ' ';
    public final static char Char_Y_UPPER = 'Y';
    public final static char Char_N_UPPER = 'N';


    /**
     * String类型的 是、否
     */
    public final static String STRING_YES = "1";
    public final static String STRING_NO = "0";
    public final static String STRING_A = "A";
    public final static String STRING_C = "C";
    public final static String STRING_D = "D";
    public final static String STRING_Y = "Y";
    public final static String STRING_L = "L";
    public final static String STRING_M = "M";
    public final static String STRING_R = "R";
    public static final Object STRING_F = "F";
    public static final Object STRING_S = "S";
    public final static String STRING_N = " ";
    public final static String STRING_N_UPPER = "N";


    /**
     * int类型
     */
    public final static int INT_ADD=1;
    public final static int INT_REMOVE=-1;
    public final static int INT_ZERO=0;
    public final static int INT_ONE=1;
    public final static int INT_TWO=2;
    public final static int INT_THREE=3;
    public final static int INT_FOUR=4;
    public final static int INT_FIVE=5;
    public final static int INT_SIX=6;
    public final static int INT_SEVEN=7;

    /**
     * long 类型
     */
    public final static long LONG_ZERO=0L;
    public final static long LONG_ONE=1L;
    public final static long LONG_TWO=2L;
    public final static long LONG_THREE=3L;

    /**
     * float型
     */
    public final static float FLOAT_ZERO=0.0f;
    public final static float FLOAT_ONE=1.0f;
    public final static float FLOAT_ABS_ZERO=0.0000001f;

    /**
     * double类型
     */
    public final static double DOUBLE_ZERO=0.0;

    /**
     * char类型
     */
    public static final char CHAR_AT = '@';
    public static final char CHAR_UNDER_LINE = '_';
    public static final char CHAR_EQUAL = '=';
    public static final char CHAR_ADD = '+';
    public static final char CHAR_MINUS = '-';
    public final static char CHAR_ZERO='0';
    public final static char CHAR_ONE='1';
    public final static char CHAR_TWO='2';
    public final static char CHAR_THREE='3';
    public final static char CHAR_FOUR='4';
    public final static char CHAR_FIVE='5';
    public static final char CHAR_SIX = '6';
    public final static char CHAR_SEVEN='7';
    public final static char CHAR_EIGHT='8';
    public final static char CHAR_NINE='9';
    public static final char CHAR_A_UPPER = 'A';
    public static final char CHAR_B_UPPER = 'B';
    public static final char CHAR_C_UPPER = 'C';
    public static final char CHAR_D_UPPER = 'D';
    public static final char CHAR_E_UPPER = 'E';
    public static final char CHAR_F_UPPER = 'F';
    public static final char CHAR_G_UPPER = 'G';
    public static final char CHAR_H_UPPER = 'H';
    public static final char CHAR_I_UPPER = 'I';
    public static final char CHAR_J_UPPER = 'J';
    public static final char CHAR_K_UPPER = 'K';
    public static final char CHAR_L_UPPER = 'L';
    public static final char CHAR_N_UPPER = 'N';
    public static final char CHAR_R_UPPER = 'R';
    public static final char CHAR_P_UPPER = 'P';
    public static final char CHAR_W_UPPER = 'W';
    public static final char CHAR_O_UPPER = 'O';
    public static final char CHAR_M_UPPER = 'M';
    public static final char CHAR_U_UPPER = 'U';
    public static final char CHAR_Z_UPPER = 'Z';
    public static final char CHAR_S_UPPER = 'S';


    /**
     * String 类型
     */
    public final static String STRING_ZERO="0";
    public final static String STRING_ONE="1";
    public final static String STRING_TWO="2";
    public final static String STRING_THREE="3";
    public final static String STRING_FOUR="4";
    public final static String STRING_FIVE="5";
    public final static String STRING_EMPTY =" ";
    public static final String STRING_001 = "001";
    public static final Object STRING_P = "P";




    //*************************** 工具常量信息 ***************************
    public final static String TOOL_SET = "set";
    public final static String TOOL_GET = "get";
    public final static String TOOL_LINE = "_";
    public final static String TOOL_BLANK = "";




    //*************************** 系统操作类型信息 ***************************
    public final static String OPERATE_CREATE = "CREATE";
    public final static String OPERATE_UPDATE = "UPDATE";
    public final static String OPERATE_DELETE = "DELETE";
    public final static String OPERATE_APPROVE = "APPROVE";
    public final static String OPERATE_REJECT = "REJECT";
    public final static String OPERATE_RELEASE= "RELEASE";
    public final static String OPERATE_IN = "IN";
    public final static String OPERATE_NOT_IN = "NOT_IN";


    //*************************** HTTP请求类型 ***************************
    public final static String HTTP_GET = "GET";
    public final static String HTTP_POST = "POST";
    public final static String HTTP_PUT = "PUT";
    public final static String HTTP_DELETE = "DELETE";


    //*************************** 用户等级类型 ***************************
    /**
     * 用户等级 - 新手/初学者
     */
    public final static String USER_LEVEL_BEGINNER = "beginner";
    public final static String USER_LEVEL_BEGINNER_CN = "新手";
    public final static String USER_LEVEL_BEGINNER_CN_ALT = "初学者";

    /**
     * 用户等级 - 中级/标准
     */
    public final static String USER_LEVEL_INTERMEDIATE = "intermediate";
    public final static String USER_LEVEL_STANDARD = "standard";

    /**
     * 用户等级 - 高级/专家
     */
    public final static String USER_LEVEL_ADVANCED = "advanced";
    public final static String USER_LEVEL_EXPERT = "expert";
    public final static String USER_LEVEL_EXPERT_CN = "专家";
    public final static String USER_LEVEL_ADVANCED_CN = "高级";


    //*************************** 问题类型 ***************************
    /**
     * 问题类型 - 基础/入门
     */
    public final static String QUESTION_TYPE_BASIC = "basic";
    public final static String QUESTION_TYPE_COMMAND = "command";
    public final static String QUESTION_TYPE_BASIC_CN = "入门";
    public final static String QUESTION_TYPE_BASIC_CN_ALT = "基础";

    /**
     * 问题类型 - 高级/专业
     */
    public final static String QUESTION_TYPE_PERFORMANCE = "performance";
    public final static String QUESTION_TYPE_OPTIMIZATION = "optimization";
    public final static String QUESTION_TYPE_ARCHITECTURE = "architecture";
    public final static String QUESTION_TYPE_PERFORMANCE_CN = "性能";
    public final static String QUESTION_TYPE_OPTIMIZATION_CN = "优化";
    public final static String QUESTION_TYPE_ARCHITECTURE_CN = "架构";


}
