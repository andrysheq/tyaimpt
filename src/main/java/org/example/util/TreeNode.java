package org.example.util;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private final String type;
    private final String value;
    private final List<TreeNode> children = new ArrayList<>();

    public TreeNode(String type) {
        this(type, null);
    }

    public TreeNode(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(type);
        if (value != null) {
            builder.append(": ").append(value);
        }
        for (TreeNode child : children) {
            builder.append("\n  ").append(child.toString().replace("\n", "\n  "));
        }
        return builder.toString();
    }
}
