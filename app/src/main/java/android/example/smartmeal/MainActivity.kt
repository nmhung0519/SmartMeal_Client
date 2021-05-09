package android.example.smartmeal

import android.annotation.SuppressLint
import android.content.Intent
import android.example.smartmeal.login.LoginActivity
import android.example.smartmeal.table.FragmentTable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import java.sql.Timestamp

class MainActivity : AppCompatActivity() {
    lateinit var hubConnection: HubConnection
    private lateinit var btn_start: Button
    private lateinit var view_move: View
    private lateinit var navbar: BottomNavigationView
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = getIntent()
        val userid = intent.getIntExtra("id", 0)
        val username = intent.getStringExtra("username")
        val fullname = intent.getStringExtra("fullname")
        val token = intent.getStringExtra("token")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentManager: FragmentManager =  supportFragmentManager
        val fragTable = FragmentTable()

        fragmentManager.beginTransaction().add(R.id.fragment_container, fragTable, "Table").commit()
        navbar = findViewById(R.id.navbar)
        navbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_1 -> {
                    Toast.makeText(baseContext, "1", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_2 -> {
                    Toast.makeText(baseContext, "2", Toast.LENGTH_SHORT).show()
                    fragTable.InsertTable()
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
//        hubConnection = HubConnectionBuilder.create("http://172.20.10.3/hub").build()
//
//        hubConnection.on("Notify", {objId, objType, actType, content, createdTime ->
//            Toast.makeText(this.baseContext, content , Toast.LENGTH_SHORT).show()
//        }, Int::class.java, String::class.java, String::class.java, String::class.java, Timestamp::class.java)
//
//        try {
//            hubConnection.start()
//        }
//        catch (ex: Exception) {
//            val tmp = ex.message
//        }

    }

}