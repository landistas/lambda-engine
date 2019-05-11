#!/usr/bin/env bash

read -r -d '' FUNCTION_CODE << EndOfFunction
package chispas;

import org.linuxfoundation.events.kubecon.lambda.context.Context;

public class Chispas {

    public static class InData {
        private String foo;
        private String bar;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        public String toString() {
            return foo + " " + bar;
        }
    }

    public static class OutData {
        private String result;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    public OutData doChispas(InData in, Context ctx) {

        System.out.println(in);

        OutData out = new OutData();

        out.setResult(in.toString());

        return out;
    }
}
EndOfFunction


# echo $FUNCTION_CODE



docker run -d \
-p 9443:9443 \
-e FUNCTION_CODE="$FUNCTION_CODE" \
-e FUNCTION_ENTRYPOINT="chispas.Chispas.doChispas" \
-e functionName="chispas.Chispas.doChispas" \
-e COMPILE_CLASSPATH="/lambda-server/*" \
-e FUNCTION_SERVER_CLASSPATH="/lambda-server/*" \
-e BUILD_DIR="/tmp" \
-e MAIN_CLASS="org.linuxfoundation.events.kubecon.lambda.server.bootstrap.FunctionServerBootstraper" \
kubecon-barcelona-lambda-engine:0.1.0 starterd





