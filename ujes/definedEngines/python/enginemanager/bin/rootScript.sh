#!/usr/bin/expect -f

# 设置变量 user,command
set user [lindex $argv 0]
set command [lindex $argv 1]
#设置超时时间
set timeout -1

# 进入expect环境后才可执行的expect命令
spawn sudo su -
# expect的一个内部命令,判断上次输入结果是否包含
expect "~]# "
# 切换hadoop用户
send "su - $user\r"
expect "~]* "
# 执行命令
send "$command \r"
expect "~]* "
# 退出
send "exit\r"
expect "~]# "
# 退出
send "exit\r"
expect "~]$ "
