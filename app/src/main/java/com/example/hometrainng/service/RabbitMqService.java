package com.example.hometrainng.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.tools.MqConsume;
import com.example.hometrainng.tools.PLog;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.tamsiree.rxkit.RxSPTool;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import lombok.SneakyThrows;

public class RabbitMqService extends Service {

    private final static String TAG = "RabbitMqService";

    private Channel channel;
    private Connection connection;
    private Consumer consumer;

    public Connection getConnection() {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(Constants.MQ_HOST);
            connectionFactory.setPort(Constants.MQ_PORT);
            connectionFactory.setVirtualHost(Constants.MQ_VIRTUAL_HOST);
            connectionFactory.setUsername(Constants.MQ_USERNAME);
            connectionFactory.setPassword(Constants.MQ_KEY);
            connectionFactory.setAutomaticRecoveryEnabled(true);
            connectionFactory.setRequestedHeartbeat(10);
            connectionFactory.setAutomaticRecoveryEnabled(true);
            return connectionFactory.newConnection();
        } catch (IOException | TimeoutException e) {
            PLog.e(TAG, "getConnection: IOException,TimeoutException");
            onDestroy();
        } finally {
            PLog.i(TAG, "getConnection: finally");
        }
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int userId = intent.getIntExtra("USER_ID", 0);
            new RabbitMqClient().execute(String.valueOf(userId));
        }
        return Service.START_STICKY;
    }

    @SuppressLint("StaticFieldLeak")
    private class RabbitMqClient extends AsyncTask<String, String, String> {

        @SneakyThrows
        @Override
        protected String doInBackground(String... strings) {

            String QUEUE_NAME = "queue" + strings[0];
            String num = strings[0];
            String EXCHANGE_NAME = Constants.MQ_EXCHANGE;

            if (connection == null) {
                connection = getConnection();
            }
            if (channel == null && connection != null) {
                channel = connection.createChannel();
                PLog.i(TAG, "create channel, bind queue");
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "message." + num);
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "goal." + num);
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "issue." + num);
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "comment." + num);
//                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "video." + num);
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "video2." + num);
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "schedule." + num);
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "therapist." + num);
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "notice");

                channel.basicConsume(QUEUE_NAME, false, new DefaultConsumer(channel) {
                    @SneakyThrows
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        if (body == null) {
                            return;
                        }
                        String token = RxSPTool.getString(getApplicationContext(), Constants.TOKEN);
                        long deliveryTag = envelope.getDeliveryTag();
                        try {
                            MqConsume mqConsume = new MqConsume(token, body);
                            mqConsume.execute();
                            channel.basicAck(deliveryTag, false);
                            PLog.i(TAG, "已消费当前消息：" + deliveryTag);
                        } catch (Exception e) {
                            channel.basicNack(deliveryTag, false, true);
                            PLog.e(TAG, "消息处理异常，直接扔掉：" + deliveryTag);
                        }

                    }
                });
//                consumer = new DefaultConsumer(channel) {
//                    @SneakyThrows
//                    @Override
//                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                        if (body == null) {
//                            return;
//                        }
//                        String token = RxSPTool.getString(getApplicationContext(), Constants.TOKEN);
//                        try{
//                            MqConsume mqConsume = new MqConsume(token, body);
//                            mqConsume.execute();
//                        }catch (Exception e){
//
//                        }
//
//                    }
//                };
//                channel.basicConsume(QUEUE_NAME, true, consumer);
            }
            return null;
        }
    }

    @SneakyThrows
    @Override
    public void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                PLog.i(TAG, "close");
                try {
                    if (channel != null) {
                        channel.abort();
                    }
                    if (connection != null) {
                        connection.abort();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        stopSelf();
    }
}
