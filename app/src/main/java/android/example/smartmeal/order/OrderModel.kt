package android.example.smartmeal.order

import java.sql.Timestamp

class OrderModel (
    val Id: Int = 0,
    val TableId: Int = 0,
    var StatusId: Int = 0,
    var StartTime: Timestamp,
    var EndTime: Timestamp,
    var CustomerName: String = "",
    var CustomerContact: String = ""
)