/*
 * 
 */

#include "jvm.h"
#include <string>
#include <vector>
#include <iostream>
#include <fstream>

using namespace std;

const char * VirtualMachine::err_file = NULL;
const string VirtualMachine::jreKey = "SOFTWARE\\JavaSoft\\Java Runtime Environment";
const string VirtualMachine::jdkKey = "SOFTWARE\\JavaSoft\\Java Development Kit";
REGSAM registryKey;

VirtualMachine::VirtualMachine(Config &c) : cfg(c) {
    jvm = NULL;
    state = Stopped;

    if (err_file == NULL) {
        string ef = cfg.Get("error.file");
        if (!ef.empty())
            err_file = _strdup(ef.c_str());
    }
	Log("ZD is launched and searching for Java on your system", fstream :: out);

}

VirtualMachine::~VirtualMachine() {
    if (jvm)
        jvm->DestroyJavaVM();
}

bool VirtualMachine::Run() {
    thread_handle = CreateThread(NULL, 0, JvmThreadMain, (void *)this, 0, &thread_id);
    if (thread_handle == NULL) {
        last_err = "unable to create jvm thread";
        return false;
    }
    while (state == Stopped)
        Sleep(100);
    return state == Running;
}

void VirtualMachine::Stop() {
    jvm->AttachCurrentThread((void **)&env, NULL);
    jmethodID shutdown = env->GetStaticMethodID(wcls, "shutdown", "()V");
    env->CallStaticVoidMethod(wcls, shutdown, NULL);
}

void VirtualMachine::Log(string data, ios_base::openmode fileMode = fstream::app) {
    string outfile = cfg.Get("launch.file");

    ofstream file;
    file.open(outfile, fileMode);
    file << data << endl;
    file.close();
}

void VirtualMachine::RedirectIO() {
    string outfile = cfg.Get("redirect.file");
    if (!outfile.empty()) {
        jmethodID redirect = env->GetStaticMethodID(wcls, "redirect", "(Ljava/lang/String;)V");
        jstring str = env->NewStringUTF(outfile.c_str());
        env->CallStaticVoidMethod(wcls, redirect, str);
    }
}

static string GetKeyName(const char *prefix, int c) {
    char numstr[32];
    sprintf_s(numstr, "%d", ++c);
    return string(prefix) + numstr;
}

static size_t GetPropertyList(Config &cfg, const char *prefix, vector<string> &list) {
    int c = 0;
    string arg;
    while (!(arg = cfg.Get(GetKeyName(prefix, c))).empty()) {
        list.push_back(arg);
        c++;
    }
    return c;
}

void VirtualMachine::CallMain() {
    vector<string> arglist;
    size_t c = GetPropertyList(cfg, "app.arg.", arglist);

    jclass strcls = env->FindClass("java/lang/String");
    jobjectArray args = env->NewObjectArray(c == 0 ? 1 : c, strcls, NULL);
    for(size_t i = 0; i < c; i++) {
        env->SetObjectArrayElement(args, i, env->NewStringUTF(arglist[i].c_str()));
    }

    jmethodID main = env->GetStaticMethodID(mcls, "main", "([Ljava/lang/String;)V");
    env->CallStaticVoidMethod(mcls, main, args);
}

jint JNICALL VirtualMachine::zd_vfprintf(FILE *fp, const char *format, va_list args) {
    FILE *fs;
    fopen_s(&fs, err_file, "a");
    if (fs != NULL) {
        vfprintf(fs, format, args);
        fclose(fs);
    }
    return 1;
}

string VirtualMachine::GetRegistryKey(string keyPath, string key) {
    HKEY hKey = 0;
    BYTE data[512];
    DWORD szsize = 1024;
    memset(data,0,512);

    //Open registry path where key is stored
    LONG retValue = RegOpenKeyEx(HKEY_LOCAL_MACHINE, keyPath.c_str(), 0, registryKey, &hKey) ;

    if (retValue == ERROR_SUCCESS) {
        //Read key value
        LONG retValue1 = RegQueryValueEx(hKey, key.c_str(), 0, 0, (BYTE *)data, &szsize);
        if (retValue1 == ERROR_SUCCESS) {
            RegCloseKey(hKey);
            std::string value(reinterpret_cast< char const* >(data),sizeof(data));
            return value;
        } else {
            char data[256];
            sprintf_s(data,"Failed to read registry key %s in path %s", keyPath.c_str(), key.c_str());
            Log(data);
        }
    } else {
            char data[128];
            sprintf_s(data,"Failed to open registry %s", keyPath.c_str());
            Log(data);
    }
}

bool CanOpenRegistry(string registry) {
    HKEY hKey = 0;
    //Open registry path where key is stored
    LONG retValue = RegOpenKeyEx(HKEY_LOCAL_MACHINE, registry.c_str(), 0, registryKey, &hKey) ;

    if (retValue == ERROR_SUCCESS) {
        RegCloseKey(hKey);
        return true;
    }
    return false;
}

bool VirtualMachine::IsJREInstalled() {
    return CanOpenRegistry(jreKey);
}

bool VirtualMachine::IsJDKInstalled() {
    return CanOpenRegistry(jdkKey);
}

string GetJvmDllLocation(string javaHome) {
    string temp = javaHome;

    if(GetFileAttributes(temp.append("\\bin\\client\\jvm.dll").c_str()) != INVALID_FILE_ATTRIBUTES) {
        return temp;
    }

    temp = javaHome;
    if(GetFileAttributes(temp.append("\\bin\\server\\jvm.dll").c_str()) != INVALID_FILE_ATTRIBUTES) {
        return temp;
    }

    temp = javaHome;
    if(GetFileAttributes(temp.append("\\jre\\bin\\client\\jvm.dll").c_str()) != INVALID_FILE_ATTRIBUTES) {
        return temp;
    }

    temp = javaHome;
    if(GetFileAttributes(temp.append("\\jre\\bin\\server\\jvm.dll").c_str()) != INVALID_FILE_ATTRIBUTES) {
        return temp;
    }

    return "";
}

string VirtualMachine::GetJVMPath(string key) {
    string version = GetRegistryKey(key, "CurrentVersion");

    char numstr[32];
    sprintf_s(numstr, "Java version is : %s", version.c_str());
    Log(string(numstr));

    string javaHomeKey;
    javaHomeKey.append(key).append("\\").append(version.c_str());

    //Read value of JavaHome from registry
    string javaHome = GetRegistryKey(javaHomeKey, "JavaHome");
    Log("Value of JavaHome from regsitry is " + (string)javaHome.c_str());
    string path;
    path = GetJvmDllLocation(javaHome.c_str());
    Log("Location of jvm.dll is " + path);
    return path;
}

BOOL Is64BitWindows()
{
#if defined(_WIN64)
    return TRUE;  // 64-bit programs run only on Win64
#elif defined(_WIN32)
    // 32-bit programs run on both 32-bit and 64-bit Windows so must sniff
    BOOL f64 = FALSE;
    return IsWow64Process(GetCurrentProcess(), &f64) && f64;
#else
    return FALSE;
#endif
}

void CheckOsAndSetRegistryPath() {
	BOOL is64_OS = Is64BitWindows();
	registryKey = (is64_OS == TRUE) ? (KEY_READ | KEY_WOW64_32KEY) : (KEY_READ);
}

string VirtualMachine::FindJava() {
    CheckOsAndSetRegistryPath();
    //Check if JRE is installed
    if (IsJREInstalled()) {
        Log("JRE is found");
        return GetJVMPath(jreKey);
    } else if(IsJDKInstalled()) {
        Log("JDK is found");
        return GetJVMPath(jdkKey);
    } else {
        Log("JRE 32 bit is not found on your system.");
    }
    return "";
}

CreateVMProc VirtualMachine::FindCreateJavaVM(const char *vmlibpath) {
    HINSTANCE hVM = LoadLibrary(vmlibpath);
    return hVM == NULL ? NULL : (CreateVMProc)GetProcAddress(hVM, "JNI_CreateJavaVM");
}

DWORD WINAPI VirtualMachine::JvmThreadMain(LPVOID lpParam) {
    VirtualMachine *self = (VirtualMachine *)lpParam;

    vector<string> classpaths;
    size_t numcps = GetPropertyList(self->cfg, "java.classpath.", classpaths);
    vector<string> javaargs;
    size_t numjargs = GetPropertyList(self->cfg, "java.arg.", javaargs);
    
    size_t numopts = (numcps > 0 ? 1 : 0) + numjargs + 1; 
    JavaVMOption *options = new JavaVMOption[numopts];
    string cpstr = "-Djava.class.path=";
    size_t j = 0;
    if (numcps > 0) {
        for (size_t i = 0; i < numcps; i++) {
            if (i > 0)
                cpstr.append(";");
            cpstr.append(classpaths[i]);
        }
        options[j++].optionString = (char *)cpstr.c_str();
    }
    for (size_t i = 0; i < numjargs; i++) {
        options[j++].optionString = (char *)javaargs[i].c_str();
    }
    options[j].optionString = "vfprintf";
    options[j].extraInfo = zd_vfprintf;

    JavaVMInitArgs vm_args;
    vm_args.version = JNI_VERSION_1_6;
    vm_args.options = options;
    vm_args.nOptions = numopts;
    vm_args.ignoreUnrecognized = JNI_TRUE;

    /* Create Java VM */
    string jvmPath = self->FindJava();

    if (jvmPath.empty()) {
        self->last_err = "Zimbra Desktop (32-bit) requires a 32-bit Java SE Runtime Environment (JRE), which is not installed or is outdated. Please install JRE 1.6 (32-bit) or later.";
        goto error0;
    }

    CreateVMProc CreateVM = self->FindCreateJavaVM(jvmPath.c_str());

    if (CreateVM == NULL) {
        self->last_err = "can't get JNI_CreateJavaVM";
        goto error0;
    }

    jint res = (*CreateVM)(&(self->jvm), (void**)&(self->env), &vm_args);
    if (res < 0) {
        self->last_err = "can't create Java VM";
        goto error0;
    }

    self->mcls = self->env->FindClass(self->cfg.Get("java.main.class").c_str());
    if (self->mcls == NULL) {
        self->last_err = "main class not found";
        goto error1;
    }

    self->wcls = self->env->FindClass(self->cfg.Get("java.wrapper.class").c_str());
    if (self->wcls == NULL) {
        self->last_err = "wrapper class not found";
        goto error1;
    }

    self->state = Running; // set state before going into blocking java call
    self->RedirectIO();
    self->CallMain();

error1:
    if (self->env->ExceptionOccurred())
        self->env->ExceptionDescribe();

error0:
    self->state = self->state == Running ? Stopped : Failed;
    delete[] options;
    return 1;
}

