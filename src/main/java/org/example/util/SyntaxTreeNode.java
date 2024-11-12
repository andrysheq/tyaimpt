package org.example.util;

import java.util.ArrayList;
import java.util.List;

public class SyntaxTreeNode {
    private String value;  // The token or non-terminal value
    private List<SyntaxTreeNode> children;  // List of child nodes

    public SyntaxTreeNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(SyntaxTreeNode child) {
        this.children.add(child);
    }

    public String getValue() {
        return value;
    }

    public List<SyntaxTreeNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return value;
    }
}