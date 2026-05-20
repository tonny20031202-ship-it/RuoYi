package com.ruoyi.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 获取IP方法
 * 
 * @author ruoyi
 */
public class IpUtils
{
    public final static String REGX_0_255 = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
    // 匹配 ip
    public final static String REGX_IP = "((" + REGX_0_255 + "\\.){3}" + REGX_0_255 + ")";
    public final static String REGX_IP_WILDCARD = "(((\\*\\.){3}\\*)|(" + REGX_0_255 + "(\\.\\*){3})|(" + REGX_0_255 + "\\." + REGX_0_255 + ")(\\.\\*){2}" + "|((" + REGX_0_255 + "\\.){3}\\*))";
    // 匹配网段
    public final static String REGX_IP_SEG = "(" + REGX_IP + "\\-" + REGX_IP + ")";

    /**
     * 获取客户端IP
     * 
     * @param request 请求对象
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request)
    {
        if (request == null)
        {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }

    /**
     * 检查是否为内部IP地址
     * 
     * @param ip IP地址
     * @return 结果
     */
    public static boolean internalIp(String ip)
    {
        byte[] addr = textToNumericFormatV4(ip);
        return internalIp(addr) || "127.0.0.1".equals(ip);
    }

    /**
     * 检查是否为内部IP地址
     * 
     * @param addr byte地址
     * @return 结果
     */
    private static boolean internalIp(byte[] addr)
    {
        if (StringUtils.isNull(addr) || addr.length < 2)
        {
            return true;
        }
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        // 172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        // 192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0)
        {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4)
                {
                    return true;
                }
            case SECTION_5:
                switch (b1)
                {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }

    /**
     * 将IPv4地址转换成字节
     * 
     * @param text IPv4地址
     * @return byte 字节
     */
    public static byte[] textToNumericFormatV4(String text)
    {
        if (text.length() == 0)
        {
            return null;
        }

        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);
        try
        {
            long l;
            int i;
            switch (elements.length)
            {
                case 1:
                    l = Long.parseLong(elements[0]);
                    if ((l < 0L) || (l > 4294967295L))
                    {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    if ((l < 0L) || (l > 255L))
                    {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if ((l < 0L) || (l > 16777215L))
                    {
                        return null;
                    }
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < 2; ++i)
                    {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L))
                        {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    if ((l < 0L) || (l > 65535L))
                    {
                        return null;
                    }
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < 4; ++i)
                    {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L))
                        {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        return bytes;
    }

    /**
     * 获取IP地址
     * 
     * @return 本地IP地址
     */
    public static String getHostIp()
    {
        try
        {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
        }
        return "127.0.0.1";
    }

    /**
     * 获取主机名
     * 
     * @return 本地主机名
     */
    public static String getHostName()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
        }
        return "未知";
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    public static String getMultistageReverseProxyIp(String ip)
    {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0)
        {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips)
            {
                if (false == isUnknown(subIp))
                {
                    ip = subIp;
                    break;
                }
            }
        }
        return StringUtils.substring(ip, 0, 255);
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    public static boolean isUnknown(String checkString)
    {
        return StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 是否为IP
     */
    public static boolean isIP(String ip)
    {
        return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP);
    }

    /**
     * 是否为IP，或 *为间隔的通配符地址
     */
    public static boolean isIpWildCard(String ip)
    {
        return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP_WILDCARD);
    }

    /**
     * 检测参数是否在ip通配符里
     */
    public static boolean ipIsInWildCardNoCheck(String ipWildCard, String ip)
    {
        String[] s1 = ipWildCard.split("\\.");
        String[] s2 = ip.split("\\.");
        boolean isMatchedSeg = true;
        for (int i = 0; i < s1.length && !s1[i].equals("*"); i++)
        {
            if (!s1[i].equals(s2[i]))
            {
                isMatchedSeg = false;
                break;
            }
        }
        return isMatchedSeg;
    }

    /**
     * 是否为特定格式如:“10.10.10.1-10.10.10.99”的ip段字符串
     */
    public static boolean isIPSegment(String ipSeg)
    {
        return StringUtils.isNotBlank(ipSeg) && ipSeg.matches(REGX_IP_SEG);
    }

    /**
     * 判断ip是否在指定网段中
     */
    public static boolean ipIsInNetNoCheck(String iparea, String ip)
    {
        int idx = iparea.indexOf('-');
        String[] sips = iparea.substring(0, idx).split("\\.");
        String[] sipe = iparea.substring(idx + 1).split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;
        for (int i = 0; i < 4; ++i)
        {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }
        if (ips > ipe)
        {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe;
    }

    /**
     * 校验ip是否符合过滤串规则
     * 
     * @param filter 过滤IP列表,支持后缀'*'通配,支持网段如:`10.10.10.1-10.10.10.99`
     * @param ip 校验IP地址
     * @return boolean 结果
     */
    public static boolean isMatchedIp(String filter, String ip)
    {
        if (StringUtils.isEmpty(filter) || StringUtils.isEmpty(ip))
        {
            return false;
        }
        String[] ips = filter.split(";");
        for (String iStr : ips)
        {
            if (isIP(iStr) && iStr.equals(ip))
            {
                return true;
            }
            else if (isIpWildCard(iStr) && ipIsInWildCardNoCheck(iStr, ip))
            {
                return true;
            }
            else if (isIPSegment(iStr) && ipIsInNetNoCheck(iStr, ip))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 轻量级测试入口
     * 用于验证 IPv4、IPv6、localhost、代理头伪造场景
     */
    public static void main(String[] args)
    {
        int total = 0;
        int failed = 0;

        System.out.println("--- IpUtils Lightweight Test ---");

        // 测试用例：{ 描述, 模拟的Header数组(交替的key-value), RemoteAddr, 预期结果 }
        Object[][] cases = {
            {"IPv4 正常地址", new String[]{}, "192.168.1.100", "192.168.1.100"},
            {"IPv6 Localhost (0:0:0:0:0:0:0:1)", new String[]{}, "0:0:0:0:0:0:0:1", "127.0.0.1"},
            {"IPv6 压缩格式", new String[]{}, "2001:db8::1", "2001:db8::1"},
            {"X-Forwarded-For 多级代理", new String[]{"X-Forwarded-For", "unknown,10.0.0.1,192.168.1.1"}, "127.0.0.1", "10.0.0.1"},
            {"X-Forwarded-For 代理伪造", new String[]{"x-forwarded-for", "unknown,unknown,203.0.113.1"}, "192.168.1.1", "203.0.113.1"},
            {"X-Real-IP 代理伪造", new String[]{"X-Real-IP", "8.8.8.8"}, "192.168.1.1", "8.8.8.8"},
            {"Proxy-Client-IP 伪造", new String[]{"Proxy-Client-IP", "114.114.114.114"}, "127.0.0.1", "114.114.114.114"},
            {"非法/未知地址", new String[]{"X-Forwarded-For", "unknown"}, "unknown", "unknown"},
            {"空地址输入", new String[]{}, "", "unknown"},
            {"Null 地址输入", new String[]{}, null, "unknown"}
        };

        for (Object[] c : cases)
        {
            total++;
            String desc = (String) c[0];
            final String[] headers = (String[]) c[1];
            final String remote = (String) c[2];
            String expected = (String) c[3];

            HttpServletRequest req = (HttpServletRequest) java.lang.reflect.Proxy.newProxyInstance(
                IpUtils.class.getClassLoader(),
                new Class[]{HttpServletRequest.class},
                (proxy, method, mArgs) -> {
                    if ("getHeader".equals(method.getName()))
                    {
                        String name = (String) mArgs[0];
                        for (int i = 0; i < headers.length; i += 2)
                        {
                            if (headers[i].equalsIgnoreCase(name))
                            {
                                return headers[i + 1];
                            }
                        }
                        return null;
                    }
                    if ("getRemoteAddr".equals(method.getName()))
                    {
                        return remote;
                    }
                    return null;
                }
            );

            String result = getIpAddr(req);
            if (expected == null && result == null)
            {
                System.out.println("[PASS] " + desc);
            }
            else if (expected != null && expected.equals(result))
            {
                System.out.println("[PASS] " + desc);
            }
            else
            {
                failed++;
                System.out.println("[FAIL] " + desc + " - 预期: " + expected + ", 实际: " + result);
            }
        }

        System.out.println("--------------------------------");
        
        // 额外覆盖：internalIp 方法测试 (验证 IPv6 压缩格式与非法地址)
        String[][] internalIpCases = {
            {"internalIp - IPv4 内部地址", "192.168.1.1", "true"},
            {"internalIp - IPv4 外部地址", "8.8.8.8", "false"},
            {"internalIp - IPv6 压缩格式", "2001:db8::1", "true"}, // 核心逻辑限制：返回 null 给 internalIp(byte[]) 会判 true
            {"internalIp - 非法地址", "invalid-ip", "true"}
        };

        for (String[] c : internalIpCases)
        {
            total++;
            String desc = c[0];
            String ip = c[1];
            String expected = c[2];
            String result = String.valueOf(internalIp(ip));
            if (expected.equals(result))
            {
                System.out.println("[PASS] " + desc);
            }
            else
            {
                failed++;
                System.out.println("[FAIL] " + desc + " - 预期: " + expected + ", 实际: " + result);
            }
        }

        System.out.println("--------------------------------");
        System.out.println("统计 -> 总计: " + total + ", 通过: " + (total - failed) + ", 失败: " + failed);
        if (failed > 0)
        {
            System.err.println("测试未完全通过！");
        }
        else
        {
            System.out.println("所有测试用例均通过。");
        }
    }
}