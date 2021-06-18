package android.example.smartmeal.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.example.smartmeal.Common
import android.example.smartmeal.MainActivity
import android.example.smartmeal.R
import android.example.smartmeal.ResponseModel
import android.example.smartmeal.databinding.ActivityPaymentBinding
import android.example.smartmeal.order.OrderModel
import android.example.smartmeal.orderproduct.OrderProductActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class ActivityPayment : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private var _flag = false
    private var _send = false

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tableId = intent.getIntExtra("tableId", 0)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)

        binding.viewModel = PaymentViewModel()

        val adapter = PaymentAdapter()
        binding.recyclerPm.adapter = adapter

        binding.btnPaymentBack.setOnClickListener{
            finish()
        }

        binding.btnPayment.setOnClickListener{
            _send = true
            binding.btnPayment.isEnabled = false
            MainActivity.hubConnection.send("GetToken")
        }

        binding.viewModel!!.data.observe(this, Observer {
            if (it != null) {
                binding.txtPmTablename.text = it.TableName
                binding.txtPmCustomername.text = it.CustomerName
                binding.txtPmCustomerphone.text = it.CustomerPhone
                var amount = 0;
                if (it.Data != null && it.Data!!.count() > 0) {
                    adapter.submitList(it.Data)
                    val n = it.Data!!.count()
                    for (i in 0 until n) {
                        amount += it.Data!![i].ProductCount * it.Data!![i].ProductPrice
                    }
                }
                else {
                    binding.txtPmEmptylist.visibility = View.VISIBLE
                }

                binding.btnPayment.text = "Thanh toán • " + Common.toMoneyFormat(amount) + "đ"
                binding.btnPayment.isEnabled = true
            }
        })

        MainActivity.token.observe(this, Observer {
            if (_flag) {
                _flag=false
                val url = Common.DOMAIN + "/Table/GetPaymentInfo?tableId=" + tableId
                val client = OkHttpClient()
                val request = Request.Builder()
                    .addHeader("Authorization", it)
                    .url(url)
                    .get()
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
                            var dtPayment = gson.fromJson(
                                responseModel.content,
                                DataPaymentModel::class.java
                            )
                            binding.viewModel!!.setData(dtPayment)
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

            if (_send) {
                try {
                    var url = Common.DOMAIN + "/Table/Pay?tableId=" + tableId;
                    val client = OkHttpClient()
                    val body = RequestBody.create(
                        MediaType.parse("application/json"), "")
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
                            //binding.btnPayment.isEnabled = false
                        }

                        override fun onFailure(call: Call, e: IOException) {
                            //binding.btnPayment.isEnabled = false
                        }
                    })
                }
                catch (ex: Exception) {
                    //binding.btnPayment.isEnabled = false
                }
            }
        })
        _flag = true
        MainActivity.hubConnection.send("GetToken")
    }

}