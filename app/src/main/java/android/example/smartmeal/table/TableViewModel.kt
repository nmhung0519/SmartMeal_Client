package android.example.smartmeal.table

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TableViewModel : ViewModel() {
    private val _tables = MutableLiveData<List<TableModel>>()
    val tables: LiveData<List<TableModel>>
            get() = _tables
    fun inserTable(table: TableModel) {
        val updatedList = tables.value?.toMutableList()
        updatedList?.add(table)
        try {
            var newTables= listOf(table, TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"), TableModel(2, "Bàn 2"))

            _tables.postValue(newTables)
        }
        catch (ex: Exception) {
            var a = 1
        }
    }

}

class TableViewModelFactory(private val context: Context) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TableViewModel::class.java)) {
            var model: TableViewModel = TableViewModel()
            model.inserTable(TableModel(1, "Bàn 1"))
            return model as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}