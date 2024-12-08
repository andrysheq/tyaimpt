package org.example.util;

import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {
    private Map<String, Boolean> variables = new HashMap<>();

    public Boolean getVariable(String name) {
        return variables.get(name);
    }

    public void setVariable(String name, Boolean value) {
        variables.put(name, value);
    }

    public Map<String, Boolean> getVariables() {
        return variables;
    }
}




