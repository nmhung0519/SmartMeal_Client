package android.example.smartmeal.table

import android.content.Context
import android.example.smartmeal.Common
import android.example.smartmeal.R
import android.example.smartmeal.ResponseModel
import android.example.smartmeal.User
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class TableViewModel : ViewModel() {
    private val _tables = MutableLiveData<List<TableModel>>()
    val tables: LiveData<List<TableModel>>
            get() = _tables
    fun inserTable(table: TableModel) {
        var updatedList = tables.value?.toMutableList()
        try {
            if (updatedList == null) _tables.postValue(listOf<TableModel>(table))
            else {
                updatedList!!.add(table)
                _tables.postValue(updatedList!!)
            }
        }
        catch (ex: Exception) {
            var a = 1
        }
    }

    fun setTable(listTable: List<TableModel>) {
        _tables.postValue(listTable)
    }

}

class TableViewModelFactory(private val context: Context, token: String?) :ViewModelProvider.Factory {
    private var token = token
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TableViewModel::class.java)) {
            var model: TableViewModel = TableViewModel()
            try {
                val url = Common.DOMAIN + "/Table/GetAllActive"
                val client = OkHttpClient()
                val request = Request.Builder()
                    .addHeader("Authorization", token)
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
                            var listTable = gson.fromJson(responseModel.content, Array<TableModel>::class.java)
                            model.setTable(listTable.toMutableList())
                        }
                        catch (ex: Exception) {
                            var c = ""
                            //Thông báo lỗi trong quá trình lấy dữ liệu bàn
                        }
                    }

                    override fun onFailure(call: Call?, e: IOException) {
                        var a = ""
                        //Thông báo lỗi trong quá trình lấy dữ liệu bàn
                    }
                })
            }
            catch (ex: Exception) {
                //
            }
            return model as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}