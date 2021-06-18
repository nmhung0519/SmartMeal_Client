package android.example.smartmeal.products

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.example.smartmeal.Common
import android.example.smartmeal.MainActivity
import android.example.smartmeal.R
import android.example.smartmeal.ResponseModel
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException

class FragmentProduct : Fragment() {

    private lateinit var productViewModel: ProductViewModel
    private var _flagIO = false

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
                                    MainActivity.msgT.postValue(responseModel.content)
                                    return
                                }
                                var listProduct = gson.fromJson(
                                    responseModel.content,
                                    Array<ProductModel>::class.java
                                )
                                productViewModel.setProducts(listProduct.toMutableList())
                            } catch (ex: Exception) {
                                MainActivity.msgT.postValue(ex.message.toString())
                            }
                        }

                        override fun onFailure(call: Call?, e: IOException) {
                            var a = ""
                            MainActivity.msgT.postValue("Đã xảy ra lỗi. Hảy thử lại.")
                        }
                    })
                } catch (ex: Exception) {
                    //
                }
            }
        })

        val btnAddProduct = view.findViewById<FloatingActionButton>(R.id.btn_addproduct)
        if (MainActivity.getRole() == 1) {
            btnAddProduct.visibility = View.VISIBLE
            btnAddProduct.setOnClickListener {
                showProductDialog()
            }
        }

        productViewModel.flag = 1
        MainActivity.hubConnection.send("GetToken")
        return view
    }

    private fun adapterOnClick(product: ProductModel) {
        showProductDialog(product)
    }

    fun showProductDialog(product: ProductModel? = null) {
        var dialog = Dialog(context as Context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_previewproduct)
        dialog.findViewById<Button>(R.id.btn_productcancel).setOnClickListener{
            dialog.dismiss()
        }

        val btnConfirm = dialog.findViewById<Button>(R.id.btn_productconfirm)
        btnConfirm.setOnClickListener {
            try {
                _flagIO = true
                MainActivity.hubConnection.send("GetToken")
            }
            catch (ex: Exception) {
                MainActivity.msgT.postValue(ex.message.toString())
            }

        }

        MainActivity.token!!.observe(requireActivity(), Observer {
            if (_flagIO) {
                try {
                    var url = Common.DOMAIN + "/Product/Update"
                    if (product == null) {
                        url = Common.DOMAIN + "/Product/Insert"
                    }
                    val client = OkHttpClient()
                    var content = "{"
                    if (product != null) {
                        content += "\"ProductId\": " + product!!.Id + ","
                    }
                    content += "\"ProductName\": \"" + dialog.findViewById<TextView>(R.id.txt_productname).text + "\","
                    content += "\"ProductName\": " + dialog.findViewById<TextView>(R.id.txt_productprice).text + ","
                    if (dialog.findViewById<CheckBox>(R.id.chk_productactive).isChecked) {
                        content += "\"IsActive\": " + 1
                    } else {
                        content += "\"IsActive\": " + 0
                    }
                    content += "}"
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
                                    MainActivity.msgT.postValue(responseModel.content)
                                    return
                                } else {
                                    dialog.dismiss()
                                }
                            } catch (ex: Exception) {
                                MainActivity.msgT.postValue(ex.message.toString())
                            }
                        }

                        override fun onFailure(call: Call?, e: IOException) {
                            var a = ""
                            MainActivity.msgT.postValue("Đã xảy ra lỗi. Hảy thử lại.")
                        }
                    })
                } catch (ex: Exception) {
                    MainActivity.msgT.postValue(ex.message.toString())
                }
            }
        })

        if (product != null) {
            val txtProductName = dialog.findViewById<EditText>(R.id.txt_productname)
            val txtProductPrice = dialog.findViewById<EditText>(R.id.txt_productprice)
            val btnConfirm = dialog.findViewById<Button>(R.id.btn_productconfirm)
            val btnCancel = dialog.findViewById<Button>(R.id.btn_productcancel)
            val checkBox = dialog.findViewById<CheckBox>(R.id.chk_productactive)
            if (MainActivity.getRole() != 1) {
                txtProductName.isEnabled = false
                txtProductPrice.isEnabled = false
                btnConfirm.visibility = View.GONE
            }
            txtProductName.setText(product.ProductName)
            txtProductPrice.setText(product.ProductPrice.toString())
            if (product.IsActive == 1) {
                checkBox.isChecked = true
            }

            if (product.Image != null && product.Image.length > 0) Picasso.get().load(product.Image).into(dialog.findViewById<ImageView>(R.id.img_product))
            btnConfirm.setText("Cập nhật")
            btnCancel.setText("Đóng")
        }

        val imgProduct = dialog.findViewById<ImageView>(R.id.img_product)
        imgProduct.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.REQUEST_CODE_SELECT_IMAGE_IN_ALBUM);
        }

        dialog.show()
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }
}