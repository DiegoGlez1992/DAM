#include <jni.h>
#include <stdio.h>
#include <fcntl.h>
#include <sys/time.h>
#include "EDU_purdue_cs_bloat_benchmark_Shade.h"

#define MAIN_NAME "main"
#define MAIN_SIG  "([Ljava/lang/String;)V"

JNIEXPORT void JNICALL Java_EDU_purdue_cs_bloat_benchmark_Shade_run(
    JNIEnv *env, jclass clazz, jclass main, jobjectArray args, jboolean quit)
{
  jmethodID method;

  (*env)->ExceptionClear(env);

  method = (*env)->GetStaticMethodID(env, main, MAIN_NAME, MAIN_SIG);

  if ((*env)->ExceptionOccurred(env) != NULL) {
    fprintf(stderr, "Method not found: %s%s\n", MAIN_NAME, MAIN_SIG);
    (*env)->ExceptionDescribe(env);
    (*env)->ExceptionClear(env);
    return;
  }

  system("sh ./run_pre");

  if (quit) {
    fprintf(stderr, "0x%x\n", &gethrtime);
    fflush(stderr);
    return;
  }

  gethrtime();

  (*env)->CallStaticVoidMethod(env, main, method, args);

  gethrtime();
}
