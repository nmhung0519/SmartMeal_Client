package android.example.smartmeal.products

import android.widget.ImageView

data class ProductModel(
    val Id: Int = 0,
    val ProductName: String = "",
    val Image: String = "",
    val Status: Int = 0,
    val IsActive: Int = 0
)