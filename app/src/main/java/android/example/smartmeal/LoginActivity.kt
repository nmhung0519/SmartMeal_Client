package android.example.smartmeal

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtUsername = findViewById<TextView>(R.id.txtUsername)
        val txtPassword = findViewById<TextView>(R.id.txtPassword)

        txtUsername.text = "admin"
        txtPassword.text = "admin"
        var userViewModel: UserViewModel = UserViewModel()
        btnLogin.setOnClickListener {
            val url = "http://192.168.1.190:8080/Account"
            val username = txtUsername.text.toString()
            val password = txtPassword.text.toString()
            val content = "{ \"$username\": \"admin\", \"Password\": \"$password\"}"
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
                }

                override fun onFailure(call: Call?, e: IOException) {
                }
            })
        }

        userViewModel.user.observe(this, Observer {
            Toast.makeText(baseContext, "Đăng nhập thành công - ", Toast.LENGTH_SHORT).show()
        })

        userViewModel.msg.observe(this, Observer {
            if (it != "") {
                Toast.makeText(baseContext, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                userViewModel.GotMsg()
            }

        })
    }
}