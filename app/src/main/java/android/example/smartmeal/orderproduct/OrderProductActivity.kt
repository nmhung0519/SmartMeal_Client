package android.example.smartmeal.orderproduct

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.example.smartmeal.Common
import android.example.smartmeal.MainActivity
import android.example.smartmeal.R
import android.example.smartmeal.ResponseModel
import android.example.smartmeal.databinding.ActivityOrderproductBinding
import android.example.smartmeal.databinding.ActivityTableorderBinding
import android.example.smartmeal.order.OrderModel
import android.example.smartmeal.order.OrderTableViewModel
import android.example.smartmeal.order.OrderTableViewModelFactory
import android.example.smartmeal.products.ProductAdapter
import android.example.smartmeal.products.ProductModel
import android.example.smartmeal.table.TableModel
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class OrderProductActivity: AppCompatActivity() {
    private lateinit var binding: ActivityOrderproductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        val tableId = intent.getIntExtra("tableId", 0)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_orderproduct)
        binding.viewModel = ViewModelProviders.of(this, OrderProductViewModelFactory(tableId))
            .get(OrderProductViewModel::class.java)

        binding.viewModel!!.flagLoad.observe(this, Observer {
            if (it) {
                //binding.framelayoutOrderproduct.visibility = View.GONE
                //binding.progressBar.visibility = View.VISIBLE
            }
            else {
                //binding.framelayoutOrderproduct.visibility = View.VISIBLE
                //binding.progressBar.visibility = View.GONE
            }
        })

        val productAdapter = ProductAdapter {product -> adapterOnClick(product)}
        binding.recyclerProductViewOrderproduct.adapter = productAdapter
        binding.viewModel!!.list.observe(this, Observer {
            it.let {
                productAdapter.submitList(it as MutableList<ProductModel>)
            }
        })

        binding.txtSearchproduct.doAfterTextChanged {
            binding.viewModel!!.flagLoad.postValue(true)
            MainActivity.hubConnection.send("GetToken")
        }

        MainActivity.token.observe(this, Observer {
            if (binding.viewModel!!.flagLoad.value == false) {
                binding.viewModel!!.flagLoad.postValue(true)
                val url = Common.DOMAIN + "/Product/SearchByName"
                val client = OkHttpClient()
                val content = "{" +
                        "\"ProductName\": \"${binding.txtSearchproduct.text}\"" +
                        "}"
                val body = RequestBody.create(MediaType.parse("application/json"), content)
                val request = Request.Builder()
                    .addHeader("Authorization", it)
                    .url(url)
                    .post(body)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call?, response: Response?) {
                        val body = response?.body()?.string()
                        lateinit var responseModel: ResponseModel
                        val gson = Gson()
                        try {
                            responseModel = gson.fromJson(body, ResponseModel::class.java)
                            if (responseModel.status == false) {
                                //Thông báo lỗi không lấy dữ liệu thành công
                                return
                            }
                            var listProduct = gson.fromJson(
                                responseModel.content,
                                Array<ProductModel>::class.java
                            )
                            binding.viewModel!!.setList(listProduct.toMutableList())
                        } catch (ex: Exception) {
                            var c = ""
                        }
                    }

                    override fun onFailure(call: Call?, e: IOException) {
                        var a = ""
                        //Thông báo lỗi trong quá trình lấy dữ liệu bàn
                    }
                })
            }
        })
        binding.viewModel!!.flagLoad!!.postValue(true)
        MainActivity.hubConnection.send("GetToken")
    }

    private fun adapterOnClick(product: ProductModel) {
        try {
            var dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_chooseproduct) //set lại content
            var window = dialog.getWindow()
            var wlp = window?.attributes
            wlp?.gravity = Gravity.BOTTOM
            window?.attributes = wlp
            dialog.show()
        }
        catch (ex: Exception) {
            var a = ex.message
            var b = ""
        }
    }
}