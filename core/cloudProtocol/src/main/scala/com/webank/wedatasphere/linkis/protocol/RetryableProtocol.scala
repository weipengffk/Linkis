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

package com.webank.wedatasphere.linkis.protocol

/**
  * Created by enjoyyin on 2019/1/7.
  * 重试协议特性
  */
trait RetryableProtocol extends Protocol {
  /**
    * 重试次数
    */
  val retryNum = 2
  //时间
  val period = 1000l
  //最大时间
  val maxPeriod = 3000l
  //重试异常
  val retryExceptions = Array.empty[Class[_  <: Throwable]]
}