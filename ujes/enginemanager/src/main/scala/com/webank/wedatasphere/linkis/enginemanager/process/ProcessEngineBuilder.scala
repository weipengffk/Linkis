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

package com.webank.wedatasphere.linkis.enginemanager.process

import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.enginemanager.EngineResource
import com.webank.wedatasphere.linkis.protocol.engine.RequestEngine

/**
  * Created by johnnwang on 2018/9/6.
  */
trait ProcessEngineBuilder extends Logging {
  /**
    * 设置端口
    * @param port
    */
  def setPort(port: Int): Unit

  /**
    * 引擎构建
    * @param engineRequest  引擎请求资源
    * @param request  引擎对象
    */
  def build(engineRequest: EngineResource, request: RequestEngine): Unit

  /**
    * 获取引擎资源
    * @return
    */
  def getEngineResource: EngineResource

  /**
    * 获取引擎对象
    * @return
    */
  def getRequestEngine: RequestEngine

  /**
    * 启动进程
    * @param args
    * @return
    */
  def start(args: Array[String]): Process
}