package android.example.smartmeal.order

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.example.smartmeal.*
import android.example.smartmeal.databinding.ActivityTableorderBinding
import android.example.smartmeal.home.FragmentHome
import android.example.smartmeal.table.FragmentTable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class OrderTableActivity: AppCompatActivity() {
    private lateinit var binding: ActivityTableorderBinding
    private var isSending = false
    var tableId: Int = 0
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        tableId = intent.getIntExtra("tableId", 0)
        var tableName = intent.getStringExtra("tableName")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tableorder)
        binding.viewModel =
            ViewModelProviders.of(this, OrderTableViewModelFactory(tableId, tableName))
                .get(OrderTableViewModel::class.java)
        binding.txtTimeorder.setText(Common.DEFAULT_HOUR.toString())
        binding.txtOrderdate.setText(Timestamp(System.currentTimeMillis()).let { x -> (if (x.day < 10) "0" else "") + x.day + "/" + (if (x.month < 10) "0" else "") + x.month + "/" + x.year })
        binding.btnCancelorder.setOnClickListener{
            var intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

        binding.viewModel?.isOnLoad?.observe(this, Observer {
            it?.let {
                binding.btnOrder.isEnabled = !it
                binding.btnOrder.isEnabled = !it
                binding.btnCancelorder.isEnabled = !it
                binding.txtCustomer.isEnabled = !it
                binding.txtPhone.isEnabled = !it
                binding.txtCustomercount.isEnabled = !it
                binding.txtTimeorder.isEnabled = !it
            }
        })

        binding.viewModel?.isSuccess?.observe(this, Observer {
            it?.let {
                if (it) {
                    var intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        })

        MainActivity.hubConnection.on("Token", {token ->
            try {
                binding.viewModel?.setToken(token)
            }
            catch (ex: Exception) {
                var a = ""
            }
        }, String::class.java)

        binding.viewModel?.token?.observe(this, Observer {
            try {
                if (isSending) {
                    Log.d("Logging", "Trying to send order table...");
                    var timeValues = binding.txtOrderdate.text.toString().split("/");
                    //var time = "${timeValues[2]}-${timeValues[1]}-${timeValues[0]}"
                    var time = "2021-05-18"
                    Log.d("Logging", "Order at date: ${time}")
                    sendOrderTable(
                        it,
                        binding.txtTimeorder.text.toString().toInt(),
                        time,
                        binding.txtCustomer.text.toString(),
                        binding.txtPhone.text.toString()
                    )
                }
            }
            catch (ex: Exception) {
                isSending = false;binding.viewModel?.loadDone()
            }
        })

        MainActivity.hubConnection.on("Unauthorized", { ->
            Log.d("Logging", "Unauthorized")
            isSending = false
            binding.viewModel?.loadDone()
            Toast.makeText(baseContext, "Lỗi xác thực", Toast.LENGTH_SHORT).show()
        })


        binding.btnOrder.setOnClickListener{
            binding.viewModel!!.onLoad()
            isSending = true
            try
            {
                MainActivity.hubConnection.send("GetToken")
            }
            catch (ex: Exception) {
                Log.d("Logging", "Lỗi lấy token: " + ex.message)
                binding.viewModel?.loadDone()
                isSending = false
            }
        }
    }

    private fun sendOrderTable(token: String, hour: Int, date: String, customerName: String, customerContact: String) {
        Log.d("Logging", "sendOrderTable")
        val url = Common.DOMAIN + "/Table/Order"
        var content = "{" +
                "\"TableId\": " + tableId + "," +
                "\"StartTime\": \"" + date + "T"
        if (hour < 10) content = content + "0"
        content = content + hour + ":00:00\"," +"\"CustomerName\": \"" + customerName + "\"," +
                "\"CustomerContact\": \"" + customerContact + "\"" +
                "}"
        Log.d("Logging", "Request Body: " + content)
        val client = OkHttpClient()
        val body = RequestBody.create(
            MediaType.parse("application/json"), content)
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", token)
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                Log.d("Logging", "sendOrderTable on Response")
                val body = response?.body()?.string()
                Log.d("Logging", "response: ${body}")
                lateinit var responseModel: ResponseModel
                val gson = Gson()
                try {
                    responseModel = gson.fromJson(body, ResponseModel::class.java)
                    Log.d("Logging", "responseModel: " + responseModel.toString())
                    if (responseModel.status == true) {
                        Log.d("Logging", "sendOrderTable response is success")
                        binding.viewModel?.success()
                        return
                    }

                    Log.d("Logging", "sendOrderTable response is fail")
                    binding.viewModel?.loadDone()
                    isSending = false
                }
                catch (ex: Exception) {
                    Log.d("Logging", "sendOrderTable on Exception: ${ex.message}")
                    binding.viewModel?.loadDone()
                    isSending = false
                }
            }

            override fun onFailure(call: Call?, e: IOException) {
                Log.d("Logging", "sendOrderTable Failed")
                binding.viewModel?.loadDone()
                isSending = false
            }
        })
    }
}