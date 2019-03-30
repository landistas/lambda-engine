package functioncompiler

import (
	"errors"
	"fmt"
	"io/ioutil"
	"os"
	"os/exec"
	"strings"
	"time"
)

func NewJavaFunctionCompiler(compileClassPath string, buildDir string) *JavaFunctionCompiler {

	return &JavaFunctionCompiler{
		buildDir:         buildDir,
		compileClassPath: compileClassPath,
	}
}

type JavaFunctionCompiler struct {
	packageName      string
	className        string
	functionName     string
	buildDir         string
	compileClassPath string
	workDir          string
}

func (compiler *JavaFunctionCompiler) CalculateExecutionClassPath() string {
	return fmt.Sprintf("%s:%s", compiler.workDir, compiler.compileClassPath)
}

func (compiler *JavaFunctionCompiler) Compile(functionCode string, functionEntryPoint string) error {

	err := compiler.parseJavaEntryPoint(functionEntryPoint)
	if err != nil {
		return err
	}

	functionFilePath, err := compiler.setupBuildDir(functionCode)
	if err != nil {
		return err
	}

	command := exec.Command("javac", "-cp", compiler.compileClassPath, functionFilePath)

	javacOutput, err := command.CombinedOutput()
	if err != nil {
		return err
	} else {
		fmt.Println(fmt.Sprintf("Function compiled. Compiler output [%s]", javacOutput))
	}

	return nil
}

// parseJavaEntryPoint parse the functionEntryPoint and populate package, class and function name
func (compiler *JavaFunctionCompiler) parseJavaEntryPoint(functionEntryPoint string) error {
	functionNameIndex := strings.LastIndex(functionEntryPoint, ".")
	if functionNameIndex == -1 {
		return errors.New("invalid functionEntryPoint. Expected package.ClassName.functionName")
	}

	packageAndClass := functionEntryPoint[:functionNameIndex]
	compiler.functionName = functionEntryPoint[functionNameIndex+1:]

	classNameIndex := strings.LastIndex(packageAndClass, ".")
	if classNameIndex == -1 {
		return errors.New("invalid functionEntryPoint. Expected package.ClassName.functionName")
	}

	compiler.packageName = packageAndClass[:classNameIndex]
	compiler.className = packageAndClass[classNameIndex+1:]

	return nil
}

// setupBuildDir creates needed directories and create the file with the given code
// returns the path of the file with the function
func (compiler *JavaFunctionCompiler) setupBuildDir(functionCode string) (string, error) {
	workDir := fmt.Sprintf("%s/%d/src", compiler.buildDir, time.Now().UnixNano())
	compiler.workDir = workDir
	packageDir := fmt.Sprintf("%s/%s", workDir, compiler.packageName)

	fmt.Println(compiler.packageName)
	fmt.Println(compiler.className)
	fmt.Println(compiler.functionName)
	fmt.Println(packageDir)

	//TODO change 0777 permissions
	err := os.MkdirAll(packageDir, os.ModePerm)
	if err != nil {
		panic(err)
	}

	functionFilePath := fmt.Sprintf("%s/%s.java", packageDir, compiler.className)

	fmt.Println(functionFilePath)

	err = ioutil.WriteFile(functionFilePath, []byte(functionCode), os.ModePerm)
	if err != nil {
		return "", err
	}

	return functionFilePath, nil
}
