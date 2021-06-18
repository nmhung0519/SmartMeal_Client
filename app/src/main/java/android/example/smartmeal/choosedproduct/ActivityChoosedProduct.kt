package android.example.smartmeal.choosedproduct

import android.app.Dialog
import android.example.smartmeal.Common
import android.example.smartmeal.R
import android.example.smartmeal.databinding.ActivityChoosedproductBinding
import android.example.smartmeal.orderproduct.OrderDetailModel
import android.example.smartmeal.orderproduct.OrderProductActivity
import android.example.smartmeal.products.ProductModel
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso

class ActivityChoosedProduct() : AppCompatActivity() {
    private lateinit var binding: ActivityChoosedproductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choosedproduct)
        var listProduct: List<ProductModel>
        var listOrder: List<OrderDetailModel>

        val adapter = ChoosedProductAdapter {order -> adapterOnClick(order)}
        binding.recyclerChoosedproduct.adapter = adapter

        OrderProductActivity.viewModel!!.listSelected.observe(this, Observer {
            it.let {
                adapter.submitList(it as MutableList<OrderDetailModel>)
                binding.txtTtAmount.text = Common.toMoneyFormat(OrderProductActivity.viewModel!!.getTotalAmount()) + "đ"
            }
        })
    }

    private fun adapterOnClick(order: OrderDetailModel) {
        if (order.ProductCount == 0) {
            OrderProductActivity.viewModel!!.removeProduct(order.ProductId)
            return
        }
        var dialog = Dialog(this, R.style.DialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_chooseproduct)
        var txtPOCount = dialog.findViewById<TextView>(R.id.txt_pocount)
        var btnDecrease = dialog.findViewById<Button>(R.id.btn_decrease)
        var btnIncrease = dialog.findViewById<Button>(R.id.btn_increase)
        var btnAdd = dialog.findViewById<Button>(R.id.btn_ordproduct)
        var product = OrderProductActivity.viewModel!!.getProduct(order.ProductId)
        if (product.Image != null && product.Image != "") Picasso.get().load(product.Image).into(dialog.findViewById<ImageView>(R.id.img_productimage))
        dialog.findViewById<TextView>(R.id.txt_POName).setText(product.ProductName)
        dialog.findViewById<TextView>(R.id.txt_POPrice).setText(Common.toMoneyFormat(product.ProductPrice) + "đ")
        var count = order.ProductCount
        txtPOCount.setText(count.toString())
        btnAdd.setText("Thêm • " + Common.toMoneyFormat(product.ProductPrice * count) + "đ")
        if (count == 1) {
            btnDecrease.isEnabled = false
        }
        btnDecrease.setOnClickListener{
            count--
            if (count == 1) {
                btnDecrease.isEnabled = false
            }
            txtPOCount.text = count.toString()
            btnAdd.setText("Thêm • " + Common.toMoneyFormat(product.ProductPrice * count) + "đ")
        }
        btnIncrease.setOnClickListener{
            if (count < 99) {
                count++
                btnDecrease.isEnabled = true
                txtPOCount.text = count.toString()
                btnAdd.setText("Thêm • " + Common.toMoneyFormat(product.ProductPrice * count) + "đ")
            }
        }
        btnAdd.setOnClickListener{
            OrderProductActivity.viewModel!!.updateCount(product.Id, count)
            dialog.dismiss()
        }
        var window = dialog.getWindow()
        var wlp = window?.attributes
        wlp?.gravity = Gravity.BOTTOM
        wlp?.windowAnimations = R.style.DialogAnimation
        window?.attributes = wlp
        dialog.show()
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }
}