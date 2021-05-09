package android.example.smartmeal.products

import android.example.smartmeal.R
import android.example.smartmeal.table.TableModel
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
import com.squareup.picasso.Picasso

class ProductAdapter(private val onClick: (ProductModel) -> Unit) :
    ListAdapter<ProductModel, ProductAdapter.ProductViewHolder>(TableDiffCallback) {
    private var isLastItem = false
    private var _isDoneFirstLoad = MutableLiveData<Boolean>()
    val isDoneFirstLoad: LiveData<Boolean>
        get() = _isDoneFirstLoad
    init {
        _isDoneFirstLoad.value = false
    }
    class ProductViewHolder(itemView: View, val onClick: (ProductModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productImage: ImageView = itemView.findViewById(R.id.product_img)
        private var currentProduct: ProductModel? = null

        init {
            itemView.setOnClickListener {
                currentProduct?.let {
                    onClick(it)
                }
            }
        }

        /* Bind flower name and image. */
        fun bind(product: ProductModel) {
            currentProduct = product
            productName.text = product.ProductName
            Picasso.get().load("https://maytinhvui.com/wp-content/uploads/2020/11/hinh-nen-may-tinh-4k-game-pubg2-min-1920x1080.png?fbclid=IwAR30d5IWO1StErHNtvgBlfQJlh47vogYeNrYMZSObEGYkEwnP6NPcb6SwfE").into(productImage)

        }
    }

    override fun onViewAttachedToWindow(holder: ProductViewHolder) {
        super.onViewAttachedToWindow(holder)
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view, onClick).apply {
            if (isLastItem) _isDoneFirstLoad.postValue(true)
        }
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        if (position == itemCount - 1) isLastItem = true
        val product = getItem(position)
        holder.bind(product)
    }

    override fun onFailedToRecycleView(holder: ProductViewHolder): Boolean {
        _isDoneFirstLoad.postValue(true)
        return super.onFailedToRecycleView(holder)
    }

}

object TableDiffCallback : DiffUtil.ItemCallback<ProductModel>() {
    override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
        return oldItem.Id == newItem.Id
    }

}