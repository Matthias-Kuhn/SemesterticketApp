package de.makuhn.semesterticket.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.makuhn.semesterticket.R
import de.makuhn.semesterticket.model.Ticket

class TicketListAdapter(private val listener: RecyclerViewEvent) : ListAdapter<Ticket, TicketListAdapter.TicketViewHolder>(TicketsComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        return TicketViewHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class TicketViewHolder(itemView: View, val listener: RecyclerViewEvent) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val ticketTitleView: TextView = itemView.findViewById(R.id.tv_title)
        private val ticketNameView: TextView = itemView.findViewById(R.id.tv_passenger)
        private val ticketValidityView: TextView = itemView.findViewById(R.id.tv_validity)
        private val logo: ImageView = itemView.findViewById(R.id.iv_logo)


        fun bind(ticket: Ticket) {
            ticketTitleView.text = ticket.ticketTitle
            ticketNameView.text = ticket.passengerName
            ticketValidityView.text = ticket.getValidityString()

            if (ticket.ticketType == Ticket.Type.DEUTSCHLANDTICKET){
                logo.setImageResource(R.drawable.dticket)
            } else {
                logo.setImageResource(R.drawable.nrwmobil)
            }

            itemView.setOnClickListener(this)
        }

        companion object {
            fun create(parent: ViewGroup, listener: RecyclerViewEvent): TicketViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return TicketViewHolder(view, listener)
            }
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    class TicketsComparator : DiffUtil.ItemCallback<Ticket>() {
        override fun areContentsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
            return oldItem.ticketId == newItem.ticketId
        }

        override fun areItemsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
            return oldItem === newItem
        }

    }

    interface RecyclerViewEvent {
        fun onItemClick(position: Int)
    }
}