package com.invisibles.smssorter.Models

data class SmsMessage(var messageId: Int,
                      var messageText: String,
                      var messageTime: Long,
                      var messageType: Int,
                      var address: String
                      ) {

}