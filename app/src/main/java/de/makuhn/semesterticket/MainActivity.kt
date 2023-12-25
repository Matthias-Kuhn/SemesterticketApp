package de.makuhn.semesterticket

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val openButton: Button = findViewById(R.id.openButton)
        imageView = findViewById(R.id.imageView)
        openButton.setOnClickListener {
            openPdfPicker()
        }
    }

    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        startActivityForResult(Intent.createChooser(intent,"open PDF") , PICK_PDF_REQUEST)

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { pdfUri ->

                // Call the renderFirstPage method to get the resized Bitmap
                val resizedBitmap = PdfUtils.renderFirstPage(this, pdfUri)
                val resizedBitmap2 = PdfUtils.cropBitmap(resizedBitmap, 1408, 512, 2400, 876*2)
                Log.d("makuhn", "loading of uri "+ pdfUri.toString())
                imageView?.let {
                    if (resizedBitmap2 != null) {
                        // Display the resized Bitmap in the ImageView
                        it.setImageBitmap(resizedBitmap2)
                    } else {
                        // Handle the case where rendering failed or the Bitmap is null
                        Toast.makeText(this, "Failed to render PDF or invalid Bitmap", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    // Handle the case where imageView is null
                    Toast.makeText(this, "ImageView is not initialized", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val PICK_PDF_REQUEST = 1
    }
}