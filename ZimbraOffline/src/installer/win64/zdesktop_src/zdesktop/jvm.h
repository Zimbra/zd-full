/*
 * 
 */

#ifndef JVM_H
#define JVM_H

#include "stdafx.h"
#include "cfg.h"
#include <jni.h>

typedef jint (JNICALL *CreateVMProc)(JavaVM **pvm, void **penv, void *args);

class VirtualMachine {
private:
    const static string jreKey;
    const static string jdkKey;

public:
    VirtualMachine(Config &c);
    ~VirtualMachine();
    bool Run();
    void Stop();
    bool IsRunning() { return state == Running; }
    string &LastError() { return last_err; }

protected:
    enum State {Running, Stopped, Failed};

    HANDLE thread_handle;
    DWORD  thread_id;
    JavaVM *jvm;
    JNIEnv *env;
    jclass mcls;
    jclass wcls;
    Config &cfg;
    State state;
    string last_err;
    static const char *err_file;

    CreateVMProc FindCreateJavaVM(const char *vmlibpath);
    string FindJava();
    string GetJVMPath(string);
    string GetRegistryKey(string, string);
    bool IsJREInstalled();
    bool IsJDKInstalled();
    void Log(string, ios_base::openmode);
    void RedirectIO();
    void CallMain();
    static DWORD WINAPI JvmThreadMain(LPVOID lpParam);
    static jint JNICALL zd_vfprintf(FILE *fp, const char *format, va_list args);
};

#endif JVM_H
