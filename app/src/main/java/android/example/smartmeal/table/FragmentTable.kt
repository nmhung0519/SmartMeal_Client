package android.example.smartmeal.table

import android.example.smartmeal.R
import android.example.smartmeal.databinding.FragmentTableBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

class FragmentTable : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentTableBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_table, container, false)


        return binding.root
    }
}