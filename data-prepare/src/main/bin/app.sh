#!/bin/bash

# jar包名称
JAR_FILE="../data-prepare.jar"
# pid 名称
PID_FILE="data-prepare.pid"

start() {
    if [ -f "$PID_FILE" ]; then
        echo "应用程序已在运行,PID: $(cat $PID_FILE) .........."
        stop
    fi

    rm log.log
    # 后台启动jar包，并将启动日志输出到log.log文件中
    nohup java -jar -Dloader.path=.,3rd-li $JAR_FILE >> log.log 2>&1 &
    echo $! > $PID_FILE
    echo "应用程序已成功启动,PID: $(cat $PID_FILE) .........."
}

stop() {
    if [ -f "$PID_FILE" ]; then
        kill -9 $(cat $PID_FILE)
        rm $PID_FILE
        #rm log.log
        echo "应用程序已成功停止.........."
    else
        echo "应用程序未运行.........."
    fi
}

restart() {
    echo "正在重启.........."
    stop
    start
    echo "重启成功.........."
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    *)
        echo "使用: $0 {start|stop|restart} 命令"
        exit 1
        ;;
esac

