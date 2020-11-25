package com.zhao.gmall.mq.receiver;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author: zhao
 * @Date: 2020/11/25 18:05
 */

@Component
@Configuration
public class ConfirmReceiver {
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue.confirm", autoDelete = "false"),
            exchange = @Exchange(value = "exchange.confirm", autoDelete = "true"),
            key = {"routing.confirm"}
    ))
    public void process(String ss, Message message, Channel channel){
        System.out.println("RabbitListener：-------------------"+ss);
        byte[] body = message.getBody();
        System.out.println("接收到消息："+new String(body));
        //  消息确认    ,   是否批量确认数据！
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
