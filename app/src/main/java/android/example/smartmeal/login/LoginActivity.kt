package android.example.smartmeal.login

import android.example.smartmeal.R
import android.example.smartmeal.databinding.ActivityLoginBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import okhttp3.*


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)


        val fragmentManager: FragmentManager =  supportFragmentManager

        val fragmentLogin = FragmentLogin()
        val fragmentLogon = FragmentLogon()
        fragmentManager.beginTransaction().add(R.id.sign_frame, fragmentLogin, "Login").commit()

        binding.button.setOnClickListener{
            fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.sign_frame, fragmentLogin).addToBackStack(null).commit()
        }

        binding.button2.setOnClickListener{
            fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.sign_frame, fragmentLogon).addToBackStack(null).commit()
        }

       /* val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtUsername = findViewById<TextView>(R.id.txtUsername)
        val txtPassword = findViewById<TextView>(R.id.txtPassword)

        txtUsername.text = "admin"
        txtPassword.text = "admin"
        var userViewModel: UserViewModel = UserViewModel()
        btnLogin.setOnClickListener {*/


//            val url = "http://192.168.1.190:8080/Account"
//            val username = txtUsername.text.toString()
//            val password = txtPassword.text.toString()
//            val content = "{ \"$username\": \"admin\", \"Password\": \"$password\"}"
//            val client = OkHttpClient()
//            val body = RequestBody.create(
//                    MediaType.parse("application/json"), content)
//
//            val request = Request.Builder()
//                    .url(url)
//                    .post(body)
//                    .build()
//            client.newCall(request).enqueue(object : Callback {
//                override fun onResponse(call: Call?, response: Response?) {
//                    val body = response?.body()?.string()
//                }
//
//                override fun onFailure(call: Call?, e: IOException) {
//                }
//            })
//        }

//        userViewModel.user.observe(this, Observer {
//            Toast.makeText(baseContext, "Đăng nhập thành công - ", Toast.LENGTH_SHORT).show()
//        })
//
//        userViewModel.msg.observe(this, Observer {
//            if (it != "") {
//                Toast.makeText(baseContext, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
//                userViewModel.GotMsg()
//            }
//
//        })
    }
}