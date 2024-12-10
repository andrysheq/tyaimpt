//package org.example.util;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SyntaxTreeNodeDeprecated {
//    private String value;
//    private List<SyntaxTreeNodeDeprecated> children;
//
//    public SyntaxTreeNodeDeprecated(String value) {
//        this.value = value;
//        this.children = new ArrayList<>();
//    }
//
//    public String getValue() {
//        return value;
//    }
//
//    public List<SyntaxTreeNodeDeprecated> getChildren() {
//        return children;
//    }
//
//    public void addChild(SyntaxTreeNodeDeprecated child) {
//        children.add(child);
//    }
//
//    @Override
//    public String toString() {
//        return toString(0);
//    }
//
//    private String toString(int level) {
//        StringBuilder sb = new StringBuilder("  ".repeat(level) + value + "\n");
//        for (SyntaxTreeNodeDeprecated child : children) {
//            sb.append(child.toString(level + 1));
//        }
//        return sb.toString();
//    }
//}
