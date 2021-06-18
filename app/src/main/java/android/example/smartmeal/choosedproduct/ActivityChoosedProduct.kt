package android.example.smartmeal.choosedproduct

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.example.smartmeal.Common
import android.example.smartmeal.MainActivity
import android.example.smartmeal.R
import android.example.smartmeal.ResponseModel
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException

class ActivityChoosedProduct() : AppCompatActivity() {
    private lateinit var binding: ActivityChoosedproductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choosedproduct)
        var listProduct: List<ProductModel>
        var listOrder: List<OrderDetailModel>
        var _flag = false

        val adapter = ChoosedProductAdapter {order -> adapterOnClick(order)}
        binding.recyclerChoosedproduct.adapter = adapter

        OrderProductActivity.viewModel!!.listSelected.observe(this, Observer {
            it.let {
                adapter.submitList(it as MutableList<OrderDetailModel>)
                binding.txtTtAmount.text = Common.toMoneyFormat(OrderProductActivity.viewModel!!.getTotalAmount()) + "đ"
                if (OrderProductActivity.viewModel!!.listSelected!!.value == null || OrderProductActivity.viewModel!!.listSelected!!.value!!.count() == 0) finish()
            }
        })
        binding.btnChoosedproductBack.setOnClickListener{
            var intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

        binding.btnCfOdproduct.setOnClickListener{
            binding.btnCfOdproduct.isEnabled = false
            _flag = true
            MainActivity.hubConnection.send("GetToken")
        }

        MainActivity.token.observe(this, Observer {
            if (_flag) {
                _flag = false
                try {
                    var url = Common.DOMAIN + "/OrderDetail/Insert";
                    var content = "["
                    var _listSelected = OrderProductActivity.viewModel!!.listSelected.value!!
                    for (i in 0 until _listSelected.count()) {
                        var _order = _listSelected[i]
                        if (i > 0) content + ","
                        content += "{"
                        content += "\"OrderId\": " + OrderProductActivity.viewModel.orderId + ","
                        content += "\"ProductName\": \"" + _order.ProductName + "\","
                        content += "\"ProductCount\": " + _order.ProductCount + ","
                        content += "\"ProductPrice\": " + _order.ProductPrice + ""
                        content += "}"
                    }
                    content += "]"
                    val client = OkHttpClient()
                    val body = RequestBody.create(
                        MediaType.parse("application/json"), content)
                    val request = Request.Builder()
                        .url(url)
                        .addHeader("Authorization", it)
                        .post(body)
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onResponse(call: Call, response: Response) {
                            val body = response?.body()?.string()
                            val gson = Gson()
                            try {
                                var responseModel = gson.fromJson(body, ResponseModel::class.java)
                                if (responseModel.status == true) {
                                    var intent = Intent()
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                                else {
                                }
                            }
                            catch (ex: Exception) {
                            }
                            binding.btnCfOdproduct.isEnabled = false
                        }

                        override fun onFailure(call: Call, e: IOException) {
                            binding.btnCfOdproduct.isEnabled = false
                        }
                    })
                }
                catch (ex: Exception) {
                    binding.btnCfOdproduct.isEnabled = false
                }
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