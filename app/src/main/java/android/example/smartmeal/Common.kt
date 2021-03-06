package android.example.smartmeal

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream


class Common {
    companion object {
        //val DOMAIN: String = "http://168.168.0.106"
        //val DOMAIN: String = "http://192.168.1.190:8089" //Debug
        val DOMAIN: String = "http://192.168.1.190"
        val COLOR_EMPTY_TABLE = "#4EC33A"
        val COLOR_FILLED_TABLE = "#C92C1E"
        val COLOR_BOOKED_TABLE = "#DAA520"
        val COLOR_UNKNOWN_TABLE = "#FFFFFF00"
        val DEFAULT_HOUR = 7
        val REQUEST_CODE_ORDERTABLE = 1
        val REQUEST_CODE_ORDERTABLEINFOR = 2
        val REQUEST_CODE_ORDERPRODUCT = 3
        val REQUEST_CODE_CFORDERPRODUCT = 4
        val REQUEST_CODE_PAYMENT = 5
        val REQUEST_CODE_SELECT_IMAGE_IN_ALBUM = 6

        fun toViewDate(datetime: String?): String { 
            if (datetime == null || datetime == "") return ""
            var tmp = datetime.split("T")
            var dateVal = tmp[0].split("-")
            return "${dateVal[2]}/${dateVal[1]}/${dateVal[0]}"
        }

        fun toViewTime(datetime: String?): String {
            if (datetime == null || datetime == "") return ""
            var tmp = datetime.split("T")
            var dateVal = tmp[0].split("-")
            return "${tmp[1]} ${dateVal[2]}/${dateVal[1]}/${dateVal[0]}"
        }

        fun getHour(datetime: String?): String {
            if (datetime == null || datetime == "") return ""
            return datetime.split("T")[1].split(":")[0]
        }

        fun getMinute(datetime: String?): String {
            if (datetime == null || datetime == "") return ""
            return datetime.split("T")[1].split(":")[1]
        }

        fun toMoneyFormat(amount: Int):String {
            var tmp = amount
            var output = ""
            while (tmp > 1000) {
                var a = "" + tmp % 1000
                while (a.length < 3) a = "0" + a
                output = "," + a + output
                tmp = tmp / 1000
            }
            output = tmp.toString() + output
            return output
        }

        fun uriImgToBase64(context: Context, uri: Uri): String {
            var bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP);
        }

    }


}