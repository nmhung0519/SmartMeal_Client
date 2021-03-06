package android.example.smartmeal

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.example.smartmeal.account.FragmentAccount
import android.example.smartmeal.home.FragmentHome
import android.example.smartmeal.products.FragmentProduct
import android.example.smartmeal.table.FragmentTable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import java.io.File
import java.sql.Timestamp
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var btn_start: Button
    private lateinit var view_move: View
    private lateinit var navbar: BottomNavigationView
    private lateinit var  fragHome: FragmentHome
    private lateinit var fragTable: FragmentTable
    private lateinit var fragProduct: FragmentProduct
    private lateinit var fragAccount: FragmentAccount
    @SuppressLint("ClickableViewAccessibility")
    companion object {
        lateinit var hubConnection: HubConnection
        var token = MutableLiveData<String>()
        var msgT = MutableLiveData<String>()
        private var roleId: Int = 0;
        fun getRole(): Int {
            return roleId
        }
        val imgPicker = MutableLiveData<Uri>()
        var fullname = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = getIntent()
        val userid = intent.getIntExtra("id", 0)
        val username = intent.getStringExtra("username")
        fullname = "" + intent.getStringExtra("fullname")
        val sToken = intent.getStringExtra("token")
        roleId = intent.getIntExtra("roleId", 0)
        token.postValue("" + sToken)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentManager: FragmentManager =  supportFragmentManager
        fragHome = FragmentHome()
        fragTable = FragmentTable()
        fragProduct = FragmentProduct()
        fragAccount = FragmentAccount()
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
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragProduct, "Product").commit()
                }
                R.id.nav_4 -> {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragAccount, "Account").commit()
                }
            }
            true
        }
        //T???o k???t n???i ?????n Main Hub (????? nh???n notify)
        hubConnection = HubConnectionBuilder.create(Common.DOMAIN + "/hub").build()

        hubConnection.on("Notify", {objId, objType, actType, content, createdTime ->
            Toast.makeText(this.baseContext, content , Toast.LENGTH_SHORT).show()
        }, Int::class.java, String::class.java, String::class.java, String::class.java, Timestamp::class.java)

        hubConnection.on("Token", {
            token.postValue(it)
        }, String::class.java)

        hubConnection.on("Logout", {
            var dialog = Dialog(baseContext)
            dialog.setTitle("B???n ???? ????ng xu???t")
            dialog.show()
        })

        msgT.observe(this, Observer {
            if (it != null && it != "") {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                msgT.postValue("")
            }
        })

        try {
            hubConnection.start().blockingAwait()
        }
        catch (ex: Exception) {
            val tmp = ex.message
        }

        hubConnection.send("Register", username, sToken)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Common.REQUEST_CODE_ORDERTABLE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(baseContext, "?????t b??n th??nh c??ng", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == Common.REQUEST_CODE_ORDERTABLEINFOR) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(baseContext, "C???p nh???t th??ng tin ?????t b??n th??nh c??ng", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == Common.REQUEST_CODE_ORDERPRODUCT) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(baseContext, "?????t m??n th??nh c??ng", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == Common.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(baseContext, "Thanh to??n th??nh c??ng", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == Common.REQUEST_CODE_SELECT_IMAGE_IN_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                imgPicker.postValue(data?.data)
            }
        }
    }
}