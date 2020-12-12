package com.isp.server.tcp

import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.annotation.Transformer
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.channel.QueueChannel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.ip.IpHeaders
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory
import org.springframework.integration.support.MessageBuilder
import org.springframework.integration.transformer.ObjectToStringTransformer
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler

@Configuration
class TCPServer {
    @Bean
    fun server(cf: TcpNetServerConnectionFactory?): TcpReceivingChannelAdapter {
        val adapter = TcpReceivingChannelAdapter()
        adapter.setConnectionFactory(cf)
        adapter.setOutputChannel(outputChannel())   //TODO obrati vnimanie sdelano v Germanii
        return adapter
    }

    @Bean
    fun inputChannel(): MessageChannel? {
        return QueueChannel()
    }

    @Bean
    fun outputChannel(): MessageChannel {
        return DirectChannel()
    }

    @Bean
    fun cf(): TcpNetServerConnectionFactory {
        return TcpNetServerConnectionFactory(9876)
    }

    @Bean
    fun outbound(): IntegrationFlow? {
        return IntegrationFlows.from(outputChannel())
            .handle(sender())
            .get()
    }

    @Bean
    fun sender(): MessageHandler {
        val tcpSendingMessageHandler = TcpSendingMessageHandler()
        tcpSendingMessageHandler.setConnectionFactory(cf())
        return tcpSendingMessageHandler
    }

    @Bean
    fun listener(): ApplicationListener<TcpConnectionOpenEvent> {
        return ApplicationListener { event ->
            outputChannel().send(MessageBuilder.withPayload("foo")
                .setHeader(IpHeaders.CONNECTION_ID, event.getConnectionId())
                .build())
        }
    }

    @Transformer(inputChannel = "outputChannel", outputChannel = "serviceChannel")
    @Bean
    fun transformer(): ObjectToStringTransformer? {
        return ObjectToStringTransformer()
    }

    @ServiceActivator(inputChannel = "serviceChannel")
    fun service(str: String?) {
        println(str)
    }
}