package android.example.smartmeal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserViewModel() {
    private val _msg = MutableLiveData<String>()
    val msg: LiveData<String>
        get() = _msg

    private val _userId = MutableLiveData<Int>()
    val user: LiveData<Int>
        get() = _userId

    fun GotMsg() {
        _msg.value = "";
    }
}
