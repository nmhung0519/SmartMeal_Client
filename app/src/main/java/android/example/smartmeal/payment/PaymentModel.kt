package android.example.smartmeal.payment

class DataPaymentModel (
    var TableName: String = "",
    var OrderId: Int = 0,
    var CustomerName: String = "",
    var CustomerPhone: String = "",
    var Data: List<PaymentItem>? = null
) {

}

class PaymentItem(
    var ProductName: String = "",
    var ProductCount: Int = 0,
    var ProductPrice: Int = 0
)