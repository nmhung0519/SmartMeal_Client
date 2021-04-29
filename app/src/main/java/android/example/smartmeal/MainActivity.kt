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
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState

class MainActivity : AppCompatActivity() {
    lateinit var hubConnection: HubConnection
    private lateinit var btn_start: Button
    private lateinit var view_move: View
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            Toast.makeText(this.baseContext, "Null", Toast.LENGTH_SHORT).show()
        }
        setContentView(R.layout.activity_main)
        if (false) {
            val intent = Intent(this, LoginActivity::class.java).apply {}
            startActivity(intent)
            return
        }

        val fragmentManager: FragmentManager =  supportFragmentManager

        val fragmentTable = FragmentTable()

        fragmentManager.beginTransaction().add(R.id.fragment_container, fragmentTable, "Table").commit()

    }
}