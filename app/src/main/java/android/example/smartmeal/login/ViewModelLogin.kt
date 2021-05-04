package android.example.smartmeal.login

import android.example.smartmeal.User
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.lang.Exception

class ViewModelLogin() {
    private val _msg = MutableLiveData<String>()
    val msg : LiveData<String>
        get() = _msg

    private val _user = MutableLiveData<User>()
    val user : LiveData<User>
        get() = _user
    fun UpdateMsg(message: String) {
        try {
            _msg.postValue(message)
        }
        catch (ex: Exception) {
            _msg.postValue(ex.message)
        }
    }

    fun SetUser(user: User) {
        _user.postValue(user)
    }
}