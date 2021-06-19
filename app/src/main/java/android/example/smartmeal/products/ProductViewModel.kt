package android.example.smartmeal.products

import android.content.Context
import android.example.smartmeal.table.TableModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProductViewModel : ViewModel() {
    private val _products = MutableLiveData<List<ProductModel>>()
    val products: LiveData<List<ProductModel>>
        get() = _products
    var flag = 0

    fun inserProduct(product: ProductModel) {
        var updatedList = products.value?.toMutableList()
        try {
            if (updatedList == null) _products.postValue(listOf<ProductModel>(product))
            else {
                updatedList!!.add(product)
                _products.postValue(updatedList!!)
            }
        } catch (ex: Exception) {
            var a = 1
        }
    }

    fun updateProduct(product: ProductModel) {
        var currentList = _products.value?.toMutableList()
        if (currentList == null) _products.postValue(listOf(product))
        else {
            var n = currentList.count()
            var _flag = true
            for (i in 0 until n) {
                if (currentList[i].Id == product.Id) {
                    currentList.removeAt(i);
                    currentList.add(i, product)
                    _flag = false
                    break
                }
            }
            if (_flag) {
                currentList.add(product)
            }
            _products.postValue(currentList!!)
        }
    }

    fun setProducts(listProduct: List<ProductModel>) {
        _products.postValue(listProduct)
    }

}

class ProductViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {

            var model: ProductViewModel = ProductViewModel()
            return model as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}