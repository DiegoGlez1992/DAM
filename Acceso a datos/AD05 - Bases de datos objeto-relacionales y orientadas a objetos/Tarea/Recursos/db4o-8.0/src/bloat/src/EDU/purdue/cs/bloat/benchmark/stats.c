#include <jni.h>
#include <stdio.h>
#include <fcntl.h>
#include <sys/time.h>
#include <stdio.h>
#include "EDU_purdue_cs_bloat_benchmark_Stats.h"

extern int instruction_count[256][256];
extern int redundant_count[256];

#define MAIN_NAME "main"
#define MAIN_SIG  "([Ljava/lang/String;)V"

JNIEXPORT void JNICALL Java_EDU_purdue_cs_bloat_benchmark_Stats_run(
    JNIEnv *env, jclass clazz, jclass main, jobjectArray args) {
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

    memset(instruction_count, 0, sizeof(instruction_count));
    memset(redundant_count, 0, sizeof(redundant_count));

    (*env)->CallStaticVoidMethod(env, main, method, args);
}
