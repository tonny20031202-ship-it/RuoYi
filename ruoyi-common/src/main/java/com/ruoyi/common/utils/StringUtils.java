package com.ruoyi.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Strings;
import org.springframework.util.AntPathMatcher;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.text.StrFormatter;

/**
 * 字符串工具类
 * 
 * @author ruoyi
 */
@SuppressWarnings("deprecation")
public class StringUtils extends org.apache.commons.lang3.StringUtils
{
    /** 空字符串 */
    private static final String NULLSTR = "";

    /** 下划线 */
    private static final char SEPARATOR = '_';

    /** 星号 */
    private static final char ASTERISK = '*';

    /**
     * 获取参数不为空值
     * 
     * @param value defaultValue 要判断的value
     * @return value 返回值
     */
    public static <T> T nvl(T value, T defaultValue)
    {
        return value != null ? value : defaultValue;
    }

    /**
     * * 判断一个Collection是否为空， 包含List，Set，Queue
     * 
     * @param coll 要判断的Collection
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Collection<?> coll)
    {
        return isNull(coll) || coll.isEmpty();
    }

    /**
     * * 判断一个Collection是否非空，包含List，Set，Queue
     * 
     * @param coll 要判断的Collection
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Collection<?> coll)
    {
        return !isEmpty(coll);
    }

    /**
     * * 判断一个对象数组是否为空
     * 
     * @param objects 要判断的对象数组
     ** @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects)
    {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * * 判断一个对象数组是否非空
     * 
     * @param objects 要判断的对象数组
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Object[] objects)
    {
        return !isEmpty(objects);
    }

    /**
     * * 判断一个Map是否为空
     * 
     * @param map 要判断的Map
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Map<?, ?> map)
    {
        return isNull(map) || map.isEmpty();
    }

    /**
     * * 判断一个Map是否为空
     * 
     * @param map 要判断的Map
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Map<?, ?> map)
    {
        return !isEmpty(map);
    }

    /**
     * * 判断一个字符串是否为空串
     * 
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str)
    {
        return isNull(str) || NULLSTR.equals(str.trim());
    }

    /**
     * * 判断一个字符串是否为非空串
     * 
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str)
    {
        return !isEmpty(str);
    }

    /**
     * * 判断一个对象是否为空
     * 
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object)
    {
        return object == null;
    }

    /**
     * * 判断一个对象是否非空
     * 
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object)
    {
        return !isNull(object);
    }

    /**
     * * 判断一个对象是否是数组类型（Java基本型别的数组）
     * 
     * @param object 对象
     * @return true：是数组 false：不是数组
     */
    public static boolean isArray(Object object)
    {
        return isNotNull(object) && object.getClass().isArray();
    }

    /**
     * 去空格
     */
    public static String trim(String str)
    {
        return (str == null ? "" : str.trim());
    }

    /**
     * 替换指定字符串的指定区间内字符为"*"
     *
     * @param str 字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude 结束位置（不包含）
     * @return 替换后的字符串
     */
    public static String hide(CharSequence str, int startInclude, int endExclude)
    {
        if (isEmpty(str))
        {
            return NULLSTR;
        }
        final int strLength = str.length();
        if (startInclude > strLength)
        {
            return NULLSTR;
        }
        if (endExclude > strLength)
        {
            endExclude = strLength;
        }
        if (startInclude > endExclude)
        {
            // 如果起始位置大于结束位置，不替换
            return NULLSTR;
        }
        final char[] chars = new char[strLength];
        for (int i = 0; i < strLength; i++)
        {
            if (i >= startInclude && i < endExclude)
            {
                chars[i] = ASTERISK;
            }
            else
            {
                chars[i] = str.charAt(i);
            }
        }
        return new String(chars);
    }

    /**
     * 截取字符串
     * 
     * @param str 字符串
     * @param start 开始
     * @return 结果
     */
    public static String substring(final String str, int start)
    {
        if (str == null)
        {
            return NULLSTR;
        }

        if (start < 0)
        {
            start = str.length() + start;
        }

        if (start < 0)
        {
            start = 0;
        }
        if (start > str.length())
        {
            return NULLSTR;
        }

        return str.substring(start);
    }

    /**
     * 截取字符串
     * 
     * @param str 字符串
     * @param start 开始
     * @param end 结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end)
    {
        if (str == null)
        {
            return NULLSTR;
        }

        if (end < 0)
        {
            end = str.length() + end;
        }
        if (start < 0)
        {
            start = str.length() + start;
        }

        if (end > str.length())
        {
            end = str.length();
        }

        if (start > end)
        {
            return NULLSTR;
        }

        if (start < 0)
        {
            start = 0;
        }
        if (end < 0)
        {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * 在字符串中查找第一个出现的 `open` 和最后一个出现的 `close` 之间的子字符串
     * 
     * @param str 要截取的字符串
     * @param open 起始字符串
     * @param close 结束字符串
     * @return 截取结果
     */
    public static String substringBetweenLast(final String str, final String open, final String close)
    {
        if (isEmpty(str) || isEmpty(open) || isEmpty(close))
        {
            return NULLSTR;
        }
        final int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND)
        {
            final int end = str.lastIndexOf(close);
            if (end != INDEX_NOT_FOUND)
            {
                return str.substring(start + open.length(), end);
            }
        }
        return NULLSTR;
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     * 
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params 参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params)
    {
        if (isEmpty(params) || isEmpty(template))
        {
            return template;
        }
        return StrFormatter.format(template, params);
    }

    /**
     * 是否为http(s)://开头
     * 
     * @param link 链接
     * @return 结果
     */
    public static boolean ishttp(String link)
    {
        return StringUtils.startsWithAny(link, Constants.HTTP, Constants.HTTPS);
    }

    /**
     * 字符串转set
     * 
     * @param str 字符串
     * @param sep 分隔符
     * @return set集合
     */
    public static final Set<String> str2Set(String str, String sep)
    {
        return new HashSet<String>(str2List(str, sep, true, false));
    }

    /**
     * 字符串转list
     * 
     * @param str 字符串
     * @param sep 分隔符
     * @return list集合
     */
    public static final List<String> str2List(String str, String sep)
    {
        return str2List(str, sep, true, false);
    }

    /**
     * 字符串转list
     * 
     * @param str 字符串
     * @param sep 分隔符
     * @param filterBlank 过滤纯空白
     * @param trim 去掉首尾空白
     * @return list集合
     */
    public static final List<String> str2List(String str, String sep, boolean filterBlank, boolean trim)
    {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isEmpty(str))
        {
            return list;
        }

        // 过滤空白字符串
        if (filterBlank && StringUtils.isBlank(str))
        {
            return list;
        }
        String[] split = str.split(sep);
        for (String string : split)
        {
            if (filterBlank && StringUtils.isBlank(string))
            {
                continue;
            }
            if (trim)
            {
                string = string.trim();
            }
            list.add(string);
        }

        return list;
    }

    /**
     * 检查子字符串是否存在
     *
     * @param seq 检查的字符串
     * @param searchSeq 查找的字符串
     * @return 结果
     */
    public static boolean contains(final CharSequence seq, final CharSequence searchSeq)
    {
        return Strings.CS.contains(seq, searchSeq);
    }

    /**
     * 判断给定的collection列表中是否包含数组array 判断给定的数组array中是否包含给定的元素value
     *
     * @param collection 给定的集合
     * @param array 给定的数组
     * @return boolean 结果
     */
    public static boolean containsAny(Collection<String> collection, String... array)
    {
        if (isEmpty(collection) || isEmpty(array))
        {
            return false;
        }
        else
        {
            for (String str : array)
            {
                if (collection.contains(str))
                {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串同时串忽略大小写
     *
     * @param cs 指定字符串
     * @param searchCharSequences 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     */
    public static boolean containsAnyIgnoreCase(CharSequence cs, CharSequence... searchCharSequences)
    {
        if (isEmpty(cs) || isEmpty(searchCharSequences))
        {
            return false;
        }
        for (CharSequence testStr : searchCharSequences)
        {
            if (containsIgnoreCase(cs, testStr))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否包含要搜索的字符串，忽略大小写
     *
     * @param str 要检查的字符串
     * @param searchStr 要查找的字符串
     * @return 如果包含要搜索的字符串（忽略大小写）则返回true，如果不包含或返回false
     */
    public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr)
    {
        return Strings.CI.contains(str, searchStr);
    }

    /**
     * 检查字符串是否以任意前缀开始
     *
     * @param sequence 要检查的字符串
     * @param searchStrings 区分大小写的字符串前缀数组
     * @return 结果
     */
    public static boolean startsWithAny(final CharSequence sequence, final CharSequence... searchStrings)
    {
        return Strings.CS.startsWithAny(sequence, searchStrings);
    }

    /**
     * 比较两个字符串是否相同
     *
     * @param cs1 第一个字符串
     * @param cs2 第二个字符串
     * @return 如果给定对象与字符串相等，则返回 true；否则返回 false
     */
    public static boolean equals(final CharSequence cs1, final CharSequence cs2)
    {
        return Strings.CS.equals(cs1, cs2);
    }

    /**
     * 替换字符串中所有匹配的字符
     *
     * @param text 要搜索和替换的文本
     * @param searchString 要搜索的字符串
     * @param replacement  用于替换的字符串
     * @return 处理完所有替换后的文本
     */
    public static String replace(final String text, final String searchString, final String replacement)
    {
        return Strings.CS.replace(text, searchString, replacement);
    }

    /**
     * 查找字符串首次出现位置的索引
     *
     * @param seq 要检查的字符串
     * @param searchSeq 要查找的字符串
     * @return 返回指定字符在字符串中第一次出现处的索引，如果此字符串中没有这样的字符，则返回 -1
     */
    public static int indexOf(final CharSequence seq, final CharSequence searchSeq)
    {
        return Strings.CS.indexOf(seq, searchSeq);
    }

    /**
     * 检查字符串是否以指定的后缀结尾
     *
     * @param str 要检查的字符
     * @param suffix 要检查的后缀
     * @return 若参数与该字符串末尾相符 true;否则 false
     */
    public static boolean endsWith(final CharSequence str, final CharSequence suffix)
    {
        return Strings.CS.endsWith(str, suffix);
    }

    /**
     * 将给定的字符串与数组进行比较
     *
     * @param string 要比较的字符串
     * @param searchStrings 字符串数组
     * @return 如果字符串等于（区分大小写）{@code searchStrings}中的任意其他元素，则返回true；如果{@code searchStrings}为null或不包含匹配项，则返回false
     */
    public static boolean equalsAny(final CharSequence string, final CharSequence... searchStrings)
    {
        return Strings.CS.equalsAny(string, searchStrings);
    }

    /**
     * 检查一个字符串是否以任意提供的区分大小写的后缀结尾。
     *
     * @param sequence 要检查的字符串
     * @param searchStrings 要查找的区分大小写的字符串数组
     * @return 如果输入参数{@code sequence}为null且未提供任何{@code searchStrings}，或者输入{@code sequence}以任意提供的区分大小写的{@code searchStrings}结尾，则返回{@code true}。
     */
    public static boolean endsWithAny(final CharSequence sequence, final CharSequence... searchStrings)
    {
        return Strings.CS.endsWithAny(sequence, searchStrings);
    }

    /**
     * 不区分大小写地检查字符序列是否以指定的后缀结尾
     *
     * @param str 要检查的字符序列
     * @param suffix 要查找的后缀
     * @return 如果字符序列以该后缀结尾（不区分大小写），或两者均为{@code null}，则返回{@code true}
     */
    public static boolean endsWithIgnoreCase(final CharSequence str, final CharSequence suffix)
    {
        return Strings.CI.endsWith(str, suffix);
    }

    /**
     * 指定范围内查找字符串,忽略大小写
     *
     * @param str 要检查的字符串
     * @param searchStr 要查找的字符串
     * @return 搜索字符串的第一个索引，如果未找到匹配项则返回 -1
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr)
    {
        return Strings.CI.indexOf(str, searchStr);
    }

    /**
     * Compares given {@code string} to a CharSequences vararg of {@code searchStrings},
     * returning {@code true} if the {@code string} is equal to any of the {@code searchStrings}, ignoring case.
     *
     * @param string to compare, may be {@code null}.
     * @param searchStrings a vararg of strings, may be {@code null}.
     * @return {@code true} if the string is equal (case-insensitive) to any other element of {@code searchStrings};
     */
    public static boolean equalsAnyIgnoreCase(final CharSequence string, final CharSequence... searchStrings)
    {
        return Strings.CI.equalsAny(string, searchStrings);
    }

    /**
     * 驼峰转下划线命名
     */
    public static String toUnderScoreCase(String str)
    {
        if (str == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase = true;
        // 当前字符是否大写
        boolean curreCharIsUpperCase = true;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            if (i > 0)
            {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            }
            else
            {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1))
            {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase)
            {
                sb.append(SEPARATOR);
            }
            else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase)
            {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 是否包含字符串
     * 
     * @param str 验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs)
    {
        if (str != null && strs != null)
        {
            for (String s : strs)
            {
                if (str.equalsIgnoreCase(trim(s)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 删除最后一个字符串
     *
     * @param str 输入字符串
     * @param spit 以什么类型结尾的
     * @return 截取后的字符串
     */
    public static String lastStringDel(String str, String spit)
    {
        if (!StringUtils.isEmpty(str) && str.endsWith(spit))
        {
            return str.subSequence(0, str.length() - 1).toString();
        }
        return str;
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。 例如：HELLO_WORLD->HelloWorld
     * 
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String convertToCamelCase(String name)
    {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty())
        {
            // 没必要转换
            return "";
        }
        else if (!name.contains("_"))
        {
            // 不含下划线，仅将首字母大写
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split("_");
        for (String camel : camels)
        {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty())
            {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * 驼峰式命名法
     * 例如：user_name->userName
     */
    public static String toCamelCase(String s)
    {
        if (s == null)
        {
            return null;
        }
        if (s.indexOf(SEPARATOR) == -1)
        {
            return s;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);

            if (c == SEPARATOR)
            {
                upperCase = true;
            }
            else if (upperCase)
            {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            }
            else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     * 
     * @param str 指定字符串
     * @param strs 需要检查的字符串数组
     * @return 是否匹配
     */
    public static boolean matches(String str, List<String> strs)
    {
        if (isEmpty(str) || isEmpty(strs))
        {
            return false;
        }
        for (String pattern : strs)
        {
            if (isMatch(pattern, str))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断url是否与规则配置: 
     * ? 表示单个字符; 
     * * 表示一层路径内的任意字符串，不可跨层级; 
     * ** 表示任意层路径;
     * 
     * @param pattern 匹配规则
     * @param url 需要匹配的url
     * @return
     */
    public static boolean isMatch(String pattern, String url)
    {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj)
    {
        return (T) obj;
    }

    /**
     * 数字左边补齐0，使之达到指定长度。注意，如果数字转换为字符串后，长度大于size，则只保留 最后size个字符。
     * 
     * @param num 数字对象
     * @param size 字符串指定长度
     * @return 返回数字的字符串格式，该字符串为指定长度。
     */
    public static final String padl(final Number num, final int size)
    {
        return padl(num.toString(), size, '0');
    }

    /**
     * 字符串左补齐。如果原始字符串s长度大于size，则只保留最后size个字符。
     * 
     * @param s 原始字符串
     * @param size 字符串指定长度
     * @param c 用于补齐的字符
     * @return 返回指定长度的字符串，由原字符串左补齐或截取得到。
     */
    public static final String padl(final String s, final int size, final char c)
    {
        final StringBuilder sb = new StringBuilder(size);
        if (s != null)
        {
            final int len = s.length();
            if (s.length() <= size)
            {
                for (int i = size - len; i > 0; i--)
                {
                    sb.append(c);
                }
                sb.append(s);
            }
            else
            {
                return s.substring(len - size, len);
            }
        }
        else
        {
            for (int i = size; i > 0; i--)
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args)
    {
        int passed = 0;
        int failed = 0;

        // ========== isEmpty / isNotEmpty ==========
        System.out.println("=== isEmpty / isNotEmpty ===");

        // null
        if (isEmpty((String) null) && !isNotEmpty((String) null)) { passed++; System.out.println("  PASS isEmpty(null)=true, isNotEmpty(null)=false"); }
        else { failed++; System.out.println("  FAIL isEmpty(null)/isNotEmpty(null)"); }

        // empty string
        if (isEmpty("") && !isNotEmpty("")) { passed++; System.out.println("  PASS isEmpty(\"\")=true, isNotEmpty(\"\")=false"); }
        else { failed++; System.out.println("  FAIL isEmpty(\"\")/isNotEmpty(\"\")"); }

        // whitespace string
        if (isEmpty("   ") && !isNotEmpty("   ")) { passed++; System.out.println("  PASS isEmpty(\"   \")=true, isNotEmpty(\"   \")=false"); }
        else { failed++; System.out.println("  FAIL isEmpty(\"   \")/isNotEmpty(\"   \")"); }

        // normal string
        if (!isEmpty("abc") && isNotEmpty("abc")) { passed++; System.out.println("  PASS isEmpty(\"abc\")=false, isNotEmpty(\"abc\")=true"); }
        else { failed++; System.out.println("  FAIL isEmpty(\"abc\")/isNotEmpty(\"abc\")"); }

        // Chinese
        if (!isEmpty("中文") && isNotEmpty("中文")) { passed++; System.out.println("  PASS isEmpty(\"中文\")=false, isNotEmpty(\"中文\")=true"); }
        else { failed++; System.out.println("  FAIL isEmpty(\"中文\")/isNotEmpty(\"中文\")"); }

        // emoji
        if (!isEmpty("😀") && isNotEmpty("😀")) { passed++; System.out.println("  PASS isEmpty(\"😀\")=false, isNotEmpty(\"😀\")=true"); }
        else { failed++; System.out.println("  FAIL isEmpty(\"😀\")/isNotEmpty(\"😀\")"); }

        // Chinese with whitespace
        if (!isEmpty("  你好  ") && isNotEmpty("  你好  ")) { passed++; System.out.println("  PASS isEmpty(\"  你好  \")=false, isNotEmpty(\"  你好  \")=true"); }
        else { failed++; System.out.println("  FAIL isEmpty(\"  你好  \")/isNotEmpty(\"  你好  \")"); }

        // emoji with whitespace
        if (!isEmpty("  😀  ") && isNotEmpty("  😀  ")) { passed++; System.out.println("  PASS isEmpty(\"  😀  \")=false, isNotEmpty(\"  😀  \")=true"); }
        else { failed++; System.out.println("  FAIL isEmpty(\"  😀  \")/isNotEmpty(\"  😀  \")"); }

        // ========== substring(str, start) ==========
        System.out.println("\n=== substring(str, start) ===");

        // null
        if ("".equals(substring(null, 0))) { passed++; System.out.println("  PASS substring(null, 0)=\"\""); }
        else { failed++; System.out.println("  FAIL substring(null, 0) expected \"\", got \"" + substring(null, 0) + "\""); }

        // empty string
        if ("".equals(substring("", 0))) { passed++; System.out.println("  PASS substring(\"\", 0)=\"\""); }
        else { failed++; System.out.println("  FAIL substring(\"\", 0) expected \"\", got \"" + substring("", 0) + "\""); }

        // normal start=0
        if ("abc".equals(substring("abc", 0))) { passed++; System.out.println("  PASS substring(\"abc\", 0)=\"abc\""); }
        else { failed++; System.out.println("  FAIL substring(\"abc\", 0) expected \"abc\", got \"" + substring("abc", 0) + "\""); }

        // normal start>0
        if ("bc".equals(substring("abc", 1))) { passed++; System.out.println("  PASS substring(\"abc\", 1)=\"bc\""); }
        else { failed++; System.out.println("  FAIL substring(\"abc\", 1) expected \"bc\", got \"" + substring("abc", 1) + "\""); }

        // negative start
        if ("c".equals(substring("abc", -1))) { passed++; System.out.println("  PASS substring(\"abc\", -1)=\"c\""); }
        else { failed++; System.out.println("  FAIL substring(\"abc\", -1) expected \"c\", got \"" + substring("abc", -1) + "\""); }

        // negative start beyond length
        if ("abc".equals(substring("abc", -100))) { passed++; System.out.println("  PASS substring(\"abc\", -100)=\"abc\""); }
        else { failed++; System.out.println("  FAIL substring(\"abc\", -100) expected \"abc\", got \"" + substring("abc", -100) + "\""); }

        // start beyond length
        if ("".equals(substring("abc", 100))) { passed++; System.out.println("  PASS substring(\"abc\", 100)=\"\""); }
        else { failed++; System.out.println("  FAIL substring(\"abc\", 100) expected \"\", got \"" + substring("abc", 100) + "\""); }

        // Chinese: "中文测试", start=2 -> "测试"
        if ("测试".equals(substring("中文测试", 2))) { passed++; System.out.println("  PASS substring(\"中文测试\", 2)=\"测试\""); }
        else { failed++; System.out.println("  FAIL substring(\"中文测试\", 2) expected \"测试\", got \"" + substring("中文测试", 2) + "\""); }

        // Chinese: negative start
        if ("测试".equals(substring("中文测试", -2))) { passed++; System.out.println("  PASS substring(\"中文测试\", -2)=\"测试\""); }
        else { failed++; System.out.println("  FAIL substring(\"中文测试\", -2) expected \"测试\", got \"" + substring("中文测试", -2) + "\""); }

        // emoji: "😀😁😂", start=0 -> "😀😁😂"
        if ("😀😁😂".equals(substring("😀😁😂", 0))) { passed++; System.out.println("  PASS substring(\"😀😁😂\", 0)=\"😀😁😂\""); }
        else { failed++; System.out.println("  FAIL substring(\"😀😁😂\", 0) expected \"😀😁😂\", got \"" + substring("😀😁😂", 0) + "\""); }

        // emoji: "😀😁😂", start=2 -> "😂" (each emoji = 2 Java chars)
        if ("😂".equals(substring("😀😁😂", 2))) { passed++; System.out.println("  PASS substring(\"😀😁😂\", 2)=\"😂\""); }
        else { failed++; System.out.println("  FAIL substring(\"😀😁😂\", 2) expected \"😂\", got \"" + substring("😀😁😂", 2) + "\""); }

        // multi-byte truncation: "a😀b", start=1 -> "😀b" (position 1 splits surrogate pair)
        {
            String result = substring("a😀b", 1);
            String expected = "😀b";
            if (expected.equals(result)) { passed++; System.out.println("  PASS substring(\"a😀b\", 1)=\"😀b\" (emoji intact)"); }
            else { failed++; System.out.println("  FAIL substring(\"a😀b\", 1) surrogate split, expected \"" + expected + "\", got \"" + result + "\" (len=" + result.length() + ")"); }
        }

        // ========== substring(str, start, end) ==========
        System.out.println("\n=== substring(str, start, end) ===");

        // null
        if ("".equals(substring(null, 0, 1))) { passed++; System.out.println("  PASS substring(null, 0, 1)=\"\""); }
        else { failed++; System.out.println("  FAIL substring(null, 0, 1) expected \"\", got \"" + substring(null, 0, 1) + "\""); }

        // empty string
        if ("".equals(substring("", 0, 0))) { passed++; System.out.println("  PASS substring(\"\", 0, 0)=\"\""); }
        else { failed++; System.out.println("  FAIL substring(\"\", 0, 0) expected \"\", got \"" + substring("", 0, 0) + "\""); }

        // normal range
        if ("ab".equals(substring("abc", 0, 2))) { passed++; System.out.println("  PASS substring(\"abc\", 0, 2)=\"ab\""); }
        else { failed++; System.out.println("  FAIL substring(\"abc\", 0, 2) expected \"ab\", got \"" + substring("abc", 0, 2) + "\""); }

        // negative indices
        if ("b".equals(substring("abc", -2, -1))) { passed++; System.out.println("  PASS substring(\"abc\", -2, -1)=\"b\""); }
        else { failed++; System.out.println("  FAIL substring(\"abc\", -2, -1) expected \"b\", got \"" + substring("abc", -2, -1) + "\""); }

        // start > end
        if ("".equals(substring("hello", 3, 1))) { passed++; System.out.println("  PASS substring(\"hello\", 3, 1)=\"\""); }
        else { failed++; System.out.println("  FAIL substring(\"hello\", 3, 1) expected \"\", got \"" + substring("hello", 3, 1) + "\""); }

        // end beyond length
        if ("abc".equals(substring("abc", 0, 100))) { passed++; System.out.println("  PASS substring(\"abc\", 0, 100)=\"abc\""); }
        else { failed++; System.out.println("  FAIL substring(\"abc\", 0, 100) expected \"abc\", got \"" + substring("abc", 0, 100) + "\""); }

        // start=end
        if ("".equals(substring("abc", 1, 1))) { passed++; System.out.println("  PASS substring(\"abc\", 1, 1)=\"\""); }
        else { failed++; System.out.println("  FAIL substring(\"abc\", 1, 1) expected \"\", got \"" + substring("abc", 1, 1) + "\""); }

        // Chinese: "中文测试", 0, 2 -> "中文"
        if ("中文".equals(substring("中文测试", 0, 2))) { passed++; System.out.println("  PASS substring(\"中文测试\", 0, 2)=\"中文\""); }
        else { failed++; System.out.println("  FAIL substring(\"中文测试\", 0, 2) expected \"中文\", got \"" + substring("中文测试", 0, 2) + "\""); }

        // Chinese: negative range
        if ("文测".equals(substring("中文测试", -3, -1))) { passed++; System.out.println("  PASS substring(\"中文测试\", -3, -1)=\"文测\""); }
        else { failed++; System.out.println("  FAIL substring(\"中文测试\", -3, -1) expected \"文测\", got \"" + substring("中文测试", -3, -1) + "\""); }

        // emoji: "😀😁😂", 0, 2 -> "😀" (first complete emoji)
        if ("😀".equals(substring("😀😁😂", 0, 2))) { passed++; System.out.println("  PASS substring(\"😀😁😂\", 0, 2)=\"😀\" (emoji intact)"); }
        else { failed++; System.out.println("  FAIL substring(\"😀😁😂\", 0, 2) expected \"😀\", got \"" + substring("😀😁😂", 0, 2) + "\""); }

        // multi-byte truncation: "😀😁😂", 0, 1 -> half emoji (surrogate split)
        {
            String result = substring("😀😁😂", 0, 1);
            // Verify it IS a broken surrogate: result.length() should be 1 and it should be a lone high surrogate
            if (result.length() == 1 && Character.isHighSurrogate(result.charAt(0)))
            {
                passed++;
                System.out.println("  PASS substring(\"😀😁😂\", 0, 1)=half-emoji (detected surrogate split, len=1)");
            }
            else
            {
                failed++;
                System.out.println("  FAIL substring(\"😀😁😂\", 0, 1) expected surrogate split, got len=" + result.length() + " chars=\"" + result + "\"");
            }
        }

        // multi-byte truncation: "a😀b", 0, 2 -> "a" + half emoji
        {
            String result = substring("a😀b", 0, 2);
            if (result.length() == 2 && result.charAt(0) == 'a' && Character.isHighSurrogate(result.charAt(1)))
            {
                passed++;
                System.out.println("  PASS substring(\"a😀b\", 0, 2) detected surrogate partial split (a + high surrogate)");
            }
            else
            {
                failed++;
                System.out.println("  FAIL substring(\"a😀b\", 0, 2) expected a+highSurrogate, got len=" + result.length());
            }
        }

        // multi-byte truncation: mixed Chinese + emoji "中😀国", 0, 2 -> "中" + half emoji
        {
            String result = substring("中😀国", 0, 2);
            if (result.length() == 2 && result.charAt(0) == '中' && Character.isHighSurrogate(result.charAt(1)))
            {
                passed++;
                System.out.println("  PASS substring(\"中😀国\", 0, 2) detected surrogate split (中+highSurrogate)");
            }
            else
            {
                failed++;
                System.out.println("  FAIL substring(\"中😀国\", 0, 2) expected split, got len=" + result.length());
            }
        }

        // ========== format ==========
        System.out.println("\n=== format ===");

        // null template
        if (null == format(null, "a")) { passed++; System.out.println("  PASS format(null, \"a\")=null"); }
        else { failed++; System.out.println("  FAIL format(null, \"a\") expected null, got \"" + format(null, "a") + "\""); }

        // empty template
        if ("".equals(format("", "a"))) { passed++; System.out.println("  PASS format(\"\", \"a\")=\"\""); }
        else { failed++; System.out.println("  FAIL format(\"\", \"a\") expected \"\", got \"" + format("", "a") + "\""); }

        // null params
        if ("hello".equals(format("hello", (Object[]) null))) { passed++; System.out.println("  PASS format(\"hello\", null) preserves template"); }
        else { failed++; System.out.println("  FAIL format(\"hello\", null) expected \"hello\", got \"" + format("hello", (Object[]) null) + "\""); }

        // empty params
        if ("hello".equals(format("hello"))) { passed++; System.out.println("  PASS format(\"hello\") preserves template (no params)"); }
        else { failed++; System.out.println("  FAIL format(\"hello\") expected \"hello\", got \"" + format("hello") + "\""); }

        // normal format
        if ("hello world".equals(format("{} {}", "hello", "world"))) { passed++; System.out.println("  PASS format(\"{} {}\", \"hello\", \"world\")=\"hello world\""); }
        else { failed++; System.out.println("  FAIL format(\"{} {}\", \"hello\", \"world\"): got \"" + format("{} {}", "hello", "world") + "\""); }

        // format with Chinese
        if ("你好 世界".equals(format("{} {}", "你好", "世界"))) { passed++; System.out.println("  PASS format(\"{} {}\", \"你好\", \"世界\")=\"你好 世界\""); }
        else { failed++; System.out.println("  FAIL format with Chinese: got \"" + format("{} {}", "你好", "世界") + "\""); }

        // format with emoji
        if ("😀 😁".equals(format("{} {}", "😀", "😁"))) { passed++; System.out.println("  PASS format(\"{} {}\", \"😀\", \"😁\")=\"😀 😁\""); }
        else { failed++; System.out.println("  FAIL format with emoji: got \"" + format("{} {}", "😀", "😁") + "\""); }

        // format with mixed Chinese + emoji
        if ("你好😀世界".equals(format("{}{}{}", "你好", "😀", "世界"))) { passed++; System.out.println("  PASS format(\"{}{}{}\", \"你好\", \"😀\", \"世界\")=\"你好😀世界\""); }
        else { failed++; System.out.println("  FAIL format mixed: got \"" + format("{}{}{}", "你好", "😀", "世界") + "\""); }

        // template with no placeholders
        if ("no placeholders".equals(format("no placeholders", "a", "b"))) { passed++; System.out.println("  PASS format(\"no placeholders\", \"a\", \"b\")=\"no placeholders\""); }
        else { failed++; System.out.println("  FAIL format no placeholders: got \"" + format("no placeholders", "a", "b") + "\""); }

        // template with escaped braces via StrFormatter
        System.out.println("  INFO format(\"this is \\\\{} for {}\", \"a\", \"b\")=\"" + format("this is \\{} for {}", "a", "b") + "\"");

        // ========== Summary ==========
        int total = passed + failed;
        System.out.println("\n============================================");
        System.out.println("  Self-check completed.");
        System.out.println("  Total: " + total + ", Passed: " + passed + ", Failed: " + failed);
        System.out.println("============================================");
        if (failed > 0)
        {
            System.exit(1);
        }
    }
}
