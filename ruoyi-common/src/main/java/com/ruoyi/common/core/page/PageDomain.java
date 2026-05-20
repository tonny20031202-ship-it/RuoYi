package com.ruoyi.common.core.page;

import com.ruoyi.common.utils.StringUtils;

/**
 * 分页数据
 * 
 * @author ruoyi
 */
public class PageDomain
{
    /** 当前记录起始索引 */
    private Integer pageNum;

    /** 每页显示记录数 */
    private Integer pageSize;

    /** 排序列 */
    private String orderByColumn;

    /** 排序的方向desc或者asc */
    private String isAsc = "asc";

    /** 分页参数合理化 */
    private Boolean reasonable = true;

    /** 每页显示记录数的最小值 */
    private static final int MIN_PAGE_SIZE = 1;

    /** 每页显示记录数的最大值 */
    private static final int MAX_PAGE_SIZE = 1000;

    /** 排序字段仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序） */
    private static final String ORDER_BY_COLUMN_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    public String getOrderBy()
    {
        if (StringUtils.isEmpty(orderByColumn))
        {
            return "";
        }
        return StringUtils.toUnderScoreCase(orderByColumn) + " " + isAsc;
    }

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        if (pageNum == null || pageNum < 1)
        {
            this.pageNum = 1;
        }
        else
        {
            this.pageNum = pageNum;
        }
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        if (pageSize == null)
        {
            this.pageSize = 10;
        }
        else if (pageSize < MIN_PAGE_SIZE)
        {
            this.pageSize = MIN_PAGE_SIZE;
        }
        else if (pageSize > MAX_PAGE_SIZE)
        {
            this.pageSize = MAX_PAGE_SIZE;
        }
        else
        {
            this.pageSize = pageSize;
        }
    }

    public String getOrderByColumn()
    {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn)
    {
        if (StringUtils.isNotEmpty(orderByColumn) && isValidOrderByColumn(orderByColumn))
        {
            this.orderByColumn = orderByColumn;
        }
        else
        {
            this.orderByColumn = null;
        }
    }

    public String getIsAsc()
    {
        return isAsc;
    }

    public void setIsAsc(String isAsc)
    {
        if (StringUtils.isNotEmpty(isAsc))
        {
            String lowerCaseAsc = isAsc.trim().toLowerCase();
            if ("asc".equals(lowerCaseAsc) || "desc".equals(lowerCaseAsc))
            {
                this.isAsc = lowerCaseAsc;
            }
            else
            {
                this.isAsc = "asc";
            }
        }
        else
        {
            this.isAsc = "asc";
        }
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

    /**
     * 验证排序列是否合法
     */
    private boolean isValidOrderByColumn(String value)
    {
        return value.matches(ORDER_BY_COLUMN_PATTERN);
    }
}
