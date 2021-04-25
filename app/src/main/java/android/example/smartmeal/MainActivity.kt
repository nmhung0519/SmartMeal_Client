package android.example.smartmeal

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import android.widget.Button
import android.widget.Toast
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
        hubConnection = HubConnectionBuilder.create("http://192.168.1.190/movehub").build()

        hubConnection.on("ReceiveNewPosition", {newX, newY ->
            view_move.x = newX!!
            view_move.y = newY!!
            Toast.makeText(this.baseContext, "ReceiveNewPosition x:" + newX + " y: " + newY, Toast.LENGTH_SHORT)
        }, Float::class.java, Float::class.java)

        btn_start = findViewById(R.id.btn_start)
        view_move = findViewById(R.id.view_move)

        btn_start.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java).apply {}
            startActivity(intent)
        }

        view_move.setOnTouchListener {
            view, motionEvent ->
            val x = motionEvent.rawX
            val y = motionEvent.rawY

            view.x = x
            view.y = y

            if (hubConnection.connectionState == HubConnectionState.CONNECTED)
                hubConnection.send("MoveViewFromServer", x, y)
        true }
    }
}