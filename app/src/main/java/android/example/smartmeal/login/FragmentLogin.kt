package android.example.smartmeal.login

import android.content.Intent
import android.example.smartmeal.MainActivity
import android.example.smartmeal.R
import android.example.smartmeal.ResponseModel
import android.example.smartmeal.User
import android.example.smartmeal.databinding.FragmentLoginBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

class FragmentLogin : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        val viewModelLogin = ViewModelLogin()
        binding.txtUsername.setText("admin")
        binding.txtPassword.setText("admin123")
        binding.viewModelLogin = viewModelLogin
        binding.btnLogin.setOnClickListener {
            val url = "http://172.20.10.3/Account/Login"
            val username = binding.txtUsername.text.toString()
            val password = binding.txtPassword.text.toString()
            val content = "{ \"Username\": \"${username}\", \"Password\": \"${md5(password)}\"}"
            val client = OkHttpClient()
            val body = RequestBody.create(
                    MediaType.parse("application/json"), content)
            val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call?, response: Response?) {
                    val body = response?.body()?.string()
                    lateinit var responseModel: ResponseModel
                    val gson = Gson()
                    try {
                        responseModel = gson.fromJson(body, ResponseModel::class.java)
                        if (responseModel.status == false) {
                            viewModelLogin.UpdateMsg(responseModel.content)
                            return
                        }
                        viewModelLogin.SetUser(gson.fromJson(responseModel.content, User::class.java))
                    }
                    catch (ex: Exception) {
                        viewModelLogin.UpdateMsg("Đăng nhập không thành công")
                    }
                }

                override fun onFailure(call: Call?, e: IOException) {
                    viewModelLogin.UpdateMsg("Đăng nhập không thành công");
                }
            })

            viewModelLogin.msg.observe(this, Observer {
                //Toast.makeText(context, viewModelLogin.msg.value, Toast.LENGTH_SHORT).show()
                binding.txtErrMsg.text = it;
            })

            viewModelLogin.user.observe(this, Observer {
                if (it != null) {
                    val mainAct = Intent(activity, MainActivity::class.java)
                    mainAct.putExtra("id", it.id)
                    mainAct.putExtra("username", it.username)
                    mainAct.putExtra("fullname", it.fullname)
                    mainAct.putExtra("age", it.age)
                    mainAct.putExtra("sex", it.sex)
                    mainAct.putExtra("token", it.token)
                    startActivity(mainAct)
                }
            })
        }
        return binding.root
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}
