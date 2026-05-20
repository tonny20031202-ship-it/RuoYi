package com.ruoyi.common.core.page;

import com.ruoyi.common.utils.StringUtils;

/**
 * 分页数据
 * 
 * @author ruoyi
 */
public class PageDomain
{
    private static final Integer DEFAULT_PAGE_NUM = 1;

    private static final Integer DEFAULT_PAGE_SIZE = 10;

    private static final Integer MAX_PAGE_SIZE = 2000;

    private static final String COLUMN_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    private static final String DEFAULT_IS_ASC = "asc";

    /** 当前记录起始索引 */
    private Integer pageNum;

    /** 每页显示记录数 */
    private Integer pageSize;

    /** 排序列 */
    private String orderByColumn;

    /** 排序的方向desc或者asc */
    private String isAsc = DEFAULT_IS_ASC;

    /** 分页参数合理化 */
    private Boolean reasonable = true;

    public String getOrderBy()
    {
        if (StringUtils.isEmpty(orderByColumn))
        {
            return "";
        }
        if (!orderByColumn.matches(COLUMN_PATTERN))
        {
            return "";
        }
        return StringUtils.toUnderScoreCase(orderByColumn) + " " + getIsAsc();
    }

    public Integer getPageNum()
    {
        if (StringUtils.isNull(pageNum) || pageNum < 1)
        {
            return DEFAULT_PAGE_NUM;
        }
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        if (StringUtils.isNull(pageSize) || pageSize < 1)
        {
            return DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE)
        {
            return MAX_PAGE_SIZE;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn()
    {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn)
    {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc()
    {
        if (StringUtils.isEmpty(isAsc))
        {
            return DEFAULT_IS_ASC;
        }
        String normalized = isAsc.trim().toLowerCase();
        if ("asc".equals(normalized) || "desc".equals(normalized))
        {
            return normalized;
        }
        return DEFAULT_IS_ASC;
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
}
