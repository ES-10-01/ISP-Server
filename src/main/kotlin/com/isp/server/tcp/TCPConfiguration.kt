package com.isp.server.tcp

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.ip.IpHeaders
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel


@Configuration
class TCPConfiguration(private val lockManager: LockManager) {
    @Bean
    fun cf(): TcpNetServerConnectionFactory {
        return TcpNetServerConnectionFactory(9876)
    }

    @Bean
    fun inputChannel(): MessageChannel {
        return DirectChannel()
    }

    @Bean
    fun outputChannel(): MessageChannel {
        return DirectChannel()
    }

    // Inbound channel adapter
    @Bean
    fun receivingChannelAdapter(cf: AbstractServerConnectionFactory?, inputChannel: MessageChannel?): TcpReceivingChannelAdapter? {
        val tcpReceivingChannelAdapter = TcpReceivingChannelAdapter()
        tcpReceivingChannelAdapter.setConnectionFactory(cf)
        tcpReceivingChannelAdapter.setOutputChannel(inputChannel)
        return tcpReceivingChannelAdapter
    }

    // Outbound channel adapter
    @Bean
    @ServiceActivator(inputChannel = "outputChannel")
    fun tcpSendingMessageHandler(cf: AbstractServerConnectionFactory?): TcpSendingMessageHandler? {
        val tcpSendingMessageHandler = TcpSendingMessageHandler()
        tcpSendingMessageHandler.setConnectionFactory(cf)
        return tcpSendingMessageHandler
    }

    // Interacting itself
    @ServiceActivator(inputChannel = "inputChannel")
    fun receiveMessage(message: Message<String>) {
        lockManager.sortIncomingMessage(message)
    }

    fun sendMessage(message: String, TCPConnId : String) {
        outputChannel().send(MessageBuilder
            .withPayload(message)
            .setHeader(IpHeaders.CONNECTION_ID, TCPConnId)
            .build())
    }

    /* REDUNDANT
    // For the first connection
    @Bean
    fun listener(outputChannel: MessageChannel): ApplicationListener<TcpConnectionOpenEvent>? {
        return ApplicationListener { event ->
            val message: Message<String> = MessageBuilder
                .withPayload("Hi from the server!")
                .setHeader(IpHeaders.CONNECTION_ID, event.getConnectionId())
                .build()
            outputChannel.send(message)
        }
    }*/
}