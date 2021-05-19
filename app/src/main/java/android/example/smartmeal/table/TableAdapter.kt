package android.example.smartmeal.table

import android.example.smartmeal.R
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

class TableAdapter(private val onClick: (View?, TableModel) -> Unit) :
    ListAdapter<TableModel, TableAdapter.TableViewHolder>(TableDiffCallback) {
    private var isLastItem = false
    private var _isDoneFirstLoad = MutableLiveData<Boolean>()
    val isDoneFirstLoad: LiveData<Boolean>
        get() = _isDoneFirstLoad
    init {
        _isDoneFirstLoad.value = false
    }
    class TableViewHolder(itemView: View, val onClick: (View?, TableModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val tableName: TextView = itemView.findViewById(R.id.table_name)
        private val tableImg: ImageView = itemView.findViewById(R.id.table_img)
        private var currentTable: TableModel? = null

        init {
            itemView.setOnClickListener {
                currentTable?.let {
                    onClick(itemView, it)
                }
            }
        }

        /* Bind flower name and image. */
        fun bind(table: TableModel) {
            currentTable = table

            tableName.text = table.TableName
            try {
                tableImg.setBackgroundColor(Color.parseColor(table.color))
            }
            catch (ex: Exception) {
                var a = ""
            }

        }
    }

    override fun onViewAttachedToWindow(holder: TableViewHolder) {
        super.onViewAttachedToWindow(holder)
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_item, parent, false)
        return TableViewHolder(view, onClick).apply {
            if (isLastItem) _isDoneFirstLoad.postValue(true)
        }
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        if (position == itemCount - 1) isLastItem = true
        val table = getItem(position)
        holder.bind(table)
    }

    override fun onFailedToRecycleView(holder: TableViewHolder): Boolean {
        _isDoneFirstLoad.postValue(true)
        return super.onFailedToRecycleView(holder)
    }

}

object TableDiffCallback : DiffUtil.ItemCallback<TableModel>() {
    override fun areItemsTheSame(oldItem: TableModel, newItem: TableModel): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: TableModel, newItem: TableModel): Boolean {
        return oldItem.Status == newItem.Status
    }

}