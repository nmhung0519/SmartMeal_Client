package android.example.smartmeal.order

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.example.smartmeal.Common
import android.example.smartmeal.MainActivity
import android.example.smartmeal.R
import android.example.smartmeal.ResponseModel
import android.example.smartmeal.databinding.ActivityTableorderBinding
import android.example.smartmeal.table.TableModel
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class OrderTableActivity: AppCompatActivity() {
    private lateinit var binding: ActivityTableorderBinding
    private var isSending = false
    var tableId: Int = 0
    var isEdit: Boolean = false
    var listHour = ArrayList<String>()
    var listMinute = ArrayList<String>()
    var waitingData = false
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        tableId = intent.getIntExtra("tableId", 0)
        isEdit = (intent.getIntExtra("isNew", 0) == 1)
        var tableName = intent.getStringExtra("tableName")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tableorder)
        binding.viewModel =
            ViewModelProviders.of(this, OrderTableViewModelFactory(tableId, tableName))
                .get(OrderTableViewModel::class.java)
        for (h in 7 .. 23) {
            if (h < 10) listHour.add("0" + h)
            else listHour.add("" + h)
        }
        val adapterHour: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item, listHour
        )
        adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.txtHourorder.setAdapter(adapterHour)

        listMinute = ArrayList<String>()
        for (m in 0 .. 55 step 5) {
            if (m < 10) listMinute.add("0" + m)
            else listMinute.add("" + m)
        }
        val adapterMinute: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item, listMinute
        )
        adapterMinute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.txtMinuteorder.setAdapter(adapterMinute)

        binding.btnCancelorder.setOnClickListener{
            var intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

        binding.viewModel?.isOnLoad?.observe(this, Observer {
            if (isEdit) {
                it?.let {
                    binding.btnOrder.isEnabled = !it
                    binding.btnOrder.isEnabled = !it
                    binding.btnCancelorder.isEnabled = !it
                    binding.txtCustomer.isEnabled = !it
                    binding.txtPhone.isEnabled = !it
                    binding.txtCustomercount.isEnabled = !it
                    binding.txtHourorder.isEnabled = !it
                    binding.txtMinuteorder.isEnabled = !it
                }
            }
        })

        binding.viewModel?.isSuccess?.observe(this, Observer {
            it?.let {
                if (isEdit && it) {
                    var intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        })

        MainActivity.token.observe(this, Observer {
            try {
                binding.viewModel?.setToken(it)
            }
            catch (ex: Exception) {
                var a = ""
            }
        })

        binding.viewModel?.token?.observe(this, Observer {
            try {
                if (isSending) {
                    isSending = false
                    Log.d("Logging", "Trying to send order table...");
                    var timeValues = binding.txtOrderdate.text.toString().split("/");
                    var date = "${timeValues[2]}-${timeValues[1]}-${timeValues[0]}"
                    Log.d("Logging", "Order at date: ${date}")
                    sendOrderTable(
                        it,
                        listHour[binding.txtHourorder.selectedItemPosition] + ":" + listMinute[binding.txtMinuteorder.selectedItemPosition] + ":00",
                        date,
                        binding.txtCustomer.text.toString(),
                        binding.txtPhone.text.toString()
                    )
                }
                if (waitingData) {
                    waitingData = false
                    getDataInfor()
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
            //Toast.makeText(baseContext, "Lỗi xác thực", Toast.LENGTH_SHORT).show()
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

        if (!isEdit) {
            binding.ordertableBtnEdit.setOnClickListener {
                viewEdit()
            }
            viewInfor()
        }
        else {
            var currentTime = Calendar.getInstance()
            binding.txtOrderdate.setText(
                "" +
                        (if (currentTime.get(Calendar.DAY_OF_MONTH) < 10) "0" else "") + currentTime.get(
                    Calendar.DAY_OF_MONTH
                ) + "/" +
                        (if (currentTime.get(Calendar.MONTH) < 9) "0" else "") + (currentTime.get(
                    Calendar.MONTH
                ) + 1) + "/" +
                        currentTime.get(Calendar.YEAR)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewModel = null
    }

    private fun sendOrderTable(token: String, time: String, date: String, customerName: String, customerContact: String) {
        isSending = false
        Log.d("Logging", "sendOrderTable")
        val url = Common.DOMAIN + "/Table/Order"
        var content = "{" +
                "\"Id\": " + binding.viewModel!!.order!!.Id + "," +
                "\"TableId\": " + tableId + "," +
                "\"StartTime\": \"" + date + "T" + time + "\"," +"\"CustomerName\": \"" + customerName + "\"," +
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
                }
                catch (ex: Exception) {
                    Log.d("Logging", "sendOrderTable on Exception: ${ex.message}")
                    binding.viewModel?.loadDone()
                }
            }

            override fun onFailure(call: Call?, e: IOException) {
                Log.d("Logging", "sendOrderTable Failed")
                binding.viewModel?.loadDone()
                isSending = false
            }
        })
    }

    private fun viewInfor() {
        binding.btnOrder.isEnabled = false
        binding.txtCustomer.isEnabled = false
        binding.txtPhone.isEnabled = false
        binding.txtCustomercount.isEnabled = false
        binding.txtHourorder.isEnabled = false
        binding.txtMinuteorder.isEnabled = false
        binding.btnCancelorder.text = "Đóng"
        binding.btnOrder.visibility = View.GONE
        binding.ordertableBtnEdit.visibility = View.VISIBLE
        binding.ordertableBtnEdit.isEnabled = true

        waitingData = true
        MainActivity.hubConnection.send("GetToken")
    }

    private fun getDataInfor() {
        Log.d("Logging", "getDataInfor")
        val url = Common.DOMAIN + "/Order/GetPreWithTable"
        var content = "{" +
                "\"TableId\": " + tableId +
                "}"
        val client = OkHttpClient()
        val body = RequestBody.create(
            MediaType.parse("application/json"), content)
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", binding.viewModel?.token?.value)
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                lateinit var responseModel: ResponseModel
                val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
                try {
                    responseModel = gson.fromJson(body, ResponseModel::class.java)
                    if (responseModel.status == true) {
                        var order = gson.fromJson(responseModel.content, OrderModel::class.java)
                        bindDataInfor(order)
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

    private fun bindDataInfor(order: OrderModel) {
        binding.txtCustomer.setText(order.CustomerName)
        binding.txtPhone.setText(order.CustomerContact)
        binding.txtOrderdate.setText(Common.toViewDate(order.StartTime))
        for (i in 0..listHour.count()) {
            if (binding.txtHourorder.adapter.getItem(i) == Common.getHour(order.StartTime)) {
                binding.txtHourorder.setSelection(i)
                break;
            }
        }
        for (i in 0..listMinute.count()) {
            if (binding.txtMinuteorder.adapter.getItem(i) == Common.getMinute(order.StartTime)) {
                binding.txtMinuteorder.setSelection(i)
                break;
            }
        }
        binding.viewModel?.order?.Id = order.Id
     }

    private fun viewEdit() {
        isEdit = true
        binding.btnOrder.isEnabled = true
        binding.txtCustomer.isEnabled = true
        binding.txtPhone.isEnabled = true
        binding.txtCustomercount.isEnabled = true
        binding.txtHourorder.isEnabled = true
        binding.txtMinuteorder.isEnabled = true
        binding.btnCancelorder.text = "Đóng"
        binding.btnOrder.setText("Cập nhật")
        binding.btnOrder.visibility = View.VISIBLE
        binding.ordertableBtnEdit.visibility = View.GONE
    }
}