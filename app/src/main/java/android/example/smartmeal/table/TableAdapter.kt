package android.example.smartmeal.table

import android.example.smartmeal.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TableAdapter(private val onClick: (TableModel) -> Unit) :
    ListAdapter<TableModel, TableAdapter.TableViewHolder>(TableDiffCallback) {

    class TableViewHolder(itemView: View, val onClick: (TableModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val tableName: TextView = itemView.findViewById(R.id.table_name)
        private var currentTable: TableModel? = null

        init {
            itemView.setOnClickListener {
                currentTable?.let {
                    onClick(it)
                }
            }
        }

        /* Bind flower name and image. */
        fun bind(table: TableModel) {
            currentTable = table

            tableName.text = table.TableName
        }
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_item, parent, false)
        return TableViewHolder(view, onClick)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val table = getItem(position)
        holder.bind(table)
    }
}

object TableDiffCallback : DiffUtil.ItemCallback<TableModel>() {
    override fun areItemsTheSame(oldItem: TableModel, newItem: TableModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TableModel, newItem: TableModel): Boolean {
        return oldItem.Id == newItem.Id
    }

}