package com.invisibles.smssorter.Models


data class Sender(var senderNumber: String,
                  var senderName: String) {

    var firstMessage = ""
        get() {
            return messagesList.first().messageText
        }
    var lastMessage = ""
        get(){
            return messagesList.last().messageText
        }
    var messagesList = arrayListOf<SmsMessage>()


    fun setMessages(messages: ArrayList<SmsMessage>){
        messagesList = messages
        lastMessage = messagesList.last().messageText
    }

    fun addMessage(message: SmsMessage){
        messagesList.add(message)
        lastMessage = message.messageText
    }

    fun clearMessages(){
        messagesList = arrayListOf()
    }

    fun searchInMessages(message: String): List<SmsMessage> {
        return messagesList.filter { message in it.messageText.toLowerCase() }

    }
}