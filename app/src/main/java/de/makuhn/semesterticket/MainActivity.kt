package de.makuhn.semesterticket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import de.makuhn.semesterticket.TicketCreator.createTicket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main)  {

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private val ticketViewModel: TicketViewModel by viewModels {
        TicketViewModelFactory((application as TicketApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        ticketViewModel.allTickets.observe(this) {tickets ->
            tickets.let {
                var merge =""
                for (ticket in it) {
                    merge += "${ticket.ticketId}, ${ticket.ticketTitle}"
                }
                textView.text = merge
            }
        }

        val openButton: Button = findViewById(R.id.openButton)
        imageView = findViewById(R.id.imageView)
        openButton.setOnClickListener {
            openPdfPicker()
        }

        val toLoad = File(filesDir, "code1.jpg")
        Glide.with(this).load(toLoad).into(imageView)

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
                val resizedBitmap2 = TicketCropUtils.cropToLeftTicketPage(resizedBitmap)
                Log.d("makuhn", "loading of uri "+ pdfUri.toString())
                imageView?.let {
                    it.setImageBitmap(resizedBitmap2)
                }


                launch {
                    try {
                        val ticket = withContext(Dispatchers.Default) {
                            createTicket(resizedBitmap, this@MainActivity)
                        }
                        //saveBitmaps(ticket)
                        val openButton: Button = findViewById(R.id.openButton)
                        openButton.text = "${ticket.ticketTitle} von ${ticket.passengerName}"
                        val toLoad = File(filesDir, ticket.fullSizeTicketImagePath)
                        Glide.with(this@MainActivity).load(toLoad).into(imageView)

                        ticketViewModel.insert(ticket)

                    } catch (e: Exception) {
                        // Handle exceptions if any
                        e.printStackTrace()
                    }
                }


            }
        }
    }

     fun saveBitmaps(ticket: Ticket) {
//        BitmapStorageHelper.saveBitmapToInternalStorage(this, "code", ticket.aztecCode)
//        BitmapStorageHelper.saveBitmapToInternalStorage(this, "nr", ticket.ticketNumberImagePath)
//        BitmapStorageHelper.saveBitmapToInternalStorage(this, "fsb", ticket.fullSizeTicketImagePath)

    }


    companion object {
        private const val PICK_PDF_REQUEST = 1
    }
}