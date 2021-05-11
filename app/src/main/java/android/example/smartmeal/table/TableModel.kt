package android.example.smartmeal.table

import android.example.smartmeal.Common
import android.example.smartmeal.order.OrderModel

data class TableModel(
    val Id: Int = 0,
    var TableName: String = "",
    var Status: Int = 0,
    var IsActive: Int = 0,
    var Order: OrderModel? = null
) {

    val color: String
        get() = when (Status) {
            0 -> Common.COLOR_EMPTY_TABLE
            1 -> Common.COLOR_FILLED_TABLE
            2 -> Common.COLOR_BOOKED_TABLE
            else -> Common.COLOR_UNKNOWN_TABLE
        }
    fun update(table: TableModel) {
        TableName = table.TableName
        Status = table.Status
        IsActive = table.IsActive
        Order = table.Order
    }
}