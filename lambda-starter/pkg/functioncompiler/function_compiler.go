package functioncompiler

type FunctionCompiler interface {
	Compile(functionCode string, functionEntryPoint string) error
}
