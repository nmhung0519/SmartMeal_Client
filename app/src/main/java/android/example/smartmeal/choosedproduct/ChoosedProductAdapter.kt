package android.example.smartmeal.choosedproduct

import android.example.smartmeal.Common
import android.example.smartmeal.R
import android.example.smartmeal.orderproduct.OrderDetailModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ChoosedProductAdapter(private val onClick: (OrderDetailModel) -> Unit) :
    ListAdapter<OrderDetailModel, ChoosedProductAdapter.ProductViewHolder>(CSProductDiffCallback) {
    class ProductViewHolder(itemView: View, val onClick: (OrderDetailModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val txtCount: TextView = itemView.findViewById(R.id.txt_csp_count)
        private val txtName: TextView = itemView.findViewById(R.id.txt_csp_name)
        private val txtAmount: TextView = itemView.findViewById(R.id.txt_csp_amount)
        private val txtDel: TextView = itemView.findViewById(R.id.txt_csp_del)
        private var currentOrder: OrderDetailModel? = null

        init {
            txtDel.setOnClickListener{
                currentOrder?.let {
                    it!!.ProductCount = 0
                    onClick(it)
                }
            }
            itemView.findViewById<LinearLayout>(R.id.ln_csp_info).setOnClickListener {
                currentOrder?.let {
                    onClick(it)
                }
            }
        }

        /* Bind flower name and image. */
        fun bind(order: OrderDetailModel) {
            currentOrder = order
            txtCount.text = order.ProductCount.toString() + " x"
            txtName.text = order.ProductName
            txtAmount.text = Common.toMoneyFormat(order.ProductPrice * order.ProductCount) + "đ"
            txtDel.text = "X"
        }
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.choosedproduct_item, parent, false)
        return ProductViewHolder(view, onClick)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

}

object CSProductDiffCallback : DiffUtil.ItemCallback<OrderDetailModel>() {
    override fun areItemsTheSame(oldItem: OrderDetailModel, newItem: OrderDetailModel): Boolean {
        return (oldItem == newItem && oldItem.Id == newItem.Id)
    }

    override fun areContentsTheSame(oldItem: OrderDetailModel, newItem: OrderDetailModel): Boolean {
        return false
    }

}
