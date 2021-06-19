package android.example.smartmeal.home

import android.example.smartmeal.MainActivity
import android.example.smartmeal.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentHome: Fragment() {
    private var loadingPanel: RelativeLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var homeView = inflater.inflate(R.layout.fragment_home, container, false)
        homeView.findViewById<TextView>(R.id.txt_user_fullname).text = MainActivity.fullname
        return homeView
    }
}