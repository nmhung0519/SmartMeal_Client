package android.example.smartmeal.account

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.example.smartmeal.Common
import android.example.smartmeal.MainActivity
import android.example.smartmeal.R
import android.example.smartmeal.ResponseModel
import android.example.smartmeal.login.FragmentLogin
import android.example.smartmeal.login.LoginActivity
import android.example.smartmeal.table.TableModel
import android.example.smartmeal.table.TableViewModel
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException

class FragmentAccount : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_account, container, false)

        view.findViewById<Button>(R.id.btn_logout).setOnClickListener{
            MainActivity.hubConnection.send("Logout")
            var dialog = Dialog(context as Context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_logout)
            dialog.findViewById<Button>(R.id.btn_cflogout).setOnClickListener{
                dialog.dismiss()
            }
            dialog.setOnDismissListener{
                var intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            dialog.show()
        }
        return view
    }


}