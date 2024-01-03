package de.makuhn.semesterticket

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import de.makuhn.semesterticket.TicketCreator.createTicket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main)  {

    private lateinit var imageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val openButton: Button = findViewById(R.id.openButton)
        imageView = findViewById(R.id.imageView)
        openButton.setOnClickListener {
            openPdfPicker()
        }

        Log.d("makuhn_files", this.fileList().size.toString())


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
                val resizedBitmap2 = PdfUtils.getLeftBitmap(resizedBitmap)
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
                val fromBitmap = OcrUtils.removeGrayText(PdfUtils.getNameBitmapDT(resizedBitmap)!!)
                val openButton: Button = findViewById(R.id.openButton)
                OcrUtils.read(fromBitmap!!) {
                    openButton.text = it
                }

                BitmapStorageHelper.saveBitmapToInternalStorage(this, "test", fromBitmap)


                launch {
                    try {
                        val ticket = withContext(Dispatchers.Default) {
                            createTicket(resizedBitmap, this@MainActivity)
                        }
                        //saveBitmaps(ticket)



                        // Process the ticket or perform other actions with the result
                        Log.d("makuhn", ticket.heading)
                    } catch (e: Exception) {
                        // Handle exceptions if any
                        e.printStackTrace()
                    }
                }


            }
        }
    }

     fun saveBitmaps(ticket: Ticket) {
        BitmapStorageHelper.saveBitmapToInternalStorage(this, "code", ticket.aztec_code)
        BitmapStorageHelper.saveBitmapToInternalStorage(this, "nr", ticket.ticketNumber)
        BitmapStorageHelper.saveBitmapToInternalStorage(this, "fsb", ticket.fullSizeBitmap)

    }


    companion object {
        private const val PICK_PDF_REQUEST = 1
    }
}