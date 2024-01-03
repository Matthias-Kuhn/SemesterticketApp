package de.makuhn.semesterticket

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TicketViewModel(private val repository: TicketRepository) : ViewModel() {
    val allTickets : LiveData<List<Ticket>> = repository.allTickets.asLiveData()

    fun insert(ticket: Ticket) = viewModelScope.launch {
        repository.insert(ticket)
    }
}

class TicketViewModelFactory(private val repository: TicketRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TicketViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}