package io.mitter.recipes

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_photo.*
import java.io.*
import android.provider.DocumentsContract
import android.widget.Toast
import io.mitter.android.Mitter
import io.mitter.android.error.model.base.ApiError

class PhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        val imageUri = intent.getParcelableExtra<Uri>(Constants.PREVIEW_IMAGE)
        val channelId = intent.getStringExtra(Constants.CHANNEL_ID)

        Glide.with(this)
            .load(imageUri)
            .into(previewImage)

        val mitter = (application as App).mitter
        val messaging = mitter.Messaging()

        val id = DocumentsContract.getDocumentId(imageUri)
        val inputStream = contentResolver.openInputStream(imageUri)
        val file = File("${cacheDir.absolutePath}/$id.jpg")
        writeFile(inputStream, file)

        sendButton?.setOnClickListener {
            messaging.sendImageMessage(
                channelId = channelId,
                caption = messageEditText.text.toString(),
                file = file,
                onValueUpdatedCallback = object : Mitter.OnValueUpdatedCallback {
                    override fun onError(apiError: ApiError) {
                        Toast.makeText(this@PhotoActivity, "Please try again!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onSuccess() {
                        finish()
                    }
                }
            )

            messageEditText.text.clear()
        }
    }

    fun writeFile(inStream: InputStream, file: File) {
        var out: OutputStream? = null
        try {
            out = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            len = inStream.read(buf)
            while (len > 0) {
                out.write(buf, 0, len)
                len = inStream.read(buf)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (out != null) {
                    out.close()
                }
                inStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}
