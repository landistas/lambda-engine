package main

import (
	"fmt"
	"lambda-server-starter/pkg/functioncompiler"
	"lambda-server-starter/pkg/serverstarter"
	"os"
)

func main() {


	//TODO use viper+cobra
	functionCode := os.Getenv("FUNCTION_CODE")
	functionEntryPoint := os.Getenv("FUNCTION_ENTRYPOINT")
	functionServerClassPath := os.Getenv("FUNCTION_SERVER_CLASSPATH")
	compilerClassPath := os.Getenv("COMPILE_CLASSPATH")
	buildDir := os.Getenv("BUILD_DIR")
	mainClass := os.Getenv("MAIN_CLASS")
	javaCompiler := functioncompiler.NewJavaFunctionCompiler(compilerClassPath, buildDir)

	err := javaCompiler.Compile(functionCode, functionEntryPoint)
	if err != nil {
		panic(err)
	}

	serverClassPath := fmt.Sprintf("%s:%s", functionServerClassPath, javaCompiler.CalculateExecutionClassPath())
	fmt.Println(serverClassPath)

	err = serverstarter.StartAndWait(mainClass, serverClassPath)
	fmt.Println(err)

}

