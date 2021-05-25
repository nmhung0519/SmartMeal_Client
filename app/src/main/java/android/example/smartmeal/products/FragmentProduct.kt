package android.example.smartmeal.products

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.example.smartmeal.Common
import android.example.smartmeal.MainActivity
import android.example.smartmeal.R
import android.example.smartmeal.ResponseModel
import android.example.smartmeal.table.TableModel
import android.example.smartmeal.table.TableViewModel
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

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

        MainActivity.token.observe(this.requireActivity(), Observer {
            if (productViewModel.flag == 1) {
                productViewModel.flag = 0
                try {
                    val url = Common.DOMAIN + "/Product/GetList"
                    val client = OkHttpClient()
                    val content = "-1"
                    val body = RequestBody.create(MediaType.parse("application/json"), content)
                    val request = Request.Builder()
                        .addHeader("Authorization", it)
                        .url(url)
                        .post(body)
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onResponse(call: Call?, response: Response?) {
                            val body = response?.body()?.string()
                            lateinit var responseModel: ResponseModel
                            val gson = Gson()
                            try {
                                responseModel = gson.fromJson(body, ResponseModel::class.java)
                                if (responseModel.status == false) {
                                    //Thông báo lỗi không lấy dữ liệu thành công
                                    return
                                }
                                var listProduct = gson.fromJson(
                                    responseModel.content,
                                    Array<ProductModel>::class.java
                                )
                                productViewModel.setProducts(listProduct.toMutableList())
                            } catch (ex: Exception) {
                                var c = ""
                            }
                        }

                        override fun onFailure(call: Call?, e: IOException) {
                            var a = ""
                            //Thông báo lỗi trong quá trình lấy dữ liệu bàn
                        }
                    })
                } catch (ex: Exception) {
                    //
                }
            }
        })

        productViewModel.flag = 1
        MainActivity.hubConnection.send("GetToken")
        return view
    }

    private fun adapterOnClick(product: ProductModel) {
        var dialog = Dialog(context as Context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_previewproduct)
        dialog.findViewById<Button>(R.id.btn_productcancel).setOnClickListener{
            dialog.dismiss()
        }

        val btnConfirm = dialog.findViewById<Button>(R.id.btn_productok)
        btnConfirm.setOnClickListener {

            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }
}