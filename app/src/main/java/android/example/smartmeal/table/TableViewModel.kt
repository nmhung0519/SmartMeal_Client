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

    fun updateTable(table: TableModel) {
        var currentList = _tables.value?.toMutableList()
        if (currentList == null) _tables.postValue(listOf(table))
        else {
            var currentTable: TableModel? = null
            var n = currentList.count()
            for (i in 0 until n) {
                if (currentList[i].Id == table.Id) {
                    currentList.removeAt(i);
                    currentList.add(i, table)
                    break
                }
            }
            _tables.postValue(currentList!!)
        }
    }

}

class TableViewModelFactory(private val context: Context, token: String?) :ViewModelProvider.Factory {
    private var token = token
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TableViewModel::class.java)) {
            var model: TableViewModel = TableViewModel()
            return model as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}