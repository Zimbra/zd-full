/* -*- Mode: c; c-basic-offset: 4 -*- */
/*
 * 
 */
#include <jni.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <stdlib.h>
#include <grp.h>

#include "Process.h"
#include "zjniutil.h"

JNIEXPORT jint JNICALL
Java_com_zimbra_znative_Process_getuid0(JNIEnv *env, jclass clz)
{
    return getuid();
}

JNIEXPORT jint JNICALL
Java_com_zimbra_znative_Process_geteuid0(JNIEnv *env, jclass clz)
{
    return geteuid();
}

JNIEXPORT jint JNICALL
Java_com_zimbra_znative_Process_getgid0(JNIEnv *env, jclass clz)
{
    return getgid();
}

JNIEXPORT jint JNICALL
Java_com_zimbra_znative_Process_getegid0(JNIEnv *env, jclass clz)
{
    return getegid();
}

static void
SetPrivileges(JNIEnv *env, const char *username, uid_t uid, gid_t gid)
{
    if (geteuid() != 0) {
        /* Nothing to do - we are not running as root. */
        return;
    }

    if (setgid(gid) == -1) {
        char msg[256];
        snprintf(msg, sizeof(msg), "setgid(%d): %s", gid, strerror(errno));
        ZimbraThrowOFE(env, msg);
        return;
    }
    
    if (initgroups(username, gid) == -1) {
        char msg[256];
        snprintf(msg, sizeof(msg), "initgroups(%s, %d): %s", username, gid, 
                 strerror(errno));
        ZimbraThrowOFE(env, msg);
        return;
    }

    if (setuid(uid) == -1) {
        char msg[256];
        snprintf(msg, sizeof(msg), "setuid(%d): %s", uid, strerror(errno));
        ZimbraThrowOFE(env, msg);
        return;
    }
}

JNIEXPORT void JNICALL
Java_com_zimbra_znative_Process_setPrivileges0(JNIEnv *env, jclass clz,
                                               jbyteArray jusername,
                                               jint uid,
                                               jint gid)
{
    int length;
    char *username;

    if (jusername == NULL) {
        ZimbraThrowNPE(env, "Process.setPrivileges0 username");
        return;
    }
    
    length = (*env)->GetArrayLength(env, jusername);
    if (length <= 0) {
        ZimbraThrowIAE(env, "Process.setPrivileges0 username length <= 0");
        return;
    }
    
    username = (char *)calloc(length + 1, 1);   /* +1 for \0 */
    if (username == NULL) {
        ZimbraThrowIAE(env, "Process.setPrivileges0 username malloc failed");
        return;
    } 
    (*env)->GetByteArrayRegion(env, jusername, 0, length, (jbyte *)username);

    SetPrivileges(env, username, uid, gid);

    free(username);
}
