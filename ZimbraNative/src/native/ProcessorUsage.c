/* -*- Mode: c; c-basic-offset: 4 -*- */
/*
 * 
 */

#include <jni.h>
#include <errno.h>
#include <string.h>
#include <sys/times.h>

#include "ProcessorUsage.h"
#include "zjniutil.h"

JNIEXPORT void JNICALL
Java_com_zimbra_znative_ProcessorUsage_getProcessorUsage0(JNIEnv *env, jclass clz, jlongArray jdata)
{
  struct tms tms;
  clock_t wall;
  jlong data[com_zimbra_znative_ProcessorUsage_OFFSET_MAX];

  if ((wall = times(&tms)) == ((clock_t)-1)) {
    char msg[256];
    snprintf(msg, sizeof(msg), "times(): %s", strerror(errno));
    ZimbraThrowOFE(env, msg);
    return;
  }
  
  data[com_zimbra_znative_ProcessorUsage_OFFSET_UTICKS] = tms.tms_utime;
  data[com_zimbra_znative_ProcessorUsage_OFFSET_STICKS] = tms.tms_stime;
  data[com_zimbra_znative_ProcessorUsage_OFFSET_CUTICKS] = tms.tms_cutime;
  data[com_zimbra_znative_ProcessorUsage_OFFSET_CSTICKS] = tms.tms_cstime;
  data[com_zimbra_znative_ProcessorUsage_OFFSET_WTICKS] = wall;

  (*env)->SetLongArrayRegion(env, jdata, 0, com_zimbra_znative_ProcessorUsage_OFFSET_MAX, data);
}
