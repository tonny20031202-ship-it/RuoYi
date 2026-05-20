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
     * 轻量级测试入口 - 验证IpUtils功能
     */
    public static void main(String[] args)
    {
        System.out.println("========== IpUtils 轻量级测试开始 ==========\n");
        int totalTests = 0;
        int failedTests = 0;

        // 测试1: 测试 getMultistageReverseProxyIp 方法
        System.out.println("----- 测试 1: getMultistageReverseProxyIp -----");
        String[][] proxyTests = {
            {"192.168.1.1, 10.0.0.1, 172.16.0.1", "192.168.1.1", "正常多级代理"},
            {"unknown, 192.168.1.1, 10.0.0.1", "192.168.1.1", "第一个为unknown"},
            {"192.168.1.1, unknown, 10.0.0.1", "192.168.1.1", "中间有unknown"},
            {"unknown, unknown, 192.168.1.1", "192.168.1.1", "前两个为unknown"},
            {"unknown, unknown, unknown", "unknown", "全为unknown"},
            {"  192.168.1.1  , 10.0.0.1  ", "192.168.1.1", "带空格"},
        };
        for (String[] test : proxyTests) {
            totalTests++;
            String result = getMultistageReverseProxyIp(test[0]);
            boolean passed = result.equals(test[1]);
            if (!passed) failedTests++;
            System.out.printf("[%s] %s: 输入=\"%s\", 期望=\"%s\", 实际=\"%s\"%n",
                passed ? "PASS" : "FAIL", test[2], test[0], test[1], result);
        }
        System.out.println();

        // 测试2: 测试 isIP 方法
        System.out.println("----- 测试 2: isIP (IPv4校验) -----");
        String[][] ipTests = {
            {"192.168.1.1", "true", "正常IPv4"},
            {"0.0.0.0", "true", "0.0.0.0"},
            {"255.255.255.255", "true", "广播地址"},
            {"256.0.0.1", "false", "超出范围"},
            {"192.168.1", "false", "不完整"},
            {"192.168.1.1.1", "false", "过多段"},
            {"abc.def.ghi.jkl", "false", "非数字"},
            {"", "false", "空字符串"},
            {null, "false", "null"},
        };
        for (String[] test : ipTests) {
            totalTests++;
            boolean result = isIP(test[0]);
            boolean expected = Boolean.parseBoolean(test[1]);
            boolean passed = result == expected;
            if (!passed) failedTests++;
            System.out.printf("[%s] %s: 输入=\"%s\", 期望=%s, 实际=%s%n",
                passed ? "PASS" : "FAIL", test[2], test[0], expected, result);
        }
        System.out.println();

        // 测试3: 测试 isUnknown 方法
        System.out.println("----- 测试 3: isUnknown -----");
        String[][] unknownTests = {
            {"unknown", "true", "unknown小写"},
            {"UNKNOWN", "true", "unknown大写"},
            {"Unknown", "true", "unknown混合"},
            {"", "true", "空字符串"},
            {"   ", "true", "空白字符串"},
            {null, "true", "null"},
            {"192.168.1.1", "false", "正常IP"},
        };
        for (String[] test : unknownTests) {
            totalTests++;
            boolean result = isUnknown(test[0]);
            boolean expected = Boolean.parseBoolean(test[1]);
            boolean passed = result == expected;
            if (!passed) failedTests++;
            System.out.printf("[%s] %s: 输入=\"%s\", 期望=%s, 实际=%s%n",
                passed ? "PASS" : "FAIL", test[2], test[0], expected, result);
        }
        System.out.println();

        // 测试4: 模拟 HttpServletRequest 测试 getIpAddr
        System.out.println("----- 测试 4: getIpAddr (模拟请求) -----");
        MockHttpRequest request = new MockHttpRequest();
        
        // 测试4.1: 无代理头，使用 remoteAddr
        request.clear();
        request.setRemoteAddr("192.168.1.100");
        totalTests++;
        String result = getIpAddr(request);
        boolean passed = result.equals("192.168.1.100");
        if (!passed) failedTests++;
        System.out.printf("[%s] 无代理头: 期望=\"192.168.1.100\", 实际=\"%s\"%n", passed ? "PASS" : "FAIL", result);

        // 测试4.2: X-Forwarded-For 头
        request.clear();
        request.setHeader("X-Forwarded-For", "10.0.0.5, 172.16.0.1, 192.168.1.1");
        request.setRemoteAddr("127.0.0.1");
        totalTests++;
        result = getIpAddr(request);
        passed = result.equals("10.0.0.5");
        if (!passed) failedTests++;
        System.out.printf("[%s] X-Forwarded-For多级代理: 期望=\"10.0.0.5\", 实际=\"%s\"%n", passed ? "PASS" : "FAIL", result);

        // 测试4.3: x-forwarded-for (小写)
        request.clear();
        request.setHeader("x-forwarded-for", "192.168.10.5");
        totalTests++;
        result = getIpAddr(request);
        passed = result.equals("192.168.10.5");
        if (!passed) failedTests++;
        System.out.printf("[%s] x-forwarded-for(小写): 期望=\"192.168.10.5\", 实际=\"%s\"%n", passed ? "PASS" : "FAIL", result);

        // 测试4.4: Proxy-Client-IP
        request.clear();
        request.setHeader("Proxy-Client-IP", "172.16.5.5");
        totalTests++;
        result = getIpAddr(request);
        passed = result.equals("172.16.5.5");
        if (!passed) failedTests++;
        System.out.printf("[%s] Proxy-Client-IP: 期望=\"172.16.5.5\", 实际=\"%s\"%n", passed ? "PASS" : "FAIL", result);

        // 测试4.5: WL-Proxy-Client-IP
        request.clear();
        request.setHeader("WL-Proxy-Client-IP", "10.10.10.10");
        totalTests++;
        result = getIpAddr(request);
        passed = result.equals("10.10.10.10");
        if (!passed) failedTests++;
        System.out.printf("[%s] WL-Proxy-Client-IP: 期望=\"10.10.10.10\", 实际=\"%s\"%n", passed ? "PASS" : "FAIL", result);

        // 测试4.6: X-Real-IP
        request.clear();
        request.setHeader("X-Real-IP", "192.168.99.99");
        totalTests++;
        result = getIpAddr(request);
        passed = result.equals("192.168.99.99");
        if (!passed) failedTests++;
        System.out.printf("[%s] X-Real-IP: 期望=\"192.168.99.99\", 实际=\"%s\"%n", passed ? "PASS" : "FAIL", result);

        // 测试4.7: IPv6 localhost (0:0:0:0:0:0:0:1)
        request.clear();
        request.setRemoteAddr("0:0:0:0:0:0:0:1");
        totalTests++;
        result = getIpAddr(request);
        passed = result.equals("127.0.0.1");
        if (!passed) failedTests++;
        System.out.printf("[%s] IPv6 localhost: 期望=\"127.0.0.1\", 实际=\"%s\"%n", passed ? "PASS" : "FAIL", result);

        // 测试4.8: 代理头为 unknown
        request.clear();
        request.setHeader("X-Forwarded-For", "unknown");
        request.setRemoteAddr("203.0.113.1");
        totalTests++;
        result = getIpAddr(request);
        passed = result.equals("203.0.113.1");
        if (!passed) failedTests++;
        System.out.printf("[%s] X-Forwarded-For为unknown: 期望=\"203.0.113.1\", 实际=\"%s\"%n", passed ? "PASS" : "FAIL", result);

        System.out.println();

        // 测试5: 测试 textToNumericFormatV4
        System.out.println("----- 测试 5: textToNumericFormatV4 -----");
        String[][] v4Tests = {
            {"192.168.1.1", "not null", "正常IPv4"},
            {"255.255.255.255", "not null", "最大IPv4"},
            {"0.0.0.0", "not null", "最小IPv4"},
            {"256.0.0.1", "null", "超出范围"},
            {"abc.def.ghi.jkl", "null", "非数字"},
            {"", "null", "空字符串"},
        };
        for (String[] test : v4Tests) {
            totalTests++;
            byte[] resultBytes = textToNumericFormatV4(test[0]);
            boolean expectedNotNull = "not null".equals(test[1]);
            boolean resultNotNull = resultBytes != null;
            passed = expectedNotNull == resultNotNull;
            if (!passed) failedTests++;
            System.out.printf("[%s] %s: 输入=\"%s\", 期望=%s, 实际=%s%n",
                passed ? "PASS" : "FAIL", test[2], test[0], test[1], resultNotNull ? "not null" : "null");
        }
        System.out.println();

        // 测试6: 测试 internalIp
        System.out.println("----- 测试 6: internalIp -----");
        String[][] internalTests = {
            {"127.0.0.1", "true", "localhost"},
            {"10.0.0.1", "true", "10.x.x.x"},
            {"172.16.0.1", "true", "172.16.x.x"},
            {"172.31.255.255", "true", "172.31.x.x"},
            {"192.168.1.1", "true", "192.168.x.x"},
            {"8.8.8.8", "false", "公网IP"},
            {"172.32.0.1", "false", "172.32.x.x不在内网范围"},
        };
        for (String[] test : internalTests) {
            totalTests++;
            boolean resultBool = internalIp(test[0]);
            boolean expectedBool = Boolean.parseBoolean(test[1]);
            passed = resultBool == expectedBool;
            if (!passed) failedTests++;
            System.out.printf("[%s] %s: 输入=\"%s\", 期望=%s, 实际=%s%n",
                passed ? "PASS" : "FAIL", test[2], test[0], expectedBool, resultBool);
        }
        System.out.println();

        // 测试总结
        System.out.println("========== 测试总结 ==========");
        System.out.printf("总测试数: %d%n", totalTests);
        System.out.printf("通过数: %d%n", totalTests - failedTests);
        System.out.printf("失败数: %d%n", failedTests);
        System.out.printf("通过率: %.2f%%%n", (totalTests - failedTests) * 100.0 / totalTests);
        System.out.println("==============================");
    }

    /**
     * HttpServletRequest 模拟类
     */
    static class MockHttpRequest implements HttpServletRequest
    {
        private java.util.Map<String, String> headers = new java.util.HashMap<>();
        private String remoteAddr;

        public void clear()
        {
            headers.clear();
            remoteAddr = null;
        }

        public void setHeader(String name, String value)
        {
            headers.put(name, value);
        }

        public void setRemoteAddr(String remoteAddr)
        {
            this.remoteAddr = remoteAddr;
        }

        @Override
        public String getHeader(String name)
        {
            return headers.get(name);
        }

        @Override
        public String getRemoteAddr()
        {
            return remoteAddr;
        }

        // 以下为 HttpServletRequest 接口的其他方法，均未实现
        @Override public String getAuthType() { return null; }
        @Override public String getContextPath() { return null; }
        @Override public String getMethod() { return null; }
        @Override public String getPathInfo() { return null; }
        @Override public String getPathTranslated() { return null; }
        @Override public String getQueryString() { return null; }
        @Override public String getRemoteUser() { return null; }
        @Override public String getRequestedSessionId() { return null; }
        @Override public String getRequestURI() { return null; }
        @Override public StringBuffer getRequestURL() { return null; }
        @Override public String getServletPath() { return null; }
        @Override public jakarta.servlet.http.HttpSession getSession(boolean create) { return null; }
        @Override public jakarta.servlet.http.HttpSession getSession() { return null; }
        @Override public boolean isRequestedSessionIdValid() { return false; }
        @Override public boolean isRequestedSessionIdFromCookie() { return false; }
        @Override public boolean isRequestedSessionIdFromURL() { return false; }
        @Override public java.security.Principal getUserPrincipal() { return null; }
        @Override public boolean isUserInRole(String role) { return false; }
        @Override public java.util.Enumeration<String> getAttributeNames() { return null; }
        @Override public Object getAttribute(String name) { return null; }
        @Override public void setAttribute(String name, Object o) { }
        @Override public void removeAttribute(String name) { }
        @Override public java.util.Locale getLocale() { return null; }
        @Override public java.util.Enumeration<java.util.Locale> getLocales() { return null; }
        @Override public boolean isSecure() { return false; }
        @Override public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) { return null; }
        @Override public String getRealPath(String path) { return null; }
        @Override public String getCharacterEncoding() { return null; }
        @Override public void setCharacterEncoding(String env) { }
        @Override public int getContentLength() { return 0; }
        @Override public long getContentLengthLong() { return 0; }
        @Override public String getContentType() { return null; }
        @Override public java.io.InputStream getInputStream() { return null; }
        @Override public java.util.Enumeration<String> getParameterNames() { return null; }
        @Override public String getParameter(String name) { return null; }
        @Override public String[] getParameterValues(String name) { return null; }
        @Override public java.util.Map<String, String[]> getParameterMap() { return null; }
        @Override public String getProtocol() { return null; }
        @Override public String getScheme() { return null; }
        @Override public String getServerName() { return null; }
        @Override public int getServerPort() { return 0; }
        @Override public java.io.BufferedReader getReader() { return null; }
        @Override public String getRemoteHost() { return null; }
        @Override public void setRemoteHost(String remoteHost) { }
        @Override public int getRemotePort() { return 0; }
        @Override public String getLocalName() { return null; }
        @Override public String getLocalAddr() { return null; }
        @Override public int getLocalPort() { return 0; }
        @Override public jakarta.servlet.ServletContext getServletContext() { return null; }
        @Override public jakarta.servlet.AsyncContext startAsync() { return null; }
        @Override public jakarta.servlet.AsyncContext startAsync(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response) { return null; }
        @Override public boolean isAsyncStarted() { return false; }
        @Override public boolean isAsyncSupported() { return false; }
        @Override public jakarta.servlet.AsyncContext getAsyncContext() { return null; }
        @Override public jakarta.servlet.DispatcherType getDispatcherType() { return null; }
        @Override public java.util.Enumeration<String> getHeaderNames() { return java.util.Collections.enumeration(headers.keySet()); }
        @Override public java.util.Enumeration<String> getHeaders(String name) { return null; }
        @Override public int getIntHeader(String name) { return 0; }
        @Override public long getDateHeader(String name) { return 0; }
        @Override public String changeSessionId() { return null; }
        @Override public boolean authenticate(jakarta.servlet.http.HttpServletResponse response) { return false; }
        @Override public void login(String username, String password) { }
        @Override public void logout() { }
        @Override public java.util.Collection<String> getParts() { return null; }
        @Override public jakarta.servlet.http.Part getPart(String name) { return null; }
        @Override public <T extends jakarta.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass) { return null; }
    }
}
