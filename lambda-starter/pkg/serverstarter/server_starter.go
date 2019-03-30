package serverstarter

import (
	"bytes"
	"io"
	"os"
	"os/exec"
)

//"org.linuxfoundation.events.kubecon.lambda.server.bootstrap.FunctionServerBootstraper"
//classPath
func StartAndWait(mainClass string, classPath string) error {
	var stdBuffer bytes.Buffer
	mw := io.MultiWriter(os.Stdout, &stdBuffer)

	serverCommand := exec.Command("java", "-cp", classPath, mainClass)
	serverCommand.Stdout = mw
	serverCommand.Stderr = mw
	err := serverCommand.Run()
	if err != nil {
		return err
	}

	return nil
}
