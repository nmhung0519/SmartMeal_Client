package android.example.smartmeal.home

import android.example.smartmeal.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment

class FragmentHome: Fragment() {
    private var loadingPanel: RelativeLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var homeView = inflater.inflate(R.layout.fragment_home, container, false)
        loadingPanel = homeView.findViewById(R.id.loadingPanel)
        loadingPanel?.visibility = View.VISIBLE
        return homeView
    }
}