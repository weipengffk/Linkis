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

package com.webank.wedatasphere.linkis.protocol.config

import java.util

/**
  * Created by enjoyyin on 2018/10/17.
  * 返回查询特性
  */
class ResponseQueryConfig extends ConfigProtocol {
  // 定义键值对
  private var keyAndValue: util.Map[String, String] = _
  //返回键值对
  def getKeyAndValue :util.Map[String, String] = keyAndValue
  //设置键值对
  def setKeyAndValue(keyAndValue :util.Map[String, String]) :Unit = this.keyAndValue =keyAndValue
}
