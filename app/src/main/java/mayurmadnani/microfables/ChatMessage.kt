package mayurmadnani.microfables

data class ChatMessage(
    val text: String,
    val timestamp: Long,
    val isSentByUser: Boolean // To differentiate between sent and received messages
)
