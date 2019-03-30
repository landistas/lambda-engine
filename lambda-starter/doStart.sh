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


echo $FUNCTION_CODE

export FUNCTION_CODE=$FUNCTION_CODE
export FUNCTION_ENTRYPOINT="chispas.Chispas.doChispas"
export functionName="chispas.Chispas.doChispas"


PWD=$PWD
PARENTDIR="$(dirname "$PWD")"

export COMPILE_CLASSPATH=${PARENTDIR}"/lambda-server-java/build/libs/*"
export FUNCTION_SERVER_CLASSPATH=${PARENTDIR}"/lambda-server-java/build/dependencieslib/*"
export BUILD_DIR="/tmp"
export MAIN_CLASS="org.linuxfoundation.events.kubecon.lambda.server.bootstrap.FunctionServerBootstraper"

go run cmd/starterd/starterd.go