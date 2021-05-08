package android.example.smartmeal.table

import android.content.Context
import android.example.smartmeal.R
import android.example.smartmeal.databinding.FragmentTableBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView


class FragmentTable : Fragment() {

    private val tableViewModel by viewModels<TableViewModel> {
        TableViewModelFactory(context as Context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_table, container, false)

        val tableAdapter = TableAdapter { flower -> adapterOnClick(flower) }

        view?.findViewById<RecyclerView>(R.id.recyclerView)?.adapter = tableAdapter

        tableViewModel.tables.observe(this.requireActivity(), Observer {
            it?.let {
                tableAdapter.submitList(it as MutableList<TableModel>)
            }
        })
        return view;
    }

    fun adapterOnClick(table: TableModel) {
        Toast.makeText(context, table.tableName, Toast.LENGTH_SHORT).show()
    }

    fun InsertTable() {
        tableViewModel.inserTable(TableModel(2, "Bàn 2"))
    }
}