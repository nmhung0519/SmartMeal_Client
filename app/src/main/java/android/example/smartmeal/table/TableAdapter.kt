package android.example.smartmeal.table

import android.content.Context
import android.example.smartmeal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class TableAdapter : BaseAdapter {
    var tableList = ArrayList<TableModel>()
    var context: Context? = null

    constructor(context: Context, tableList: ArrayList<TableModel>) : super() {
        this.context = context
        this.tableList = tableList
    }

    override fun getCount(): Int {

        return tableList.size
    }

    override fun getItem(position: Int): Any {

        return tableList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(p0: Int, convertView: View?, p2: ViewGroup?): View {

        val table = this.tableList[p0]
        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var tableView = inflator.inflate(R.layout.table_item, null)

        return tableView
    }


}