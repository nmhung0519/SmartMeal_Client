package android.example.smartmeal.payment

import android.example.smartmeal.Common
import android.example.smartmeal.R
import android.example.smartmeal.orderproduct.OrderDetailModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class PaymentAdapter() :
    ListAdapter<PaymentItem, PaymentAdapter.ProductViewHolder>(PaymentDiffCallback) {
    class ProductViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val txtCount: TextView = itemView.findViewById(R.id.txt_pm_count)
        private val txtName: TextView = itemView.findViewById(R.id.txt_pm_name)
        private val txtAmount: TextView = itemView.findViewById(R.id.txt_pm_amount)
        private var curItem: PaymentItem? = null

        /* Bind flower name and image. */
        fun bind(item: PaymentItem) {
            curItem = item
            txtCount.text = item.ProductCount.toString() + " x"
            txtName.text = item.ProductName
            txtAmount.text = Common.toMoneyFormat(item.ProductPrice * item.ProductCount) + "đ"
        }
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.payment_item, parent, false)
        return ProductViewHolder(view)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

}

object PaymentDiffCallback : DiffUtil.ItemCallback<PaymentItem>() {
    override fun areItemsTheSame(oldItem: PaymentItem, newItem: PaymentItem): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: PaymentItem, newItem: PaymentItem): Boolean {
        return false
    }

}