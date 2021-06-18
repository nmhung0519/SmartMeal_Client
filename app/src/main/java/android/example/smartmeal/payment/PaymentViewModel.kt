package android.example.smartmeal.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PaymentViewModel {
    private val _data = MutableLiveData<DataPaymentModel>()
    val data : LiveData<DataPaymentModel>
        get() = _data

    fun setData(d: DataPaymentModel) {
        _data.postValue(d)
    }
}