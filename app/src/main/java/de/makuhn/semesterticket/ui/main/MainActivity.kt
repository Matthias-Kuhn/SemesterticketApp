package de.makuhn.semesterticket.ui.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.makuhn.semesterticket.R
import de.makuhn.semesterticket.TicketApplication
import de.makuhn.semesterticket.model.Ticket
import de.makuhn.semesterticket.ui.recyclerview.TicketListAdapter
import de.makuhn.semesterticket.ui.viewmodel.TicketViewModel
import de.makuhn.semesterticket.ui.viewmodel.TicketViewModelFactory
import de.makuhn.semesterticket.utils.PdfUtils
import de.makuhn.semesterticket.utils.TicketCreator.createTicket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main), TicketListAdapter.RecyclerViewEvent {

    private val ticketViewModel: TicketViewModel by viewModels {
        TicketViewModelFactory((application as TicketApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = TicketListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)




        ticketViewModel.allTickets.observe(this) {tickets ->
            tickets?.let { adapter.submitList(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
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



                launch {
                    try {
                        val ticket = withContext(Dispatchers.Default) {
                            createTicket(resizedBitmap, this@MainActivity)
                        }

                        ticketViewModel.insert(ticket)

                    } catch (e: Exception) {
                        // Handle exceptions if any
                        e.printStackTrace()
                    }
                }


            }
        }
    }

    private fun showDialog(ticket: Ticket) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.ticket_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.80).toInt()
        dialog.window?.setLayout(width, height)


        val title = dialog.findViewById<TextView>(R.id.tv_ticketTitle)
        val subtitle = dialog.findViewById<TextView>(R.id.tv_subtitle)
        val name = dialog.findViewById<TextView>(R.id.tv_passengername)
        val validity = dialog.findViewById<TextView>(R.id.tv_validity)
        val code = dialog.findViewById<ImageView>(R.id.iv_code)
        val number = dialog.findViewById<ImageView>(R.id.iv_ticketNumber)
        val logo = dialog.findViewById<ImageView>(R.id.iv_logo)

        if (ticket.ticketType == Ticket.Type.DEUTSCHLANDTICKET) {
            title.text = "D-Ticket"
            logo.setImageResource(R.drawable.dticket)
        } else {
            title.text = ticket.ticketTitle
            logo.setImageResource(R.drawable.nrwmobil)
        }

        subtitle.text = ticket.ticketSubtitle
        name.text = ticket.passengerName
        validity.text = ticket.getValidityString()

        val codeImage = File(filesDir, ticket.aztecCodeImagePath)
        Glide.with(this).load(codeImage).into(code)

        val numberImage = File(filesDir, ticket.ticketNumberImagePath)
        Glide.with(this).load(numberImage).into(number)

        dialog.show()

    }


    companion object {
        private const val PICK_PDF_REQUEST = 1
    }

    override fun onItemClick(position: Int) {
        ticketViewModel.allTickets.value?.get(position)?.let { showDialog(it) }
    }
}