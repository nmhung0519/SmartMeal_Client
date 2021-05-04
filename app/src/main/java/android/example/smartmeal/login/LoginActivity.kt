package android.example.smartmeal.login

import android.content.Intent
import android.example.smartmeal.MainActivity
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
    }
}