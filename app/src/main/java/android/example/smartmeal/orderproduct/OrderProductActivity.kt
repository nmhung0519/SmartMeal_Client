package android.example.smartmeal.orderproduct

import android.app.Dialog
import android.content.Intent
import android.example.smartmeal.Common
import android.example.smartmeal.MainActivity
import android.example.smartmeal.R
import android.example.smartmeal.ResponseModel
import android.example.smartmeal.choosedproduct.ActivityChoosedProduct
import android.example.smartmeal.databinding.ActivityOrderproductBinding
import android.example.smartmeal.products.ProductAdapter
import android.example.smartmeal.products.ProductModel
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException
import java.util.*


class OrderProductActivity: AppCompatActivity() {
    private lateinit var binding: ActivityOrderproductBinding
    companion object {
        lateinit var viewModel: OrderProductViewModel
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val tableId = intent.getIntExtra("tableId", 0)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_orderproduct)
        binding.viewModel = ViewModelProviders.of(this, OrderProductViewModelFactory(tableId))
            .get(OrderProductViewModel::class.java)
        viewModel = binding.viewModel!!
        binding.viewModel!!.flagLoad.observe(this, Observer {
            if (it) {
                //binding.framelayoutOrderproduct.visibility = View.GONE
                //binding.progressBar.visibility = View.VISIBLE
            }
            else {
                //binding.framelayoutOrderproduct.visibility = View.VISIBLE
                //binding.progressBar.visibility = View.GONE
            }
        })

        val productAdapter = ProductAdapter {product -> adapterOnClick(product)}
        binding.recyclerProductViewOrderproduct.adapter = productAdapter
        binding.viewModel!!.list.observe(this, Observer {
            it.let {
                productAdapter.submitList(it as MutableList<ProductModel>)
            }
        })

        binding.btnPrePoproduct.isVisible = false
        binding.viewModel!!.listSelected.observe(this, Observer {
            it.let {
                if (it == null || it!!.count() == 0) {
                    binding.btnPrePoproduct.isVisible = false
               }
                else {
                    binding.txtPpopCount.text = binding.viewModel!!.getTotalCount().toString() + " Món"
                    binding.txtPpopAmount.text = Common.toMoneyFormat(binding.viewModel!!.getTotalAmount()) + "đ"
                    binding.btnPrePoproduct.isVisible = true
                }
            }
        })

        binding.btnPrePoproduct.setOnClickListener{
            var intent = Intent(this, ActivityChoosedProduct::class.java)
            this.startActivity(intent)
        }

        binding.txtSearchproduct.doAfterTextChanged {
            binding.viewModel!!.flagLoad.postValue(true)
            MainActivity.hubConnection.send("GetToken")
        }

        MainActivity.token.observe(this, Observer {
            if (binding.viewModel!!.flagLoad.value == false) {
                binding.viewModel!!.flagLoad.postValue(true)
                val url = Common.DOMAIN + "/Product/SearchByName"
                val client = OkHttpClient()
                val content = "{" +
                        "\"ProductName\": \"${binding.txtSearchproduct.text}\"" +
                        "}"
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
                            binding.viewModel!!.setList(listProduct.toMutableList())
                        } catch (ex: Exception) {
                            var c = ""
                        }
                    }

                    override fun onFailure(call: Call?, e: IOException) {
                        var a = ""
                        //Thông báo lỗi trong quá trình lấy dữ liệu bàn
                    }
                })
            }
        })
        binding.viewModel!!.flagLoad!!.postValue(true)
        MainActivity.hubConnection.send("GetToken")
    }

    private fun adapterOnClick(product: ProductModel) {
        try {
            var count = 1
            var dialog = Dialog(this, R.style.DialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_chooseproduct) //set lại content
            var txtPOCount = dialog.findViewById<TextView>(R.id.txt_pocount)
            var btnDecrease = dialog.findViewById<Button>(R.id.btn_decrease)
            var btnIncrease = dialog.findViewById<Button>(R.id.btn_increase)
            var btnAdd = dialog.findViewById<Button>(R.id.btn_ordproduct)
            if (product.Image != null && product.Image != "") Picasso.get().load(product.Image).into(dialog.findViewById<ImageView>(R.id.img_productimage))
            dialog.findViewById<TextView>(R.id.txt_POName).setText(product.ProductName)
            dialog.findViewById<TextView>(R.id.txt_POPrice).setText(Common.toMoneyFormat(product.ProductPrice) + "đ")
            txtPOCount.setText("1")
            btnAdd.setText("Thêm • " + Common.toMoneyFormat(product.ProductPrice) + "đ")
            btnDecrease.isEnabled = false
            btnDecrease.setOnClickListener{
                count--
                if (count == 1) {
                    btnDecrease.isEnabled = false
                }
                txtPOCount.text = count.toString()
                btnAdd.setText("Thêm • " + Common.toMoneyFormat(product.ProductPrice * count) + "đ")
            }
            btnIncrease.setOnClickListener{
                if (count < 99) {
                    count++
                    btnDecrease.isEnabled = true
                    txtPOCount.text = count.toString()
                    btnAdd.setText("Thêm • " + Common.toMoneyFormat(product.ProductPrice * count) + "đ")
                }
            }
            btnAdd.setOnClickListener{
                binding.viewModel!!.addSelectedProduct(product.Id, count)
                dialog.dismiss()
            }
            var window = dialog.getWindow()
            var wlp = window?.attributes
            wlp?.gravity = Gravity.BOTTOM
            wlp?.windowAnimations = R.style.DialogAnimation
            window?.attributes = wlp
            dialog.show()
            dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        }
        catch (ex: Exception) {
            var a = ex.message
            var b = ""
        }
    }
}