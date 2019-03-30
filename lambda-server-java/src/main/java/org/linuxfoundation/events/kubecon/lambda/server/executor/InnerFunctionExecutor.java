package org.linuxfoundation.events.kubecon.lambda.server.executor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.linuxfoundation.events.kubecon.lambda.context.Context;
import org.linuxfoundation.events.kubecon.lambda.server.exception.FunctionException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class InnerFunctionExecutor {

    private String functionClass;
    private String functionName;
    private ObjectWriter responseJsonWriter;
    private ObjectReader requestJsonReader;
    private Object functionObject;
    private Method functionMethod;

    public void execute(InputStream input, OutputStream output, Context ctx)
            throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object functionParameter = this.requestJsonReader.readValue(input);
        Object responseObject = this.functionMethod.invoke(this.functionObject, functionParameter, ctx);
        this.responseJsonWriter.writeValue(output, responseObject);
    }

    private Method findMethodAndCheckSignature(Class<?> clazz, String methodName) {

        Method[] allMethods = clazz.getDeclaredMethods();

        Method theMethod = null;
        //Check that the method exists
        for (Method currentMethod : allMethods) {
            if (currentMethod.getName().equals(methodName)) {
                theMethod = currentMethod;
                break;
            }
        }


        //Check method signature
        if (theMethod != null) {
            //Two parameters
            if (theMethod.getParameterTypes().length != 2) {
                throw new FunctionException("Invalid functions definition");
            } else {
                //Second one is the context
                Class<?> context = theMethod.getParameterTypes()[1];
                if (!context.isAssignableFrom(Context.class)) {
                    throw new FunctionException("Invalid functions definition. Missing context parameter");
                }
                return theMethod;
            }
        }


        throw new FunctionException("Method not found " + methodName + " on " + clazz.getName());

    }

    private InnerFunctionExecutor(String theFunctionClass, String theFunctionName) {
        this.functionClass = theFunctionClass;
        this.functionName = theFunctionName;

        try {

            //Instantiate the class with the function to be executed
            Class<?> clazz = Class.forName(this.functionClass);

            this.functionMethod = findMethodAndCheckSignature(clazz, this.functionName);

            Class<?> functionParameterClass = this.functionMethod.getParameterTypes()[0];


            ObjectMapper objectMapper = new ObjectMapper();
            this.requestJsonReader = objectMapper.readerFor(functionParameterClass);
            this.responseJsonWriter = objectMapper.writer();

            this.functionObject = clazz.getConstructor().newInstance();

        } catch (FunctionException le) {
            throw le;
        } catch (Exception e) {
            throw new FunctionException("Function could not be initialized", e);
        }
    }

    public static final class Builder {
        private String builderFunctionName;
        private String builderFunctionClass;

        public static Builder create() {
            return new Builder();
        }

        public Builder functionName(String functionName) {
            this.builderFunctionName = functionName;
            return this;
        }

        public Builder functionClass(String functionClass) {
            this.builderFunctionClass = functionClass;
            return this;
        }

        public InnerFunctionExecutor newFunctionExecutor() {
            return new InnerFunctionExecutor(this.builderFunctionClass, this.builderFunctionName);
        }
    }

}
