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

package com.webank.wedatasphere.linkis.protocol.engine

/**
  * Created by enjoyyin on 2018/9/27.
  */
object EngineState extends Enumeration {
  type EngineState = Value
  /**
    * starting 启动
    * ldle  空闲
    * busy  繁忙
    */
  val Starting, Idle, Busy, ShuttingDown, Error, Dead, Success = Value

  /**
    * 是否完成
    * @param executorState
    * @return
    */
  def isCompleted(executorState: EngineState): Boolean = executorState match {
    case Error | Dead | Success => true
    case _ => false
  }

  /**
    * 是否可用
    * @param executorState
    * @return
    */
  def isAvailable(executorState: EngineState): Boolean = executorState match {
    case Idle | Busy => true
    case _ => false
  }
}