package org.linuxfoundation.events.kubecon.lambda.server.executor;

import org.linuxfoundation.events.kubecon.lambda.server.exception.FunctionException;

public class FunctionExecutor {
    private static InnerFunctionExecutor executor;

    public static final void setup(String functionFullName) {


        int lastPoint = functionFullName.lastIndexOf(".");
        if (lastPoint == -1) {
            throw new FunctionException("Invalid functionName [" + functionFullName + "]");
        }

        String functionClass = functionFullName.substring(0, lastPoint);
        String functionName = functionFullName.substring(lastPoint + 1);

        FunctionExecutor.executor = InnerFunctionExecutor.Builder.create().functionClass(functionClass).functionName(functionName).newFunctionExecutor();
    }


    public static InnerFunctionExecutor instance() {
        return executor;
    }
}
