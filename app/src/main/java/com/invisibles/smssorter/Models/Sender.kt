package com.invisibles.smssorter.Models


data class Sender(var senderNumber: String,
                  var senderName: String) {

    lateinit var firstMessage: SmsMessage
    lateinit var lastMessage: SmsMessage

    var messagesList = arrayListOf<SmsMessage>()
        set(value) {
            field = value
            lastMessage = value.last()
            firstMessage = value.first()
        }


    fun setMessages(messages: ArrayList<SmsMessage>){
        messagesList = messages
        lastMessage = messagesList.last()
    }

    fun addMessage(message: SmsMessage){
        messagesList.add(message)
        lastMessage = messagesList.last()
        firstMessage = messagesList.first()
    }

    fun clearMessages(){
        messagesList = arrayListOf()
    }

    fun searchInMessages(message: String): List<SmsMessage> {
        return messagesList.filter { message in it.messageText.toLowerCase() }

    }
}