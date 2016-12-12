# LiteBox
Distributed application that provides shared folder service

Intended for LAN.

Installation guide:
Copy executable to path where you want the shared folder to be created.
Create 'iplist.txt' in the same folder as the executable.
Add ALL peers' IPs to 'iplist.txt'.
Restart app to detect new IPs.

## Requirements

<img src="http://3.bp.blogspot.com/-maL4T5D3trw/U4c66rzQgcI/AAAAAAAAJXc/5NumFMFyGc8/s1600/java8_logo.png" height=50/> 
<img src="http://www.guernik.com/blog/wp-content/uploads/2012/05/mac-windows-linux.jpg" height=50/>
## How to start the app as service
```sublime-commands
#!/bin/sh
SERVICE_NAME=liteP2PBox
PATH_TO_JAR=$JAR_PATH/p2p.jar
PID_PATH_NAME=/tmp/liteP2PBox-pid
case $1 in
    start)
        echo "Starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
            nohup $JAVA_8_PATH/java -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &
                        echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill $PID;
            echo "$SERVICE_NAME stopped ...";
            rm $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."
            nohup $JAVA_8_PATH/java -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &
                        echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
esac
```
Edit the service file:
- Replace $JAVA_8_PATH with corresponding pat for Java 8.
- Replace $JAR_PATH with corresponding path for the runnable jar.

Execute command in terminal to move the script at the services folder:
`mv ./service /etc/init.d/liteP2Pbox` 

Start app as service:
`sudo service liteP2Pbox start`

Stop service:
`sudo service liteP2Pbox stop`
