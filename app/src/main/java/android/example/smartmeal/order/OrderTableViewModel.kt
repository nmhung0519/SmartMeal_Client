package android.example.smartmeal.order

import android.example.smartmeal.Common
import android.example.smartmeal.table.TableModel
import android.example.smartmeal.table.TableViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.sql.Timestamp

class OrderTableViewModel: ViewModel() {
    var tableName: String = ""
    lateinit var order: OrderModel

    private var _isOnLoad = MutableLiveData<Boolean>()
    val isOnLoad: LiveData<Boolean>
        get() = _isOnLoad
    private var _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean>
        get() = _isSuccess
    private var _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token
    init {
        _isSuccess.value = false
    }
    fun onLoad() {
        try {
            _isOnLoad.postValue(true)
        }
        catch (ex: Exception) {
            var a = ex.message
        }
    }

    fun setToken(tokenValue: String) {
        try {
            _token.postValue(tokenValue)
        }
        catch (ex: Exception) {
            Log.d("Logging", "[OrderTableViewModel_setToken_Exception] - " + ex.message)
        }
    }

    fun loadDone() {
        try {
            _isOnLoad.postValue(false)
        }
        catch (ex: Exception) {
            var a = ex.message
        }
    }

    fun success() {
        _isSuccess.postValue(true)
    }

}

class OrderTableViewModelFactory(private val tableId: Int = 0, private val tableName: String? = "") :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderTableViewModel::class.java)) {
            var viewModel = OrderTableViewModel()
            viewModel.tableName = "" + tableName
            viewModel.order = OrderModel(0, tableId)
            viewModel.loadDone()
            return viewModel as T
        }
        throw IllegalArgumentException("Unkown ViewModel class")
    }

}