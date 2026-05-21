package com.ruoyi.common.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.ruoyi.common.core.domain.entity.SysMenu;

/**
 * TreeSelect树结构实体类
 * 
 * @author ruoyi
 */
public class TreeSelect implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private Long id;

    /** 节点名称 */
    private String label;

    /** 子节点 */
    private List<TreeSelect> children = new ArrayList<TreeSelect>();

    public TreeSelect()
    {
    }

    public TreeSelect(Long id, String label)
    {
        this.id = id;
        this.label = label;
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

    public List<TreeSelect> getChildren()
    {
        return children;
    }

    public void setChildren(List<TreeSelect> children)
    {
        this.children = children;
    }

    /**
     * 构建树结构，每次调用创建全新TreeSelect实例，避免子节点引用复用导致串数据
     */
    public static List<TreeSelect> build(List<SysMenu> menus)
    {
        List<TreeSelect> treeList = new ArrayList<TreeSelect>();
        if (menus == null)
        {
            return treeList;
        }
        for (SysMenu menu : menus)
        {
            if (menu.getParentId() == null || menu.getParentId() == 0L)
            {
                treeList.add(buildChild(menus, menu));
            }
        }
        return treeList;
    }

    private static TreeSelect buildChild(List<SysMenu> menus, SysMenu parent)
    {
        TreeSelect node = new TreeSelect(parent.getMenuId(), parent.getMenuName());
        List<TreeSelect> childNodes = new ArrayList<TreeSelect>();
        if (parent.getMenuId() != null)
        {
            for (SysMenu menu : menus)
            {
                if (menu.getParentId() != null && menu.getParentId().equals(parent.getMenuId()))
                {
                    childNodes.add(buildChild(menus, menu));
                }
            }
        }
        node.setChildren(childNodes);
        return node;
    }
}