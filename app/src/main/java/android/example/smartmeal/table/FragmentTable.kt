package android.example.smartmeal.table

import android.content.Context
import android.content.Intent
import android.example.smartmeal.*
import android.example.smartmeal.databinding.FragmentTableBinding
import android.example.smartmeal.order.OrderTableActivity
import android.os.Bundle
import android.util.Log
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
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException


class FragmentTable : Fragment() {
    private var token: String? = null
    private lateinit var tableViewModel: TableViewModel
    private var loadingPanel: RelativeLayout? = null
    private var waitingUpdate = ArrayList<Int>()
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
        MainActivity.hubConnection.on("Table", {sTableId, sTypeId ->
            Log.d("Logging", "SignarlR Table Method: ${sTableId} - ${sTypeId}")
            try
            {
                waitingUpdate.add(sTableId.toInt())
                MainActivity.hubConnection.send("GetToken")
            }
            catch (ex: Exception) {
                Log.d("Logging", "Exception: ${ex.message}")
            }
        }, String::class.java, String::class.java)

        MainActivity.hubConnection.on("Token", {sToken ->
            onUpdateTable(sToken)
        }, String::class.java)

        return view
    }

    private fun onUpdateTable(tk: String) {
        Log.d("Logging", "Fun onUpdateTable")
        val url = Common.DOMAIN + "/Table/GetById"
        val content = "{ \"Id\": ${waitingUpdate[0]}}"
        val client = OkHttpClient()
        val body = RequestBody.create(
            MediaType.parse("application/json"), content)
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", tk)
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                var responseModel: ResponseModel?
                val gson = Gson()
                try {
                    responseModel = gson.fromJson(body, ResponseModel::class.java)
                    if (responseModel.status == false) {
                        return
                    }

                    var table = gson.fromJson(responseModel.content, TableModel::class.java)
                    tableViewModel.updateTable(table)
                    waitingUpdate.removeAt(0)
                    if (waitingUpdate.count() > 0) onUpdateTable(tk)
                }
                catch (ex: Exception) {

                }
            }

            override fun onFailure(call: Call?, e: IOException) {

            }
        })
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

        //setup by role
        popupMenu.menu.findItem(R.id.orderTable).setOnMenuItemClickListener {
            val mainAct = Intent(activity, OrderTableActivity::class.java)
            mainAct.putExtra("tableId", table.Id)
            mainAct.putExtra("tableName", table.TableName)
            activity?.startActivityForResult(mainAct, Common.REQUEST_CODE_ORDERTABLE)
            true
        }

        //setup by status

        popupMenu.show()
    }

    fun updateTable(table: TableModel) {
        tableViewModel.updateTable(table)
    }
}