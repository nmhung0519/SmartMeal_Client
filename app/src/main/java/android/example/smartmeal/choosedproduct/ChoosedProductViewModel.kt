package android.example.smartmeal.choosedproduct

import android.example.smartmeal.orderproduct.OrderDetailModel
import android.example.smartmeal.products.ProductModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ChoosedProductViewModel : ViewModel() {
    private val _listSelected = MutableLiveData<List<OrderDetailModel>>()
    val listSelected: LiveData<List<OrderDetailModel>>
        get() = _listSelected

    private lateinit var _listProduct: List<ProductModel>
    private var _countProduct = 0

    fun setListProduct(listProductModel: List<ProductModel>) {
        _listProduct = listProductModel
        _countProduct = _listProduct.count()
    }

    fun setList(listOrder: List<OrderDetailModel>) {
        _listSelected.postValue(listOrder)
    }

    fun removeAt(index: Int) {
        var _tmp: MutableList<OrderDetailModel>? = _listSelected.value?.toMutableList()
        if (_tmp == null || index > _tmp.count()) return;

        _tmp!!.removeAt(index)
        setList(_tmp)
    }

    fun getProduct(productId: Int): ProductModel {
        for (i in 0 until _countProduct) {
            if (_listProduct[i].Id == productId) return _listProduct[i]
        }
        return ProductModel()
    }

    fun updateCount(productId: Int, count: Int) {
        var _tmp: MutableList<OrderDetailModel>? = _listSelected.value?.toMutableList()
        var n = _tmp!!.count()
        for (i in 0 until n) {
            if (_tmp[i].ProductId == productId) {
                _tmp[i].ProductCount = count
                break
            }
        }
        setList(_tmp)
    }
}

class ChoosedProductViewModelFactory(private val listProduct: List<ProductModel>, private val listOrder: List<OrderDetailModel>) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChoosedProductViewModel::class.java)) {
            var model = ChoosedProductViewModel()
            model.setList(listOrder)
            model.setListProduct(listProduct)
            return model as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}