var stompClient = null;
var wsReconnect = null;
var userId = null;
var reConnectCount = 0;
var url = "http://127.0.0.1:8900/websocket"; // 改成自己的服务端地址

/**
 * 连接
 */
function connect() {
    userId = GetUrlParam("userId");
    var socket = new SockJS(url, null, {timeout: 15000});
    stompClient = Stomp.over(socket); // 覆盖sockjs使用stomp客户端
    stompClient.connect({}, function (frame) {

            console.log('frame: ' + frame)

            // 连接成功后广播通知
            sendNoticeMsg(userId, "in");

            /**
             * 订阅列表，订阅路径和服务端发消息路径一致就能收到消息。
             * -- /topic: 服务端配置的广播订阅路径
             * -- /queue/: 服务端配置的点对点订阅路径
             */
            stompClient.subscribe("/topic", function (response) {
                showMsg(response.body);
            });

            stompClient.subscribe("/queue/" + userId + "/topic", function (response) {
                showMsg(response.body);
            });

            // 异常时进行重连
        }, function (error) {
            console.log('connect error: ' + error)
            if (reConnectCount > 10) {
                console.log("温馨提示：您的连接已断开，请退出后重新进入。")
                reConnectCount = 0;
            } else {
                wsReconnect && clearTimeout(wsReconnect);
                wsReconnect = setTimeout(function () {
                    console.log("开始重连...");
                    connect();
                    console.log("重连完毕...");
                    reConnectCount++;
                }, 1000);
            }
        }
    )
}

/**
 * 断开
 */
function disconnect() {
    if (stompClient != null) {
        // 断开连接时进行广播通知
        sendNoticeMsg(userId, "out");
        // 断开连接
        stompClient.disconnect(function () {
            // 有效断开的回调
            console.log(userId + "断开连接....")
        });
    }
}

/**
 * 获取URL带的参数
 */
function GetUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substring(1).match(reg); //获取url中"?"符后的字符串并正则匹配
    var context = "";
    if (r != null)
        context = r[2];
    reg = null;
    r = null;
    return context == null || context == "" || context == "undefined" ? "" : context;
}

/**
 * 格式化时间
 */
Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份   
        "d+": this.getDate(), //日   
        "H+": this.getHours(), //小时   
        "m+": this.getMinutes(), //分   
        "s+": this.getSeconds(), //秒   
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度   
        "S": this.getMilliseconds() //毫秒   
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

// 消息窗口滚动到底部
function scrollBotton() {
    var div = document.getElementById("content");
    div.scrollTop = div.scrollHeight;
}

/**
 * 聊天消息渲染到页面中
 */
function showMsg(obj) {
    obj = JSON.parse(obj);
    var userId = obj.userId;
    var sendTime = obj.sendTime;
    var info = obj.info;
    var type = obj.type;

    if (1 === type) {
        // 聊天消息
        console.log("聊天消息...")
        var msgHtml = "<div class=\"msg\" id=\"msg\">" +
            "  <div class=\"first-line\">" +
            "	   <div class=\"userName\" id=\"userName\">" + userId + "</div>" +
            "	   <div class=\"sendTime\" id=\"sendTime\">" + sendTime + "</div>" +
            "  </div>" +
            "  <div class=\"second-line\">" +
            "    <div class=\"sendMsg\" id=\"sendMsg\">" + info + "</div>" +
            "  </div>" +
            "</div>";

        // 渲染到页面
        $("#content").html($("#content").html() + "\n" + msgHtml);

    } else if (2 === type) {
        // 系统消息
        console.log("系统消息...")
        var msgHtml = "<div class=\"notice\">" +
            "<div class=\"notice-info\">" + info + "</div>" +
            "</div>";

        // 渲染到页面
        $("#content").html($("#content").html() + "\n" + msgHtml);
    }

    // 消息窗口滚动到底部
    scrollBotton();
}

/**
 * 发送系统通知消息
 * @param userId 用户id
 */
function sendNoticeMsg(userId, action) {
    var obj = {
        "userId": userId,
        "sendTime": new Date().Format("HH:mm:ss"),
        "info": "in" === action ? userId + "进入房间" : userId + "离开房间",
        "type": 2
    }
    sendAll(obj);
}

/**
 * 发送群聊消息
 * -- 这里我们传递消息体对象
 *            {
 *               "userId": userId, // 发送者
 *               "sendTime": sendTime, // 发送时间
 *               "info": info, // 发送内容
 *               "type": 1  // 消息类型，1-聊天消息，2-系统消息
 *           }
 */
function sendAll(obj) {
    // 路径：服务端WebsocketConfig.java中设置的 ApplicationDestinationPrefixes + enableSimpleBroker 路径，少一个或多一个斜杠都不行。
    stompClient.send("/app/send", {}, JSON.stringify(obj));
}
