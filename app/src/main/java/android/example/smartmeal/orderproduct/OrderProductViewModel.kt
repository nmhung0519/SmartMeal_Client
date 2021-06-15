package android.example.smartmeal.orderproduct

import android.example.smartmeal.Common
import android.example.smartmeal.products.ProductModel
import android.example.smartmeal.table.TableModel
import android.example.smartmeal.table.TableViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.sql.Timestamp

class OrderProductViewModel: ViewModel() {
    var tableId: Int = 0
    var flagLoad = MutableLiveData<Boolean>()
    private val _list = MutableLiveData<List<ProductModel>>()
    val list: LiveData<List<ProductModel>>
        get() = _list

    private val _listSelected = MutableLiveData<List<OrderDetailModel>>()
    val listSelected: LiveData<List<OrderDetailModel>>
        get() = _listSelected

    fun setList(listProduct: List<ProductModel>) {
        _list.postValue(listProduct)
    }

    fun addSelectedProduct(productId: Int, count: Int) {
        var _tmpList: List<OrderDetailModel>? = null
        if (_listSelected.value == null) {
            _tmpList = listOf<OrderDetailModel>()
        }
        else {
            _tmpList = _listSelected.value!!.toMutableList()
        }
        var _flag = true
        for (i in 0 until _listSelected.value!!.count()) {
            if (_tmpList[i].ProductId == productId) {
                _tmpList[i].ProductCount += count
                _flag = false
                break
            }
        }

        if (_flag) {
            var product: ProductModel? = null
            _list.value?.forEach {
                if (it.Id == productId) product = it
            }
            var _orderDetail = OrderDetailModel()
            _orderDetail.ProductId = productId
            _orderDetail.ProductCount = count
            if (product != null) {
                _orderDetail.ProductName = product!!.ProductName
                _orderDetail.ProductPrice = product!!.ProductPrice
            }
        }
    }

    fun updateProduct(product: ProductModel) {
        var isExist = false
        var currentList = _list.value?.toMutableList()
        if (currentList == null) _list.postValue(listOf(product))
        else {
            var currentProduct: ProductModel? = null
            var n = currentList.count()
            for(i in 0 until n) {
                if (currentList[i].Id == product.Id) {
                    currentList.removeAt(i);
                    currentList.add(i, product)
                    isExist = true
                    break;
                }
            }
            if (isExist) currentList.add(product)
            _list.postValue(currentList!!)

        }
    }
}

class OrderProductViewModelFactory(private val tableId: Int = 0) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderProductViewModel::class.java)) {
            var model = OrderProductViewModel()
            model.tableId = tableId
            model.flagLoad.value = false
            return model as T
        }
        throw IllegalArgumentException("Unkown ViewModel class")
    }
}