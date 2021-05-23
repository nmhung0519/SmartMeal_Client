package android.example.smartmeal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.example.smartmeal.home.FragmentHome
import android.example.smartmeal.login.LoginActivity
import android.example.smartmeal.table.FragmentTable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import java.sql.Timestamp

class MainActivity : AppCompatActivity() {
    private lateinit var btn_start: Button
    private lateinit var view_move: View
    private lateinit var navbar: BottomNavigationView
    private lateinit var  fragHome: FragmentHome
    private lateinit var fragTable: FragmentTable
    @SuppressLint("ClickableViewAccessibility")
    companion object {
        lateinit var hubConnection: HubConnection
        var token = MutableLiveData<String>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = getIntent()
        val userid = intent.getIntExtra("id", 0)
        val username = intent.getStringExtra("username")
        val fullname = intent.getStringExtra("fullname")
        val sToken = intent.getStringExtra("token")
        token.postValue("" + sToken)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentManager: FragmentManager =  supportFragmentManager
        fragHome = FragmentHome()
        fragTable = FragmentTable()
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragHome, "Home").commit()
        navbar = findViewById(R.id.navbar)
        navbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_1 -> {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragHome, "Home").commit()
                }
                R.id.nav_2 -> {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragTable, "Table").commit()
                }
                R.id.nav_3 -> {
                    Toast.makeText(baseContext, "3", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_4 -> {
                    Toast.makeText(baseContext, "4", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        //Tạo kết nối đến Main Hub (Để nhận notify)
        hubConnection = HubConnectionBuilder.create(Common.DOMAIN + "/hub").build()

        hubConnection.on("Notify", {objId, objType, actType, content, createdTime ->
            Toast.makeText(this.baseContext, content , Toast.LENGTH_SHORT).show()
        }, Int::class.java, String::class.java, String::class.java, String::class.java, Timestamp::class.java)

        hubConnection.on("Token", {
            token.postValue(it)
        }, String::class.java)

        try {
            hubConnection.start().blockingAwait()
        }
        catch (ex: Exception) {
            val tmp = ex.message
        }

        hubConnection.send("Register", username, sToken)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Common.REQUEST_CODE_ORDERTABLE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(baseContext, "Đặt bàn thành công", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == Common.REQUEST_CODE_ORDERTABLEINFOR) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(baseContext, "Cập nhật thông tin đặt bàn thành công", Toast.LENGTH_SHORT).show()
            }
        }
    }


}