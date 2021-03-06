package android.example.smartmeal.table

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.example.smartmeal.*
import android.example.smartmeal.order.OrderModel
import android.example.smartmeal.order.OrderTableActivity
import android.example.smartmeal.orderproduct.OrderProductActivity
import android.example.smartmeal.payment.ActivityPayment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException


class FragmentTable : Fragment() {
    private var token: String? = null
    private lateinit var tableViewModel: TableViewModel
    private var loadingPanel: RelativeLayout? = null
    private var waitingUpdate = ArrayList<Int>()
    private var waitingPreviewOrder = false
    var waitingConfirmOrderId = 0
    private var doneFirstLoad = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        MainActivity.token.observe(this.requireActivity(), Observer {
            if (!doneFirstLoad) {
                try {
                    val url = Common.DOMAIN + "/Table/GetAllActive"
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .addHeader("Authorization", it)
                        .url(url)
                        .get()
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onResponse(call: Call?, response: Response?) {
                            val body = response?.body()?.string()
                            lateinit var responseModel: ResponseModel
                            val gson = Gson()
                            try {
                                responseModel = gson.fromJson(body, ResponseModel::class.java)
                                if (responseModel.status == false) {
                                    //Th??ng b??o l???i kh??ng l???y d??? li???u th??nh c??ng
                                    return
                                }
                                var listTable = gson.fromJson(responseModel.content, Array<TableModel>::class.java)
                                tableViewModel.setTable(listTable.toMutableList())
                            }
                            catch (ex: Exception) {
                                var c = ""
                                //Th??ng b??o l???i trong qu?? tr??nh l???y d??? li???u b??n
                            }
                        }

                        override fun onFailure(call: Call?, e: IOException) {
                            var a = ""
                            //Th??ng b??o l???i trong qu?? tr??nh l???y d??? li???u b??n
                        }
                    })
                }
                catch (ex: Exception) {
                    //
                }
            }
            onUpdateTable(it)
        })
        return view
    }

    private fun onUpdateTable(tk: String) {
        if (waitingUpdate.count() == 0) return;
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
        val popupMenu: PopupMenu = PopupMenu(this.activity, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_table, popupMenu.menu)

        //Menu Item Thanh To??n
        popupMenu.menu.findItem(R.id.payment).setVisible(
            (table.Status == 1)
        )

        if (popupMenu.menu.findItem(R.id.payment).isVisible) {
            popupMenu.menu.findItem(R.id.payment).setOnMenuItemClickListener {
                val act = Intent(activity, ActivityPayment::class.java)
                act.putExtra("tableId", table.Id)
                activity?.startActivityForResult(act, Common.REQUEST_CODE_PAYMENT)
                true
            }
        }

        //Menu Item ?????t b??n
        popupMenu.menu.findItem(R.id.orderTable).setVisible(
            (table.Status == 0)
        )
        if (popupMenu.menu.findItem(R.id.orderTable).isVisible) {
            popupMenu.menu.findItem(R.id.orderTable).setOnMenuItemClickListener {
                val mainAct = Intent(activity, OrderTableActivity::class.java)
                mainAct.putExtra("isNew", 1)
                mainAct.putExtra("tableId", table.Id)
                mainAct.putExtra("tableName", table.TableName)
                activity?.startActivityForResult(mainAct, Common.REQUEST_CODE_ORDERTABLE)
                true
            }
        }

        //Menu Item G???i m??n
        popupMenu.menu.findItem(R.id.orderProduct).setVisible(
            (table.Status == 1)
        )

        if (popupMenu.menu.findItem(R.id.orderProduct).isVisible) {
            popupMenu.menu.findItem(R.id.orderProduct).setOnMenuItemClickListener {
                val orderProductActivity = Intent(activity, OrderProductActivity::class.java)
                orderProductActivity.putExtra("tableId", table.Id)
                activity?.startActivityForResult(orderProductActivity, Common.REQUEST_CODE_ORDERPRODUCT)
                true
            }
        }

        //Menu Item Th??ng tin g???i m??n
        popupMenu.menu.findItem(R.id.orderProductInfor).setVisible(
            (table.Status == 1) && false
        )

        //Menu Item Th??ng tin ?????t b??n
        popupMenu.menu.findItem(R.id.orderTableInfor).setVisible(
            (table.Status == 2)
        )
        if (popupMenu.menu.findItem(R.id.orderTableInfor).isVisible) {
            popupMenu.menu.findItem(R.id.orderTableInfor).setOnMenuItemClickListener {
                val mainAct = Intent(activity, OrderTableActivity::class.java)
                mainAct.putExtra("isNew", 0)
                mainAct.putExtra("tableId", table.Id)
                mainAct.putExtra("tableName", table.TableName)
                activity?.startActivityForResult(mainAct, Common.REQUEST_CODE_ORDERTABLEINFOR)
                true
            }
        }

        //Menu Item C?? kh??ch
        popupMenu.menu.findItem(R.id.fillTable).setVisible(
            (table.Status == 0 || table.Status == 2)
        )

        if (popupMenu.menu.findItem(R.id.fillTable).isVisible) {
            popupMenu.menu.findItem(R.id.fillTable).setOnMenuItemClickListener {
                if (table.Status == 2) {
                    waitingPreviewOrder = true
                    val dialog = Dialog(context as Context)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.dialog_filltable)
                    dialog.findViewById<TextView>(R.id.lbl_cftablename).setText(table.TableName)
                    var btnConfirm = dialog.findViewById<Button>(R.id.btn_cf2ok)
                    btnConfirm.isEnabled = false
                    btnConfirm.setOnClickListener {
                        //
                        dialog.dismiss()
                    }
                    dialog.findViewById<Button>(R.id.btn_cf2cancel).setOnClickListener {
                        dialog.dismiss()
                    }
                    //MainActivity.hubConnection.send("GetToken")
                    MainActivity.token.observe(requireActivity(), Observer {
                        if (waitingPreviewOrder) {
                            getDataOrder(dialog, table.Id, it)
                        }

                        if (waitingConfirmOrderId > 0) {
                            var orderId = waitingConfirmOrderId
                            waitingConfirmOrderId = 0;
                            val url = Common.DOMAIN + "/Order/Confirm"
                            val content = "{ \"Id\": ${orderId}}"
                            val client = OkHttpClient()
                            val body = RequestBody.create(
                                MediaType.parse("application/json"), content
                            )
                            val request = Request.Builder()
                                .url(url)
                                .addHeader("Authorization", it)
                                .post(body)
                                .build()
                            client.newCall(request).enqueue(object : Callback {
                                override fun onResponse(call: Call?, response: Response?) {
                                    val body = response?.body()?.string()
                                    val gson = Gson()
                                    try {
                                        var responseModel =
                                            gson.fromJson(body, ResponseModel::class.java)
                                        if (responseModel.status == true) {
                                            dialog.dismiss()
                                        } else {
                                            btnConfirm.isEnabled = true
                                        }
                                    } catch (ex: Exception) {
                                    }
                                }

                                override fun onFailure(call: Call?, e: IOException) {
                                    btnConfirm.isEnabled = true
                                }
                            })
                        }
                    })
                    dialog.show()
                }
                else {
                    var flagFill = false
                    val dialog = Dialog(context as Context)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.dialog_filltablenotorder)
                    dialog.findViewById<TextView>(R.id.lbl_cf2tablename).setText(table.TableName)
                    val txtCustomer = dialog.findViewById<EditText>(R.id.txt_cf2customer)
                    val txtPhone = dialog.findViewById<EditText>(R.id.txt_cf2phone)
                    val btnConfirm = dialog.findViewById<Button>(R.id.btn_cf2ok)
                    val btnCancel = dialog.findViewById<Button>(R.id.btn_cf2cancel)
                    btnConfirm.setOnClickListener{
                        txtCustomer.isEnabled = false
                        txtPhone.isEnabled = false
                        btnConfirm.isEnabled = false
                        btnCancel.isEnabled = false
                        flagFill = true
                        MainActivity.hubConnection.send("GetToken")
                    }

                    MainActivity.token.observe(this.requireActivity(), Observer {
                        if (flagFill) {
                            flagFill = false
                            val url = Common.DOMAIN + "/Table/Fill"
                            val content = "{ " +
                                    "\"TableId\": ${table.Id}," +
                                    "\"CustomerName\": \"${txtCustomer.text}\"," +
                                    "\"CustomerContact\": \"${txtPhone.text}\"" +
                                    "}"
                            val client = OkHttpClient()
                            val body = RequestBody.create(
                                MediaType.parse("application/json"), content
                            )
                            val request = Request.Builder()
                                .url(url)
                                .addHeader("Authorization", it)
                                .post(body)
                                .build()
                            client.newCall(request).enqueue(object : Callback {
                                override fun onResponse(call: Call?, response: Response?) {
                                    val body = response?.body()?.string()
                                    val gson = Gson()
                                    try {
                                        var responseModel =
                                            gson.fromJson(body, ResponseModel::class.java)
                                        if (responseModel.status == true) {
                                            dialog.dismiss()
                                            return
                                        }

                                    } catch (ex: Exception) {
                                    }
                                    txtCustomer.isEnabled = true
                                    txtPhone.isEnabled = true
                                    btnConfirm.isEnabled = true
                                    btnCancel.isEnabled = true
                                }

                                override fun onFailure(call: Call?, e: IOException) {
                                    txtCustomer.isEnabled = true
                                    txtPhone.isEnabled = true
                                    btnConfirm.isEnabled = true
                                    btnCancel.isEnabled = true
                                }
                            })
                        }
                    })
                    btnCancel.setOnClickListener{
                        dialog.dismiss()
                    }
                    dialog.show()
                }
                true
            }
        }
        popupMenu.show()
    }

    private fun getDataOrder(dialog: Dialog, tableId: Int, token: String) {
        waitingPreviewOrder = false
        val url = Common.DOMAIN + "/Order/GetPreWithTable"
        val content = "{ \"TableId\": ${tableId}}"
        val client = OkHttpClient()
        val body = RequestBody.create(
            MediaType.parse("application/json"), content
        )
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", token)
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                val gson = Gson()
                try {
                    var responseModel = gson.fromJson(body, ResponseModel::class.java)
                    if (responseModel.status == true) {
                        var order = gson.fromJson(responseModel.content, OrderModel::class.java)
                        dialog.findViewById<TextView>(R.id.txt_cfcustomer).setText(order.CustomerName)
                        dialog.findViewById<TextView>(R.id.txt_cfphone).setText(order.CustomerContact)
                        dialog.findViewById<TextView>(R.id.txt_cftime).setText(Common.toViewTime(order.StartTime))
                        var btnConfirm = dialog.findViewById<Button>(R.id.btn_cf2ok)
                        btnConfirm.setOnClickListener{
                            waitingConfirmOrderId = order.Id
                            MainActivity.hubConnection.send("GetToken")
                        }
                        btnConfirm.isEnabled = true
                    }
                } catch (ex: Exception) {
                    Log.d("Logging", "L???i khi l???y d??? li???u preview Order. Chi ti???t: " + ex.message)
                }
            }

            override fun onFailure(call: Call?, e: IOException) {
            }
        })
    }
}