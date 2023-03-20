/*
 * This C file provides the implementation of
 * EDU.purdue.cs.bloat.Benchmark's native run method.  It is loosely
 * based on the "timeit" example found in: 
 *                       /u/u83/pps/perfmon/examples/
 */
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/processor.h>
#include <sys/procset.h>
#include <sys/wait.h>
#include <sys/priocntl.h>
#include <sys/rtpriocntl.h>

#include <jni.h>
#include "EDU_purdue_cs_bloat_benchmark_Benchmark.h"
#include "perfmon.h"

#define MAIN_NAME "main"
#define MAIN_SIG  "([Ljava/lang/String;)V"
#define FALSE 0
#define TRUE (!FALSE)
#define PIC_SNAPSHOT_INTERVAL 5
#define check_syscall(exp) \
   do { if ((exp) == -1) die_with_errno(#exp, __FILE__, __LINE__); } while(0);

#ifndef U
#define U(a) a
#endif


/* Global locations for holding PIC values */
static unsigned long long pic0_counter;     /* Cumulative PIC0 */
static unsigned long long pic1_counter;     /* Cumulative PIC1 */
static unsigned long pic0_interval;         /* Change since last alarm */
static unsigned long pic1_interval;

/* Other interesting global variables */
static int snapshotting = TRUE;
static int want_realtime = FALSE;
static int processor = 0;                   /* Processor we use */
static int mode;                            /* Determines what we count */

/* Stuff for running method */
static jmethodID method;                    /* Pointer to main method */

/* Error reporting function */
static void
die_with_errno(const char* s, const char* file, int line)
{
   fprintf( stderr, "%s:%d:%s:%s\n", file, line, s, strerror(errno) );
   exit(1);
}


/* Functions for reading the PIC registers and keeping a running total
 * in memory.  Called before the Java program is run and when the
 * alarm goes off (i.e. when a "snapshot is taken").
 */
static void start_pic_snapshot() {
   unsigned long long pic;

   pic = get_pic();
   pic0_interval -= extract_pic0( pic );
   pic1_interval -= extract_pic1( pic );
}

/* Computes the difference in the PIC between now and the last
 * "snapshot" (i.e. the "interval").  Adds increments the overall
 * counts by these values.  Resets the interval.
 */
static void end_pic_snapshot() {
   unsigned long long pic;

   pic = get_pic();

   pic0_interval += extract_pic0( pic );
   pic1_interval += extract_pic1( pic );

   pic0_counter += (unsigned long long)pic0_interval;
   pic1_counter += (unsigned long long)pic1_interval;

   pic0_interval = pic1_interval = 0;
}

/* Function to catch interrupts and update the PIC values.  This is
 * called whenever the alarm "goes off".
 */
static void alarm_handler( int U(sig),
                           siginfo_t* U(info),
                           ucontext_t* U(ctxt) )
{
   if ( snapshotting ) {
      end_pic_snapshot();
      start_pic_snapshot();
      alarm(PIC_SNAPSHOT_INTERVAL);
   }
}

/*
 * Performs initialization necessary for running a Java program
 * multiple times and monitoring what goes on using perfmon.  For
 * instance, we obtain a pointer to the main method, set up the alarm
 * stuff, and initialize perfmon.
 *
 * Runs a Java program (a main method) with the perfmon counters
 * turned on.  We have to be careful to make sure that we correctly
 * account for counters that may overflow.  Thus, we set an alarm to
 * go off every five seconds and record the counters.  The code that
 * handles all of the alarm stuff was contributed by Kevin Corry.
 *
 * Parameters:
 *   env          JNI Environment
 *   clazz        Reference to EDU.purdue.cs.bloat.benchmark.Benchmark
 *   main         Class containing main method
 *   args         Arguments to main method
 */
JNIEXPORT void JNICALL Java_EDU_purdue_cs_bloat_benchmark_Benchmark_init(
	 JNIEnv *env, jclass clazz, jclass main)
{
  unsigned long long pcr;
  int fd;
  struct sigaction act;

  (*env)->ExceptionClear(env);

  method = (*env)->GetStaticMethodID(env, main, MAIN_NAME, MAIN_SIG);

  if ((*env)->ExceptionOccurred(env) != NULL) {
    fprintf(stderr, "Method not found: %s%s\n", MAIN_NAME, MAIN_SIG);
    (*env)->ExceptionDescribe(env);
    (*env)->ExceptionClear(env);
    return;
  }

  /* Determine which things we want to count */
  if (mode == 0) {
    /* Count load interlock induced stalls and instructions. */
    pcr = PCR_USER_TRACE | PCR_S0_STALL_LOAD | PCR_S1_INSTR_CNT;

  } else if (mode == 1) {
    /* Count data cache hit rate. */
    pcr = PCR_USER_TRACE | PCR_S0_DC_READ | PCR_S1_DC_READ_HIT;

  } else if (mode == 2) {
    /* Count icache miss induced stalls and cycles. */
    pcr = PCR_USER_TRACE | PCR_S0_STALL_IC_MISS | PCR_S1_CYCLE_CNT;

  } else if (mode == 3) {
    /* Count instructions and cycles. */
    pcr = PCR_USER_TRACE | PCR_S0_INSTR_CNT | PCR_S1_CYCLE_CNT;

  } else {
    fprintf(stderr, "invalid mode: %d, must be [012]\n", mode);
    exit(1);
  }

  /* Set up perfmon.  Bind this thread to a processor so that it can't
     get away from us.  Flush the cache for the heck of it. */
  check_syscall(processor_bind(P_PID, P_MYID, processor, 0));
  check_syscall(fd = open( "/dev/perfmon", O_RDONLY));
  check_syscall(ioctl(fd, PERFMON_FLUSH_CACHE ));
  check_syscall(ioctl(fd, PERFMON_SETPCR, &pcr));
  check_syscall(close(fd));

  /*  Set up the periodic interrupts to make measurements.  This way
      we don't have to worry about the PIC counters wrapping around. */
  act.sa_handler = 0;
  act.sa_sigaction = (void(*)(int,siginfo_t*,void*))alarm_handler;
  sigemptyset(&act.sa_mask);
  act.sa_flags = SA_SIGINFO;
  check_syscall(sigaction(SIGALRM, &act, 0));
}

/*
 * Actually runs the program and takes measurements before and after.
 * Prints the results out.
 */
JNIEXPORT void JNICALL Java_EDU_purdue_cs_bloat_benchmark_Benchmark_run(
		 JNIEnv *env, jclass clazz, jclass main, jobjectArray args) {
  pid_t pid;

  hrtime_t starttime;
  hrtime_t endtime;
  unsigned long long starttick;
  unsigned long long endtick;

  /* Initialize counters and intervals */
  snapshotting = TRUE;
  pic0_counter = pic1_counter = 0LL;
  pic0_interval = pic1_interval = 0;

  /* Print this guy.  I'm not too sure why. */
  fprintf(stderr, "0x%x\n", &gethrtime);

  /* Take an initial reading of the PICs */
  fprintf(stderr, "reset\n");
  cpu_sync();
  clr_pic();
  start_pic_snapshot();

  starttick = get_tick();   /* Get number of cycles since power-on */
  starttime = gethrtime();  /* Get the (high-resolution) time */

  alarm(PIC_SNAPSHOT_INTERVAL);         /* Set the alarm */
  check_syscall(processor_bind(P_PID, P_MYID, processor, 0));

  /* Run the Java program (benchmark). */
  (*env)->CallStaticVoidMethod(env, main, method, args);

  /* Get whatever data from the counters and report it. */
  cpu_sync();
  snapshotting = FALSE;          /* No more snapshooting */
  end_pic_snapshot();
  alarm(0);                      /* Turn off alarm */

  endtime = gethrtime();   /* Get time  */
  endtick = get_tick();    /* Get number of cycles since power-on */

  fprintf(stderr, "wall time      %llu ns\n", endtime-starttime);
  fprintf(stderr, "ticks          %llu\n",    endtick-starttick);

  if (mode == 0) {
    /* Count load stalls and instructions. */
    fprintf(stderr, "load stalls    %llu\n", pic0_counter);
    fprintf(stderr, "insts          %llu\n", pic1_counter);
  }
  else if (mode == 1) {
    /* Count data cache hit rate. */
    fprintf(stderr, "D-cache reads  %llu\n", pic0_counter);
    fprintf(stderr, "D-cache hits   %llu\n", pic1_counter);
  }
  else if (mode == 2) {
    /* Count I-cache miss stalls and cycles. */
    fprintf(stderr, "I-miss stalls  %llu\n", pic0_counter);
    fprintf(stderr, "cycles         %llu\n", pic1_counter);
  }
  else if (mode == 3) {
    /* Count instructions and cycles. */
    fprintf(stderr, "insts          %llu\n", pic0_counter);
    fprintf(stderr, "cycles         %llu\n", pic1_counter);
  }


  fflush(stderr);
}

/* Set the mode.  The mode tells us which things to count.
 */
JNIEXPORT void JNICALL Java_EDU_purdue_cs_bloat_benchmark_Benchmark_setMode(
    JNIEnv *env, jclass clazz, jint m)
{
    mode = m;
}
