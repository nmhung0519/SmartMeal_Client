package android.example.smartmeal.login

import android.content.Intent
import android.example.smartmeal.*
import android.example.smartmeal.databinding.FragmentLoginBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.google.gson.Gson
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
            val url = Common.DOMAIN + "/Account/Login"
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
                            viewModelLogin.UpdateMsg("" + responseModel.content)
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

            viewModelLogin.msg.observe(viewLifecycleOwner, Observer {
                //Toast.makeText(context, viewModelLogin.msg.value, Toast.LENGTH_SHORT).show()
                binding.txtErrMsg.text = it;
            })

            viewModelLogin.user.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    val mainAct = Intent(activity, MainActivity::class.java)
                    mainAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
                    mainAct.putExtra("id", it.Id)
                    mainAct.putExtra("username", it.Username)
                    mainAct.putExtra("fullname", it.Fullname)
                    mainAct.putExtra("age", it.Age)
                    mainAct.putExtra("sex", it.Sex)
                    mainAct.putExtra("token", it.Token)
                    startActivity(mainAct)
                    activity?.finish()
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
