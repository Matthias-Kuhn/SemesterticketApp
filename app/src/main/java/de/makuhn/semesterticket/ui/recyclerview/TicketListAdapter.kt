package de.makuhn.semesterticket.ui.recyclerview

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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

    class TicketViewHolder(itemView: View, val listener: RecyclerViewEvent) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener{
        private val ticketTitleView: TextView = itemView.findViewById(R.id.tv_title)
        private val ticketNameView: TextView = itemView.findViewById(R.id.tv_passenger)
        private val ticketValidityView: TextView = itemView.findViewById(R.id.tv_validity)
        private val logo: ImageView = itemView.findViewById(R.id.iv_logo)
        private val item_bg: ConstraintLayout = itemView.findViewById(R.id.item_bg)
        private val cardView: CardView = itemView.findViewById(R.id.card_view)


        fun bind(ticket: Ticket) {
            ticketTitleView.text = ticket.ticketTitle
            ticketNameView.text = ticket.passengerName
            ticketValidityView.text = ticket.getValidityString()

            if (!ticket.isValid()){
                item_bg.setBackgroundResource(R.drawable.recyclerview_item_invalid_background)
                ticketTitleView.setTypeface(null, Typeface.ITALIC)
                cardView.elevation = 0f
                logo.alpha = 0.4f

            } else {
                item_bg.setBackgroundResource(R.drawable.recyclerview_item_background)
                ticketTitleView.setTypeface(null, Typeface.BOLD)
                cardView.elevation = 8f
                logo.alpha = 1f

            }

            when (ticket.ticketType) {
                Ticket.Type.DEUTSCHLANDTICKET -> {
                    logo.setImageResource(R.drawable.dticket_icon)
                }
                Ticket.Type.SEMESTERTICKET -> {
                    logo.setImageResource(R.drawable.mobilnrw_icon)
                }
                else -> {
                    logo.setImageResource(R.drawable.semticket_icon)
                }
            }

            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
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

        override fun onLongClick(p0: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemLongClick(position)
            }
            return true
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

        fun onItemLongClick(position: Int)
    }
}