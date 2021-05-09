package android.example.smartmeal.products

import android.content.Context
import android.example.smartmeal.table.TableModel
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso

class ProductViewModel : ViewModel() {
    private val _products = MutableLiveData<List<ProductModel>>()
    val products: LiveData<List<ProductModel>>
        get() = _products

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

    fun setProducts(listProduct: List<ProductModel>) {
        _products.postValue(listProduct)
    }

}

class ProductViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {

            var model: ProductViewModel = ProductViewModel()
            model.setProducts(listOf(ProductModel(1, "Rice"), ProductModel(2, "Noodles"), ProductModel(3, "abc")))
            return model as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}