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

    // ==================== 轻量级测试入口（仅限本文件，不依赖 JUnit） ====================

    /**
     * 轻量级测试入口，覆盖 IPv4、IPv6（含压缩格式）、localhost、X-Forwarded-For 多级代理伪造、非法地址。
     * 直接运行 main 即可，输出包含通过/失败统计。
     */
    public static void main(String[] args)
    {
        int passed = 0;
        int failed = 0;

        System.out.println("========================================");
        System.out.println("  IpUtils 轻量级测试");
        System.out.println("========================================\n");

        // ---------- getIpAddr ----------
        System.out.println("--- getIpAddr ---");

        // 1. null request
        String r = getIpAddr(null);
        if ("unknown".equals(r)) { passed++; sop("PASS", "null request => 'unknown'"); }
        else { failed++; sop("FAIL", "null request: expect 'unknown', got '" + r + "'"); }

        // 2. 正常 RemoteAddr IPv4
        r = getIpAddr(mockReq(null, "192.168.1.100"));
        if ("192.168.1.100".equals(r)) { passed++; sop("PASS", "RemoteAddr IPv4 => 192.168.1.100"); }
        else { failed++; sop("FAIL", "RemoteAddr IPv4: expect '192.168.1.100', got '" + r + "'"); }

        // 3. RemoteAddr IPv6 完整格式 localhost -> 应转为 127.0.0.1
        r = getIpAddr(mockReq(null, "0:0:0:0:0:0:0:1"));
        if ("127.0.0.1".equals(r)) { passed++; sop("PASS", "RemoteAddr IPv6 full localhost => 127.0.0.1"); }
        else { failed++; sop("FAIL", "RemoteAddr IPv6 full localhost: expect '127.0.0.1', got '" + r + "'"); }

        // 4. RemoteAddr IPv6 压缩格式 ::1（未经转换原样保留）
        r = getIpAddr(mockReq(null, "::1"));
        if ("::1".equals(r)) { passed++; sop("PASS", "RemoteAddr IPv6 compressed ::1 => '::1'"); }
        else { failed++; sop("FAIL", "RemoteAddr IPv6 compressed ::1: expect '::1', got '" + r + "'"); }

        // 5. x-forwarded-for 单级代理
        r = getIpAddr(mockReq(h("x-forwarded-for", "10.0.0.1"), "192.168.1.1"));
        if ("10.0.0.1".equals(r)) { passed++; sop("PASS", "X-Forwarded-For single => 10.0.0.1"); }
        else { failed++; sop("FAIL", "X-Forwarded-For single: expect '10.0.0.1', got '" + r + "'"); }

        // 6. x-forwarded-for 多级代理（模拟：client -> proxy1 -> proxy2）
        r = getIpAddr(mockReq(h("x-forwarded-for", "client,proxy1,proxy2"), "10.0.0.1"));
        if ("client".equals(r)) { passed++; sop("PASS", "X-Forwarded-For multi => first non-unknown 'client'"); }
        else { failed++; sop("FAIL", "X-Forwarded-For multi: expect 'client', got '" + r + "'"); }

        // 7. x-forwarded-for 伪造 unknown 打头（模拟攻击者注入 unknown）
        r = getIpAddr(mockReq(h("x-forwarded-for", "unknown,attacker,real-client"), "10.0.0.1"));
        if ("attacker".equals(r)) { passed++; sop("PASS", "X-Forwarded-For unknown-first => 'attacker'"); }
        else { failed++; sop("FAIL", "X-Forwarded-For unknown-first: expect 'attacker', got '" + r + "'"); }

        // 8. Proxy-Client-IP 头回退
        r = getIpAddr(mockReq(h("Proxy-Client-IP", "172.16.0.1"), "10.0.0.1"));
        if ("172.16.0.1".equals(r)) { passed++; sop("PASS", "Proxy-Client-IP fallback => 172.16.0.1"); }
        else { failed++; sop("FAIL", "Proxy-Client-IP fallback: expect '172.16.0.1', got '" + r + "'"); }

        // 9. X-Forwarded-For（大写 F）fallback
        r = getIpAddr(mockReq(h("X-Forwarded-For", "10.10.0.1"), "10.0.0.1"));
        if ("10.10.0.1".equals(r)) { passed++; sop("PASS", "X-Forwarded-For(caps) fallback => 10.10.0.1"); }
        else { failed++; sop("FAIL", "X-Forwarded-For(caps) fallback: expect '10.10.0.1', got '" + r + "'"); }

        // 10. WL-Proxy-Client-IP 头回退
        r = getIpAddr(mockReq(h("WL-Proxy-Client-IP", "192.168.0.1"), "10.0.0.1"));
        if ("192.168.0.1".equals(r)) { passed++; sop("PASS", "WL-Proxy-Client-IP fallback => 192.168.0.1"); }
        else { failed++; sop("FAIL", "WL-Proxy-Client-IP fallback: expect '192.168.0.1', got '" + r + "'"); }

        // 11. X-Real-IP 头回退
        r = getIpAddr(mockReq(h("X-Real-IP", "172.31.0.1"), "10.0.0.1"));
        if ("172.31.0.1".equals(r)) { passed++; sop("PASS", "X-Real-IP fallback => 172.31.0.1"); }
        else { failed++; sop("FAIL", "X-Real-IP fallback: expect '172.31.0.1', got '" + r + "'"); }

        // 12. 全部头为 unknown -> 回退到 RemoteAddr
        r = getIpAddr(mockReq(h("x-forwarded-for", "unknown"), "10.0.0.99"));
        if ("10.0.0.99".equals(r)) { passed++; sop("PASS", "All headers unknown => RemoteAddr 10.0.0.99"); }
        else { failed++; sop("FAIL", "All headers unknown: expect '10.0.0.99', got '" + r + "'"); }

        // ---------- getMultistageReverseProxyIp ----------
        System.out.println("\n--- getMultistageReverseProxyIp ---");

        r = getMultistageReverseProxyIp("client,proxy1,proxy2");
        if ("client".equals(r)) { passed++; sop("PASS", "multi-proxy => 'client'"); }
        else { failed++; sop("FAIL", "multi-proxy: expect 'client', got '" + r + "'"); }

        r = getMultistageReverseProxyIp("unknown,real-ip,proxy");
        if ("real-ip".equals(r)) { passed++; sop("PASS", "unknown-first => 'real-ip'"); }
        else { failed++; sop("FAIL", "unknown-first: expect 'real-ip', got '" + r + "'"); }

        r = getMultistageReverseProxyIp("single-ip");
        if ("single-ip".equals(r)) { passed++; sop("PASS", "single ip => 'single-ip'"); }
        else { failed++; sop("FAIL", "single ip: expect 'single-ip', got '" + r + "'"); }

        r = getMultistageReverseProxyIp(null);
        if ("".equals(r)) { passed++; sop("PASS", "null => ''"); }
        else { failed++; sop("FAIL", "null: expect '', got '" + r + "'"); }

        // ---------- internalIp ----------
        System.out.println("\n--- internalIp ---");

        if (internalIp("10.0.0.1")) { passed++; sop("PASS", "internalIp 10.x => true"); }
        else { failed++; sop("FAIL", "internalIp 10.x: expect true"); }

        if (internalIp("172.16.0.1")) { passed++; sop("PASS", "internalIp 172.16.x => true"); }
        else { failed++; sop("FAIL", "internalIp 172.16.x: expect true"); }

        if (internalIp("172.31.255.255")) { passed++; sop("PASS", "internalIp 172.31.x => true"); }
        else { failed++; sop("FAIL", "internalIp 172.31.x: expect true"); }

        if (internalIp("192.168.0.1")) { passed++; sop("PASS", "internalIp 192.168.x => true"); }
        else { failed++; sop("FAIL", "internalIp 192.168.x: expect true"); }

        if (internalIp("127.0.0.1")) { passed++; sop("PASS", "internalIp 127.0.0.1 => true"); }
        else { failed++; sop("FAIL", "internalIp 127.0.0.1: expect true"); }

        if (!internalIp("172.32.0.1")) { passed++; sop("PASS", "internalIp 172.32.x(外) => false"); }
        else { failed++; sop("FAIL", "internalIp 172.32.x(外): expect false"); }

        if (!internalIp("8.8.8.8")) { passed++; sop("PASS", "internalIp 8.8.8.8(公网) => false"); }
        else { failed++; sop("FAIL", "internalIp 8.8.8.8(公网): expect false"); }

        // ---------- isIP ----------
        System.out.println("\n--- isIP ---");

        if (isIP("192.168.1.1")) { passed++; sop("PASS", "isIP 192.168.1.1 => true"); }
        else { failed++; sop("FAIL", "isIP 192.168.1.1: expect true"); }

        if (!isIP("256.0.0.1")) { passed++; sop("PASS", "isIP 256.0.0.1(溢出) => false"); }
        else { failed++; sop("FAIL", "isIP 256.0.0.1(溢出): expect false"); }

        if (!isIP("not-an-ip")) { passed++; sop("PASS", "isIP 'not-an-ip' => false"); }
        else { failed++; sop("FAIL", "isIP 'not-an-ip': expect false"); }

        if (!isIP("")) { passed++; sop("PASS", "isIP '' => false"); }
        else { failed++; sop("FAIL", "isIP '': expect false"); }

        if (!isIP(null)) { passed++; sop("PASS", "isIP null => false"); }
        else { failed++; sop("FAIL", "isIP null: expect false"); }

        // ---------- isUnknown ----------
        System.out.println("\n--- isUnknown ---");

        if (isUnknown(null)) { passed++; sop("PASS", "isUnknown null => true"); }
        else { failed++; sop("FAIL", "isUnknown null: expect true"); }

        if (isUnknown("")) { passed++; sop("PASS", "isUnknown '' => true"); }
        else { failed++; sop("FAIL", "isUnknown '': expect true"); }

        if (isUnknown("unknown")) { passed++; sop("PASS", "isUnknown 'unknown' => true"); }
        else { failed++; sop("FAIL", "isUnknown 'unknown': expect true"); }

        if (isUnknown("UNKNOWN")) { passed++; sop("PASS", "isUnknown 'UNKNOWN'(大小写) => true"); }
        else { failed++; sop("FAIL", "isUnknown 'UNKNOWN'(大小写): expect true"); }

        if (!isUnknown("real-client")) { passed++; sop("PASS", "isUnknown 'real-client' => false"); }
        else { failed++; sop("FAIL", "isUnknown 'real-client': expect false"); }

        // ---------- textToNumericFormatV4（非法地址覆盖） ----------
        System.out.println("\n--- textToNumericFormatV4 ---");

        byte[] b = textToNumericFormatV4("192.168.1.1");
        if (b != null && b.length == 4 && b[0] == (byte) 192 && b[1] == (byte) 168 && b[2] == (byte) 1 && b[3] == (byte) 1)
        { passed++; sop("PASS", "textToNumV4 192.168.1.1 => valid bytes"); }
        else { failed++; sop("FAIL", "textToNumV4 192.168.1.1: invalid result"); }

        b = textToNumericFormatV4("256.0.0.1");
        if (b == null) { passed++; sop("PASS", "textToNumV4 256.0.0.1(溢出) => null"); }
        else { failed++; sop("FAIL", "textToNumV4 256.0.0.1(溢出): expect null"); }

        b = textToNumericFormatV4("");
        if (b == null) { passed++; sop("PASS", "textToNumV4 '' => null"); }
        else { failed++; sop("FAIL", "textToNumV4 '': expect null"); }

        b = textToNumericFormatV4("not.an.ip");
        if (b == null) { passed++; sop("PASS", "textToNumV4 'not.an.ip'(非法) => null"); }
        else { failed++; sop("FAIL", "textToNumV4 'not.an.ip'(非法): expect null"); }

        // ---------- IPv6 补充 ----------
        System.out.println("\n--- IPv6 补充 ---");

        // 非 localhost IPv6 通过 getIpAddr RemoteAddr 传入
        r = getIpAddr(mockReq(null, "2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
        if ("2001:0db8:85a3:0000:0000:8a2e:0370:7334".equals(r))
        { passed++; sop("PASS", "RemoteAddr full IPv6 => 原样保留"); }
        else { failed++; sop("FAIL", "RemoteAddr full IPv6: expect original, got '" + r + "'"); }

        r = getIpAddr(mockReq(null, "fe80::1"));
        if ("fe80::1".equals(r))
        { passed++; sop("PASS", "RemoteAddr IPv6 fe80::1 => 原样保留"); }
        else { failed++; sop("FAIL", "RemoteAddr IPv6 fe80::1: expect 'fe80::1', got '" + r + "'"); }

        // ---------- 结果汇总 ----------
        System.out.println("\n========================================");
        System.out.println("  总计: 通过 " + passed + " / 失败 " + failed);
        if (failed > 0)
        {
            System.out.println("  结果: *** 存在失败用例! ***");
        }
        else
        {
            System.out.println("  结果: 全部通过!");
        }
        System.out.println("========================================");
    }

    private static void sop(String tag, String msg)
    {
        System.out.println("  [" + tag + "] " + msg);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> java.util.Map<K, V> h(K k1, V v1)
    {
        java.util.Map<K, V> m = new java.util.HashMap<>();
        m.put(k1, v1);
        return m;
    }

    private static HttpServletRequest mockReq(java.util.Map<String, String> headers, String remoteAddr)
    {
        return (HttpServletRequest) java.lang.reflect.Proxy.newProxyInstance(
                IpUtils.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("getHeader".equals(name) && args != null && args.length == 1)
                    {
                        return headers != null ? headers.get((String) args[0]) : null;
                    }
                    if ("getRemoteAddr".equals(name))
                    {
                        return remoteAddr;
                    }
                    if ("toString".equals(name))
                    {
                        return "MockReq[headers=" + headers + ", remoteAddr=" + remoteAddr + "]";
                    }
                    if ("hashCode".equals(name))
                    {
                        return System.identityHashCode(proxy);
                    }
                    if ("equals".equals(name))
                    {
                        return proxy == args[0];
                    }
                    return null;
                });
    }
}