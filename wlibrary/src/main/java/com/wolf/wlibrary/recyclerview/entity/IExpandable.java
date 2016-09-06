package com.wolf.wlibrary.recyclerview.entity;

import java.util.List;

/**
 * implement the interface if the item is expandable
 */
public interface IExpandable<T> {
    boolean isExpanded();
    void setExpanded(boolean expanded);
    List<T> getSubItems();
}
