package mayurmadnani.microfables

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // Define integer constants for each view type
    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_AI = 2

    // The ViewHolder remains the same, as both layouts have a TextView with the same ID
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
    }

    // This function determines which layout to use based on the message sender
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.isSentByUser) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_AI
        }
    }

    // This function inflates the correct layout file based on the view type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_USER) {
            R.layout.item_chat_user
        } else {
            R.layout.item_chat_ai
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ChatViewHolder(view)
    }

    // The binding logic remains simple
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.messageTextView.text = message.text
    }

    override fun getItemCount() = messages.size
}
