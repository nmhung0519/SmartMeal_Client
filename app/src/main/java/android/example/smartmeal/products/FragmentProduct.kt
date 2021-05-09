package android.example.smartmeal.products

import android.content.Context
import android.example.smartmeal.R
import android.example.smartmeal.table.TableViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView

class FragmentProduct : Fragment() {

    private lateinit var productViewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_products, container, false)

        productViewModel = ViewModelProviders.of(this, ProductViewModelFactory(context as Context)).get(ProductViewModel::class.java)
        val productAdapter = ProductAdapter {product -> adapterOnClick(product)}
        view.findViewById<RecyclerView>(R.id.recyclerProductView).adapter = productAdapter

        productViewModel.products.observe(this.requireActivity(), Observer {
            it.let {
                productAdapter.submitList(it as MutableList<ProductModel>)
            }
        })



        return view
    }

    private fun adapterOnClick(product: ProductModel) {

    }
}