package android.example.smartmeal.table

import android.content.Context
import android.example.smartmeal.R
import android.example.smartmeal.databinding.FragmentTableBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

class FragmentTable : Fragment() {

    var adapter: TableAdapter? = null
    var tableList = ArrayList<TableModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentTableBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_table, container, false)
        tableList.add(TableModel(1, "Ban 1"))
        tableList.add(TableModel(2, "Ban 2"))

        adapter = TableAdapter(context as Context, tableList)

        binding.gridViewTable.adapter = adapter


        return binding.root
    }
}