package android.example.smartmeal.products

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.example.smartmeal.Common
import android.example.smartmeal.MainActivity
import android.example.smartmeal.R
import android.example.smartmeal.ResponseModel
import android.example.smartmeal.table.TableModel
import android.os.Bundle
import android.util.Log
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
    private lateinit var dialog: Dialog

    private var _choosedProductId = 0
    var _flagImg = false

    private var waitingUpdate = ArrayList<Int>()

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

        MainActivity.hubConnection.on("Product", {sProductId, sTypeId ->
            try
            {
                waitingUpdate.add(sProductId.toInt())
                MainActivity.hubConnection.send("GetToken")
            }
            catch (ex: Exception) {
                Log.d("Logging", "Exception: ${ex.message}")
            }
        }, String::class.java, String::class.java)

        productViewModel.products.observe(this.requireActivity(), Observer {
            it?.let {
                productAdapter.submitList(it)
            }
        })

        val btnAddProduct = view.findViewById<FloatingActionButton>(R.id.btn_addproduct)
        if (MainActivity.getRole() == 1) {
            btnAddProduct.visibility = View.VISIBLE
            btnAddProduct.setOnClickListener {
                showProductDialog()
            }
        }

        MainActivity.token!!.observe(requireActivity(), Observer {
            if (_flagIO) {
                _flagIO = false
                try {
                    var url = Common.DOMAIN + "/Product/Update"
                    if (_choosedProductId == 0) {
                        url = Common.DOMAIN + "/Product/Insert"
                    }
                    val client = OkHttpClient()
                    var content = "{"
                    if (_choosedProductId > 0) {
                        content += "\"Id\": " + _choosedProductId + ","
                    }
                    content += "\"ProductName\": \"" + dialog.findViewById<TextView>(R.id.txt_productname).text + "\","
                    content += "\"ProductPrice\": " + dialog.findViewById<TextView>(R.id.txt_productprice).text + ","
                    content += "\"Image\": \"" + dialog.findViewById<TextView>(R.id.txt_image).text.toString() + "\","
                    if (dialog.findViewById<CheckBox>(R.id.chk_productactive).isChecked) {
                        content += "\"IsActive\": " + 1
                    }
                    else {
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
                                    if (_choosedProductId > 0) {
                                        MainActivity.msgT.postValue("Cập nhật sản phẩm thành công")
                                    }
                                    else {
                                        MainActivity.msgT.postValue("Thêm sản phẩm thành công")
                                    }
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
            onUpdateProduct(it)
        })

        MainActivity.imgPicker.observe(requireActivity(), Observer {
            if (_flagImg) {
                _flagImg = false
                var a = dialog.findViewById<TextView>(R.id.txt_image)
                dialog.findViewById<TextView>(R.id.txt_image).text = Common.uriImgToBase64(this.requireContext(), it)
                dialog.findViewById<ImageView>(R.id.img_product).setImageURI(it)
            }
        })

        productViewModel.flag = 1
        MainActivity.hubConnection.send("GetToken")
        return view
    }

    private fun onUpdateProduct(tk: String) {
        if (waitingUpdate.count() == 0) return;
        Log.d("Logging", "Fun onUpdateTable")
        val url = Common.DOMAIN + "/Product/Get?id=" + waitingUpdate[0]
        val content = ""
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

                    var product = gson.fromJson(responseModel.content, ProductModel::class.java)
                    productViewModel.updateProduct(product)
                    waitingUpdate.removeAt(0)
                    if (waitingUpdate.count() > 0) onUpdateProduct(tk)
                }
                catch (ex: Exception) {

                }
            }

            override fun onFailure(call: Call?, e: IOException) {

            }
        })
    }

    private fun adapterOnClick(product: ProductModel) {
        showProductDialog(product)
    }

    fun showProductDialog(product: ProductModel? = null) {
        _flagImg = false
        if (product == null) _choosedProductId = 0
        else _choosedProductId = product.Id
        dialog = Dialog(context as Context)
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
            _flagImg = true
            activity?.startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.REQUEST_CODE_SELECT_IMAGE_IN_ALBUM);

        }

        dialog.show()
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }
}