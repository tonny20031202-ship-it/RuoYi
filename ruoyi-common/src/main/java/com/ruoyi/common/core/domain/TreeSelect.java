package com.ruoyi.common.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TreeSelect implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String label;

    private Long parentId;

    private List<TreeSelect> children = new ArrayList<>();

    private Map<String, Object> params;

    public TreeSelect()
    {
    }

    public TreeSelect(Long id, String label, Long parentId)
    {
        this.id = id;
        this.label = label;
        this.parentId = parentId;
    }

    public TreeSelect(Long id, String label, Long parentId, Map<String, Object> params)
    {
        this.id = id;
        this.label = label;
        this.parentId = parentId;
        this.params = params;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public List<TreeSelect> getChildren()
    {
        return children;
    }

    public void setChildren(List<TreeSelect> children)
    {
        if (children == null)
        {
            this.children = new ArrayList<>();
        }
        else
        {
            this.children = new ArrayList<>(children);
        }
    }

    public Map<String, Object> getParams()
    {
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null)
            return false;
        TreeSelect other = (TreeSelect) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
