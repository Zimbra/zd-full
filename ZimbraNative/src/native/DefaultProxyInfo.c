/* -*- Mode: c; c-basic-offset: 4 -*- */
/*
 * 
 */

#include <jni.h>

#include "ProxyInfo.h"

#define UNSUPPORTED_OPERATION "java/lang/UnsupportedOperationException"

static void throwException(JNIEnv *env, const char *clsname, const char *msg) {
    jclass cls = (*env)->FindClass(env, clsname);
    if (cls != NULL) {
        (*env)->ThrowNew(env, cls, msg);
    }
}

JNIEXPORT jobjectArray JNICALL
Java_com_zimbra_znative_ProxyInfo_getProxyInfo(JNIEnv *env, jclass cls, jstring jurl) {
    throwException(env, UNSUPPORTED_OPERATION, "ProxyInfo not supported on this platform");
    return NULL;
}


JNIEXPORT jboolean JNICALL
Java_com_zimbra_znative_ProxyInfo_isSupported(JNIEnv *env, jclass cls) {
    return JNI_FALSE;
}
