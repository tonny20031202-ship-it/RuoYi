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
     * 是否为特定格式如:"10.10.10.1-10.10.10.99"的ip段字符串
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

    public static void main(String[] args)
    {
        int passed = 0, failed = 0;
        System.out.println("========== IpUtils Lightweight Test ==========\n");

        passed += testCase("IPv4 normal", getIpAddr(createMockRequest(null, null, "192.168.1.100")), "192.168.1.100") ? 1 : (failed++, 0);
        passed += testCase("IPv4 localhost", getIpAddr(createMockRequest(null, null, "127.0.0.1")), "127.0.0.1") ? 1 : (failed++, 0);
        passed += testCase("IPv6 localhost expanded", getIpAddr(createMockRequest(null, null, "0:0:0:0:0:0:0:1")), "127.0.0.1") ? 1 : (failed++, 0);
        passed += testCase("IPv6 compressed loopback", getIpAddr(createMockRequest(null, null, "::1")), "127.0.0.1") ? 1 : (failed++, 0);
        passed += testCase("IPv6 full compressed", getIpAddr(createMockRequest(null, null, "::")), "127.0.0.1") ? 1 : (failed++, 0);
        passed += testCase("X-Forwarded-For single", getIpAddr(createMockRequest("X-Forwarded-For", null, "10.0.0.1")), "10.0.0.1") ? 1 : (failed++, 0);
        passed += testCase("X-Forwarded-For multi-level proxy", getIpAddr(createMockRequest("X-Forwarded-For", null, "192.168.1.1, 10.0.0.1, 172.16.0.1")), "192.168.1.1") ? 1 : (failed++, 0);
        passed += testCase("X-Forwarded-For with unknown", getIpAddr(createMockRequest("X-Forwarded-For", null, "unknown, 10.0.0.1, unknown")), "10.0.0.1") ? 1 : (failed++, 0);
        passed += testCase("Proxy-Client-IP header", getIpAddr(createMockRequest(null, "Proxy-Client-IP", "10.10.10.10")), "10.10.10.10") ? 1 : (failed++, 0);
        passed += testCase("Internal IP 10.x.x.x", internalIp("10.255.255.255"), true) ? 1 : (failed++, 0);
        passed += testCase("Internal IP 172.16.x.x", internalIp("172.16.0.1"), true) ? 1 : (failed++, 0);
        passed += testCase("Internal IP 192.168.x.x", internalIp("192.168.0.1"), true) ? 1 : (failed++, 0);
        passed += testCase("External IP", internalIp("8.8.8.8"), false) ? 1 : (failed++, 0);
        passed += testCase("Invalid IP format", textToNumericFormatV4("invalid"), null) ? 1 : (failed++, 0);
        passed += testCase("Out-of-range octet", textToNumericFormatV4("256.0.0.1"), null) ? 1 : (failed++, 0);
        passed += testCase("IPv6 invalid format", textToNumericFormatV4("2001:db8::gggg"), null) ? 1 : (failed++, 0);
        passed += testCase("Valid IPv4 bytes conversion", textToNumericFormatV4("192.168.1.1") != null, true) ? 1 : (failed++, 0);
        passed += testCase("isIP valid", isIP("192.168.1.1"), true) ? 1 : (failed++, 0);
        passed += testCase("isIP invalid", isIP("999.999.999.999"), false) ? 1 : (failed++, 0);
        passed += testCase("isMatchedIp single", isMatchedIp("192.168.1.1", "192.168.1.1"), true) ? 1 : (failed++, 0);
        passed += testCase("isMatchedIp wildcard", isMatchedIp("192.168.*.*", "192.168.5.5"), true) ? 1 : (failed++, 0);
        passed += testCase("isMatchedIp segment", isMatchedIp("192.168.1.1-192.168.1.10", "192.168.1.5"), true) ? 1 : (failed++, 0);

        System.out.println("\n========== Test Summary ==========");
        System.out.printf("Passed: %d | Failed: %d | Total: %d%n", passed, failed, passed + failed);
        System.out.println(failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED");
        System.out.println("==================================");
    }

    private static boolean testCase(String name, String actual, String expected)
    {
        boolean pass = expected.equals(actual);
        System.out.printf("[%s] %s: expected=[%s], actual=[%s] %s%n",
                pass ? "PASS" : "FAIL", name, expected, actual, pass ? "" : "❌");
        return pass;
    }

    private static boolean testCase(String name, boolean actual, boolean expected)
    {
        boolean pass = expected == actual;
        System.out.printf("[%s] %s: expected=[%s], actual=[%s] %s%n",
                pass ? "PASS" : "FAIL", name, expected, actual, pass ? "" : "❌");
        return pass;
    }

    private static Object createMockRequest(String headerName, String altHeaderName, String ipValue)
    {
        return new MockHttpServletRequestWrapper(headerName, altHeaderName, ipValue);
    }

    private static class MockHttpServletRequestWrapper
    {
        private final String headerValue;
        private final String altHeaderValue;
        private String remoteAddr;

        MockHttpServletRequestWrapper(String headerName, String altHeaderName, String ipValue)
        {
            if ("X-Forwarded-For".equals(headerName))
            {
                this.headerValue = ipValue;
                this.altHeaderValue = null;
                this.remoteAddr = "127.0.0.1";
            }
            else if ("Proxy-Client-IP".equals(altHeaderName))
            {
                this.headerValue = null;
                this.altHeaderValue = ipValue;
                this.remoteAddr = "127.0.0.1";
            }
            else
            {
                this.headerValue = null;
                this.altHeaderValue = null;
                this.remoteAddr = ipValue;
            }
        }

        public String getHeader(String name)
        {
            if ("x-forwarded-for".equalsIgnoreCase(name)) return headerValue;
            if ("X-Forwarded-For".equalsIgnoreCase(name)) return headerValue;
            if ("Proxy-Client-IP".equalsIgnoreCase(name)) return altHeaderValue;
            if ("WL-Proxy-Client-IP".equalsIgnoreCase(name)) return null;
            if ("X-Real-IP".equalsIgnoreCase(name)) return null;
            return null;
        }

        public String getRemoteAddr()
        {
            return remoteAddr;
        }
    }
}