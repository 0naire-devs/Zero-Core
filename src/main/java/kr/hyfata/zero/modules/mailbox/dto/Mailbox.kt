package kr.hyfata.zero.modules.mailbox.dto

import java.sql.Timestamp

class Mailbox {
    var mailId: Int = 0
    var uuid: String? = null
    var item: ByteArray? = null
    var expiryTime: Timestamp? = null
    var sentTime: Timestamp? = null
}
