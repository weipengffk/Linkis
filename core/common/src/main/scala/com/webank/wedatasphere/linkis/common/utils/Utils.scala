/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.linkis.common.utils

import java.io.{BufferedReader, InputStreamReader}
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{ScheduledThreadPoolExecutor, _}

import com.webank.wedatasphere.linkis.common.exception.{DwcCommonErrorException, ErrorException, FatalException, WarnException}
import org.apache.commons.io.IOUtils
import org.slf4j.Logger

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}
import scala.util.control.ControlThrowable
/**
  * Created by enjoyyin on 2018/1/10.
  */
object Utils extends Logging {

  def tryQuietly[T](tryOp: => T): T = try tryOp catch {
    case c: ControlThrowable => throw c
    case fatal: FatalException =>
      error("Fatal Error, system exit...", fatal)
      System.exit(fatal.getErrCode)
      null.asInstanceOf[T]
    case _ => null.asInstanceOf[T]
  }

  def tryCatch[T](tryOp: => T)(catchOp: Throwable => T): T = {
    try tryOp catch {
      case t: ControlThrowable => throw t
      case fatal: FatalException =>
        error("Fatal Error, system exit...", fatal)
        System.exit(fatal.getErrCode)
        null.asInstanceOf[T]
      case t: Throwable => catchOp(t)
    }
  }

  def tryThrow[T](tryOp: => T)(exception: Throwable => Throwable): T = tryCatch(tryOp){
    t: Throwable => throw exception(t)
  }

  def tryFinally[T](tryOp: => T)(finallyOp: => Unit): T = try tryOp finally finallyOp

  def tryQuietly[T](tryOp: => T, catchOp: Throwable => Unit): T = tryCatch(tryOp){
    case fatal: FatalException =>
      error("Fatal Error, system exit...", fatal)
      System.exit(fatal.getErrCode)
      null.asInstanceOf[T]
    case t: Throwable =>
      catchOp(t)
      null.asInstanceOf[T]
  }

  def tryAndWarn[T](tryOp: => T)(implicit log: Logger): T = tryCatch(tryOp){
    case error: ErrorException =>
      val errorMsg = s"error code（错误码）: ${error.getErrCode}, Error message（错误信息）: ${error.getDesc}."
      log.error(errorMsg, error)
      null.asInstanceOf[T]
    case warn: WarnException =>
      val warnMsg = s"Warning code（警告码）: ${warn.getErrCode}, Warning message（警告信息）: ${warn.getDesc}."
      log.warn(warnMsg, warn)
      null.asInstanceOf[T]
    case t: Throwable =>
      log.warn("", t)
      null.asInstanceOf[T]
  }

  def tryAndWarnMsg[T](tryOp: => T)(message: String)(implicit log: Logger): T = tryCatch(tryOp){
    case error: ErrorException =>
      log.error(s"error code（错误码）: ${error.getErrCode}, Error message（错误信息）: ${error.getDesc}.")
      log.error(message, error)
      null.asInstanceOf[T]
    case warn: WarnException =>
      log.warn(s"Warning code（警告码）: ${warn.getErrCode}, Warning message（警告信息）: ${warn.getDesc}.")
      log.warn(message, warn)
      null.asInstanceOf[T]
    case t: Throwable =>
      log.warn(message, t)
      null.asInstanceOf[T]
  }

  def tryAndError[T](tryOp: => T)(implicit log: Logger): T = tryCatch(tryOp){
    case error: ErrorException =>
      val errorMsg = s"error code（错误码）: ${error.getErrCode}, Error message（错误信息）: ${error.getDesc}."
      log.error(errorMsg, error)
      null.asInstanceOf[T]
    case warn: WarnException =>
      val warnMsg = s"Warning code（警告码）: ${warn.getErrCode}, Warning message（警告信息）: ${warn.getDesc}."
      log.warn(warnMsg, warn)
      null.asInstanceOf[T]
    case t: Throwable =>
      log.error("", t)
      null.asInstanceOf[T]
  }

  def tryAndErrorMsg[T](tryOp: => T)(message: String)(implicit log: Logger): T = tryCatch(tryOp){
    case error: ErrorException =>
      log.error(s"error code（错误码）: ${error.getErrCode}, Error message（错误信息）: ${error.getDesc}.")
      log.error(message, error)
      null.asInstanceOf[T]
    case warn: WarnException =>
      log.warn(s"Warning code（警告码）: ${warn.getErrCode}, Warning message（警告信息）: ${warn.getDesc}.")
      log.warn(message, warn)
      null.asInstanceOf[T]
    case t: Throwable =>
      log.error(message, t)
      null.asInstanceOf[T]
  }
  
  def sleepQuietly(mills: Long): Unit = tryQuietly(Thread.sleep(mills))

  def threadFactory(threadName: String, isDaemon: Boolean = true): ThreadFactory = {
    new ThreadFactory {
      val num = new AtomicInteger(0)
      override def newThread(r: Runnable): Thread = {
        val t = new Thread(r)
        t.setDaemon(isDaemon)
        t.setName(threadName + num.incrementAndGet())
        t
      }
    }
  }

  def newCachedThreadPool(threadNum: Int, threadName: String, isDaemon: Boolean = true) = {
    val threadPool = new ThreadPoolExecutor(threadNum, threadNum, 120L, TimeUnit.SECONDS,
      new LinkedBlockingQueue[Runnable](10 * threadNum),
      threadFactory(threadName, isDaemon))
    threadPool.allowCoreThreadTimeOut(true)
    threadPool
  }

  def newCachedExecutionContext(threadNum: Int, threadName: String, isDaemon: Boolean = true) =
    ExecutionContext.fromExecutorService(newCachedThreadPool(threadNum, threadName, isDaemon))

  def newFixedThreadPool(threadNum: Int, threadName: String, isDaemon: Boolean = true): ExecutorService = {
    Executors.newFixedThreadPool(threadNum, threadFactory(threadName, isDaemon))
  }

  def newFixedExecutionContext(threadNum: Int, threadName: String, isDaemon: Boolean = true): ExecutionContextExecutorService = {
    ExecutionContext.fromExecutorService(newFixedThreadPool(threadNum, threadName, isDaemon))
  }

  /**
    * 定义默认队列：
    */
  val defaultScheduler = {
    val scheduler = new ScheduledThreadPoolExecutor(20, threadFactory("BDP-Default-Scheduler-Thread-", true))
    scheduler.setMaximumPoolSize(20)
    //空闲线程最大存活时间：5分钟
    scheduler.setKeepAliveTime(5, TimeUnit.MINUTES)
    scheduler
  }

  def getLocalHostname = InetAddress.getLocalHost.getHostAddress

  def getComputerName = Utils.tryCatch(InetAddress.getLocalHost.getCanonicalHostName)(t => sys.env("COMPUTERNAME"))

  /**
    * Checks if event has occurred during some time period. This performs an exponential backoff
    * to limit the poll calls.
    *
    * @param checkForEvent
    * @param atMost
    * @throws java.util.concurrent.TimeoutException
    * @throws java.lang.InterruptedException
    * @return
    */
  @throws(classOf[TimeoutException])
  @throws(classOf[InterruptedException])
  final def waitUntil(checkForEvent: () => Boolean, atMost: Duration, radix: Int, maxPeriod: Long): Unit = {
    val endTime = try System.currentTimeMillis() + atMost.toMillis catch { case _: IllegalArgumentException => 0l }

    @tailrec
    def aux(count: Int): Unit = {
      if (!checkForEvent()) {
        val now = System.currentTimeMillis()

        if (endTime == 0 || now < endTime) {
          val sleepTime = Math.max(Math.min(radix * count, maxPeriod), 100)
          Thread.sleep(sleepTime)
          aux(count + 1)
        } else {
          throw new TimeoutException
        }
      }
    }

    aux(1)
  }

  final def waitUntil(checkForEvent: () => Boolean, atMost: Duration): Unit = waitUntil(checkForEvent, atMost, 100, 2000)

  /**
    * do not exec complex shell command with lots of output, may cause io blocking
    * @param commandLine shell command
    * @return
    */
  def exec(commandLine: Array[String]): String = exec(commandLine, -1)

  /**
    * do not exec complex shell command with lots of output, may cause io blocking
    * @param commandLine shell command
    * @return
    */
  def exec(commandLine: List[String]): String = exec(commandLine, -1)

  /**
    * do not exec complex shell command with lots of output, may cause io blocking
    * @param commandLine shell command
    * @param maxWaitTime max wait time
    * @return
    */
  def exec(commandLine: Array[String], maxWaitTime: Long): String = exec(commandLine.toList, maxWaitTime)

  /**
    * do not exec complex shell command with lots of output, may cause io blocking
    * @param commandLine shell command
    * @param maxWaitTime max wait time
    * @return
    */
  def exec(commandLine: List[String], maxWaitTime: Long): String = {
    val pb = new ProcessBuilder(commandLine)
    pb.redirectErrorStream(true)
    pb.redirectInput(ProcessBuilder.Redirect.PIPE)
    val process = pb.start
    val log = new BufferedReader(new InputStreamReader(process.getInputStream))
    val exitCode = if(maxWaitTime > 0) {
      val completed = process.waitFor(maxWaitTime, TimeUnit.MILLISECONDS)
      if(!completed) {
        IOUtils.closeQuietly(log)
        process.destroy()
        throw new TimeoutException(s"exec timeout with ${ByteTimeUtils.msDurationToString(maxWaitTime)}!")
      }
      process.exitValue
    } else
      tryThrow(process.waitFor)(t => {process.destroy();IOUtils.closeQuietly(log);t})
    val lines = log.lines().toArray
    IOUtils.closeQuietly(log)
    if (exitCode != 0) {
      throw new DwcCommonErrorException(0, s"exec failed with exit code: $exitCode, ${lines.mkString(". ")}")
    }
   lines.mkString("\n")
  }

  def addShutdownHook(hook: => Unit): Unit = ShutdownUtils.addShutdownHook(hook)

  def getClassInstance[T](className: String): T ={
    Utils.tryThrow(
      (Thread.currentThread.getContextClassLoader.loadClass(className).asInstanceOf[Class[T]].newInstance())) (t =>{
      error(s"Failed to instance: $className ", t)
      throw t
    })
  }

  def msDurationToString(ms: Long): String = {
    val second = 1000
    val minute = 60 * second
    val hour = 60 * minute
    ms match {
      case t if t < second =>
        "%d ms".format(t)
      case t if t < minute =>
        "%.1f 秒".format(t.toFloat / second)
      case t if t < hour =>
        "%.1f 分钟".format(t.toFloat / minute)
      case t =>
        "%.2f 小时".format(t.toFloat / hour)
    }
  }


}
