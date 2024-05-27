package com.lianxi.db.pg.listener;


import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

@Configuration
@ConditionalOnProperty(value = "spring.datasource.listener.enable", havingValue = "true")
public class Pglistener {
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.host}")
    private String host;
    @Value("${spring.datasource.driver-class-name}")
    private String driver;
    @Value("${spring.datasource.port}")
    private int port;
    @Value("${spring.datasource.database}")
    private String database;

    @PostConstruct
    public void pgListen() throws SQLException {
        // 如果要分布式部署，则要加入选主功能，防止重复监听，建议采用：利用zookeeper选主
        PGDataSource dataSource = new PGDataSource();
        dataSource.setHost(host);
        dataSource.setPort(port);
        dataSource.setDatabaseName(database);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        System.out.println("初始化监听器");
        // 此处是重点，保证连接的唯一性，否则会出现重复监听的情况
        final Connection[] connection = {null};
        final PGConnection[] pgConnection = {null};
        PGNotificationListener listener = new PGNotificationListener() {
            @Override
            public void notification(int processId, String channelName, String payload) {
                System.out.println("Received Notification: " + processId + ", " + channelName + ", " + payload);
            }

            @Override
            public void closed() {
                PGNotificationListener.super.closed();
            }
        };
        connectPg(connection, pgConnection, dataSource, listener);

        // 创建定时任务，每隔一段时间发送一个心跳查询语句
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try (Statement statement = connection[0].createStatement()) {
                    statement.execute("SELECT 1"); // 发送心跳查询语句
                } catch (SQLException e) {
                    // 处理异常
                    try {
                        if (!connection[0].isValid(1)) {
                            connection[0].close();
                        }
                        connectPg(connection, pgConnection, dataSource, listener);

                    } catch (SQLException se) {
                        // 处理异常
                    }
                }
            }

        }, 0, 1000);


    }

    private void connectPg(Connection[] connection, PGConnection[] pgConnection, PGDataSource dataSource, PGNotificationListener listener) throws SQLException {
        connection[0] = dataSource.getConnection();
        //将数据库连接还原为原始的PGConnection
        pgConnection[0] = (PGConnection) connection[0];
        //为连接添加监听器
        pgConnection[0].addNotificationListener(listener);
        //设置监听通道 对应pgnotify的channel
        listenChannel(pgConnection);
    }

    private void listenChannel(PGConnection[] pgConnection) throws SQLException {
        Statement stmt = pgConnection[0].createStatement();
        System.out.println("LISTEN change_data_capture");
        stmt.executeUpdate("LISTEN change_data_capture");
    }


    /**创建触发器的函数
     * CREATE OR REPLACE FUNCTION "public"."notify_global_data_change"()
     *   RETURNS "pg_catalog"."trigger" AS $BODY$
     * BEGIN
     *   PERFORM pg_notify('change_data_capture','{"table":"'||tg_table_name||'","operation":"'||tg_op||'","id":"'||OLD.id||'","new":"'||NEW||'"}');
     *   RETURN null;
     * END
     * $BODY$
     *   LANGUAGE plpgsql VOLATILE
     *   COST 100
     */

    /**建立触发器
     * CREATE TRIGGER "trigger_articles" AFTER INSERT OR UPDATE OR DELETE ON "public"."articles"
     * FOR EACH ROW
     * EXECUTE PROCEDURE "public"."notify_global_data_change"();
     */
}
