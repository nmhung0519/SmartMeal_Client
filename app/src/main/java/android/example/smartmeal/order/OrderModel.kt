package android.example.smartmeal.order

import android.example.smartmeal.Common
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime

class OrderModel (
    var Id: Int = 0,
    var TableId: Int = 0,
    var StatusId: Int = 0,
    var StartTime: String? = "",
    var EndTime: String? = "",
    var CustomerName: String = "",
    var CustomerContact: String = ""
)