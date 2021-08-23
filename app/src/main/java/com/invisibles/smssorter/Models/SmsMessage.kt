package com.invisibles.smssorter.Models

data class SmsMessage(var id: Int,
                      var text: String,
                      var time: Long,
                      var type: Int,
                      var address: String
                      ) {

}