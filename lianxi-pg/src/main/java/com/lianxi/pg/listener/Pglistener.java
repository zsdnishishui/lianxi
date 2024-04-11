package com.lianxi.pg.listener;


import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
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
    public void pgListen() throws SQLException {
        PGDataSource dataSource = new PGDataSource();
        dataSource.setHost(host);
        dataSource.setPort(port);
        dataSource.setDatabaseName(database);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        System.out.println("初始化监听器");
        //获取数据库连接
        Connection connection = dataSource.getConnection();
        //将数据库连接还原为原始的PGConnection
        PGConnection pgConnection = connection.unwrap(PGConnection.class);
        //为连接添加监听器
        pgConnection.addNotificationListener(new PGNotificationListener() {
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
        Statement stmt = pgConnection.createStatement();
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
