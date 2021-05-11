package android.example.smartmeal.table

import android.content.Context
import android.example.smartmeal.R
import android.example.smartmeal.databinding.FragmentTableBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView


class FragmentTable : Fragment() {
    private var token: String? = null
    private lateinit var tableViewModel: TableViewModel
    private var loadingPanel: RelativeLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        token = arguments?.getString("token")
        tableViewModel = ViewModelProviders.of(this, TableViewModelFactory(context as Context, token)).get(TableViewModel::class.java)
        var view = inflater.inflate(R.layout.fragment_table, container, false)
        loadingPanel = view?.findViewById(R.id.loadingPanel)
        //onLoading()
        val tableAdapter = TableAdapter { view, table -> adapterOnClick(view, table) }
        view?.findViewById<RecyclerView>(R.id.recyclerView)?.adapter = tableAdapter

        tableViewModel.tables.observe(this.requireActivity(), Observer {
            it?.let {
                tableAdapter.submitList(it as MutableList<TableModel>)
            }
        })
        tableAdapter.isDoneFirstLoad.observe(this.requireActivity(), Observer {
            it?.let {
                if (it) loaded()
            }
        })
        return view
    }
    fun onLoading() {
        loadingPanel?.visibility = View.VISIBLE
    }

    fun loaded() {
        loadingPanel?.visibility = View.GONE
    }

    fun adapterOnClick(view: View?, table: TableModel) {
        Toast.makeText(context, table.TableName, Toast.LENGTH_SHORT).show()
        val popupMenu: PopupMenu = PopupMenu(this.activity, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_table, popupMenu.menu)
        popupMenu.menu.findItem(R.id.payTable).setVisible(false)
        popupMenu.show()
    }

    fun updateTable(table: TableModel) {
        tableViewModel.updateTable(table)
    }
}