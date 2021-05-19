package android.example.smartmeal.order

import android.example.smartmeal.Common
import java.sql.Date
import java.sql.Timestamp

class OrderModel (
    var Id: Int = 0,
    var TableId: Int = 0,
    var StatusId: Int = 0,
    var StartTime: Timestamp = Timestamp(2000, 1, 1, 0, 0, 0, 0),
    var EndTime: Timestamp = Timestamp(2000, 1, 1, 0, 0, 0, 0),
    var CustomerName: String = "",
    var CustomerContact: String = ""
)