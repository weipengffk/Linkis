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

import com.webank.wedatasphere.linkis.protocol.{CacheableProtocol, RetryableProtocol}

/**
  * Created by enjoyyin on 2018/10/16.
  * 配置协议特性
  */
trait ConfigProtocol

/**
  * 请求查询全局配置
  * @param userName  用户名
  */
case class RequestQueryGlobalConfig(userName:String) extends CacheableProtocol with RetryableProtocol with ConfigProtocol

/**
  * 请求查询APP配置信息
  * @param userName  用户
  * @param creator   创建者
  * @param appName   app名称
  */
case class RequestQueryAppConfig(userName:String,creator:String,appName:String) extends CacheableProtocol with RetryableProtocol with ConfigProtocol

/**
  * 全局查询APP配置信息
  * @param userName  用户名
  * @param creator  创建者
  * @param appName  app名称
  * @param isMerge  是否合并
  */
case class RequestQueryAppConfigWithGlobal(userName:String,creator:String,appName:String,isMerge:Boolean) extends CacheableProtocol with RetryableProtocol with ConfigProtocol
