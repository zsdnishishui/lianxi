package com.lianxi.pg.listen;


import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    public void pgListen() {
        PGDataSource dataSource = new PGDataSource();
        dataSource.setHost(host);
        dataSource.setPort(port);
        dataSource.setDatabaseName(database);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        System.out.println("初始化监听器");
        try {
            //获取数据库连接
            final Connection[] connection = {dataSource.getConnection()};
            //将数据库连接还原为原始的PGConnection
            final PGConnection[] pgConnection = {(PGConnection) connection[0]};
            //为连接添加监听器
            pgConnection[0].addNotificationListener(new PGNotificationListener() {
                @Override
                public void notification(int processId, String channelName, String payload) {
                    System.out.println("Received Notification: " + processId + ", " + channelName + ", " + payload);
                }

                @Override
                public void closed() {
                    PGNotificationListener.super.closed();
                }
            });
            //设置监听通道 对应pgnotify的channel
            Statement stmt = pgConnection[0].createStatement();
            System.out.println("LISTEN change_data_capture");
            stmt.executeUpdate("LISTEN change_data_capture");
            // 创建定时任务，每隔一段时间发送一个心跳查询语句
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try (Statement statement = connection[0].createStatement()) {
                        System.out.println("SELECT 1");
                        statement.execute("SELECT 1"); // 发送心跳查询语句
                    } catch (SQLException e) {
                        // 处理异常
                        System.out.println("重连");
                        try {
                            if (!connection[0].isValid(1)) {
                                connection[0].close();
                            }
                        } catch (SQLException e3) {
                            // 处理异常
                        }
                        try {
                            connection[0] = dataSource.getConnection();
                            // 重新注册监听
                            pgConnection[0] = (PGConnection) connection[0];
                            //为连接添加监听器
                            pgConnection[0].addNotificationListener(new PGNotificationListener() {
                                @Override
                                public void notification(int processId, String channelName, String payload) {
                                    System.out.println("Received Notification: " + processId + ", " + channelName + ", " + payload);
                                }

                                @Override
                                public void closed() {
                                    PGNotificationListener.super.closed();
                                }
                            });
                            //设置监听通道 对应pgnotify的channel
                            Statement stmt = pgConnection[0].createStatement();
                            System.out.println("LISTEN change_data_capture");
                            stmt.executeUpdate("LISTEN change_data_capture");
                        } catch (SQLException e2) {
                            // 处理异常
                        }
                        System.out.println("结束");
                    }
                }
            }, 0, 1000);

        } catch (SQLException e) {
            // 处理异常
        }


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
