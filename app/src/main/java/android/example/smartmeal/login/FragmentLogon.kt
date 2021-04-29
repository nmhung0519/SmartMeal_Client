package android.example.smartmeal.login

import android.example.smartmeal.R
import android.example.smartmeal.databinding.FragmentLogonBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

class FragmentLogon : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentLogonBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_logon, container, false)

        return binding.root
    }
}