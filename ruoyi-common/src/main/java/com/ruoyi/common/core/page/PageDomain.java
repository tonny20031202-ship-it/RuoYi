package com.ruoyi.common.core.page;

import java.util.Locale;
import java.util.regex.Pattern;
import com.ruoyi.common.utils.StringUtils;

/**
 * 分页数据
 * 
 * @author ruoyi
 */
public class PageDomain
{
    private static final int DEFAULT_PAGE_NUM = 1;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final String SORT_ASC = "asc";

    private static final String SORT_DESC = "desc";

    private static final Pattern ORDER_BY_COLUMN_PATTERN = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)*$");

    /** 当前记录起始索引 */
    private Integer pageNum;

    /** 每页显示记录数 */
    private Integer pageSize;

    /** 排序列 */
    private String orderByColumn;

    /** 排序的方向desc或者asc */
    private String isAsc = SORT_ASC;

    /** 分页参数合理化 */
    private Boolean reasonable = true;

    public String getOrderBy()
    {
        String orderByColumn = getOrderByColumn();
        if (StringUtils.isEmpty(orderByColumn))
        {
            return "";
        }
        return StringUtils.toUnderScoreCase(orderByColumn) + " " + getIsAsc();
    }

    public Integer getPageNum()
    {
        return normalizePageValue(pageNum, DEFAULT_PAGE_NUM);
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return normalizePageValue(pageSize, DEFAULT_PAGE_SIZE);
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn()
    {
        String orderByColumn = StringUtils.trim(this.orderByColumn);
        if (StringUtils.isEmpty(orderByColumn))
        {
            return "";
        }
        return ORDER_BY_COLUMN_PATTERN.matcher(orderByColumn).matches() ? orderByColumn : "";
    }

    public void setOrderByColumn(String orderByColumn)
    {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc()
    {
        String isAsc = StringUtils.trim(this.isAsc).toLowerCase(Locale.ROOT);
        return StringUtils.equalsAny(isAsc, SORT_ASC, SORT_DESC) ? isAsc : SORT_ASC;
    }

    public void setIsAsc(String isAsc)
    {
        this.isAsc = isAsc;
    }

    public Boolean getReasonable()
    {
        if (StringUtils.isNull(reasonable))
        {
            return Boolean.TRUE;
        }
        return reasonable;
    }

    public void setReasonable(Boolean reasonable)
    {
        this.reasonable = reasonable;
    }

    private Integer normalizePageValue(Integer value, int defaultValue)
    {
        if (StringUtils.isNull(value) || value < 1)
        {
            return defaultValue;
        }
        return value;
    }
}
