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
<<<<<<< Updated upstream
=======
        token = arguments?.getString("token")
        tableViewModel = ViewModelProviders.of(this, TableViewModelFactory(context as Context, token)).get(TableViewModel::class.java)
        var view = inflater.inflate(R.layout.fragment_table, container, false)
        loadingPanel = view?.findViewById(R.id.loadingPanel)
        //onLoading()
        val tableAdapter = TableAdapter { flower -> adapterOnClick(flower) }
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
>>>>>>> Stashed changes

        val binding: FragmentTableBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_table, container, false)

<<<<<<< Updated upstream
=======
    fun loaded() {
        //loadingPanel?.visibility = View.GONE
    }

    fun adapterOnClick(table: TableModel) {
        Toast.makeText(context, table.TableName, Toast.LENGTH_SHORT).show()
    }
>>>>>>> Stashed changes

        return binding.root
    }
}