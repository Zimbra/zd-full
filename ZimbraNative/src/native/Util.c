/* -*- Mode: c; c-basic-offset: 4 -*- */
/*
 * 
 */

#include <jni.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/times.h>

#include "Util.h"
#include "zjniutil.h"

JNIEXPORT jlong JNICALL
Java_com_zimbra_znative_Util_getTicksPerSecond0(JNIEnv *env, jclass clz)
{
  long tps = sysconf(_SC_CLK_TCK);
  if (tps == -1) {
    char msg[256];
    snprintf(msg, sizeof(msg), "times(): %s", strerror(errno));
    ZimbraThrowOFE(env, msg);
    return -1;
  }
  return tps;
} 
