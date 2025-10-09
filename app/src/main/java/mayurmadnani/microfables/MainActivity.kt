package mayurmadnani.microfables

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import mayurmadnani.microfables.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<ChatMessage>()
    private val ollamaService = ApiClient.instance
    private var selectedModel: String? = null

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "Activity created.")

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        fetchAndSetupModels()

        binding.sendButton.setOnClickListener {
            val messageText = binding.messageEditText.text.toString().trim()
            if (messageText.isNotEmpty() && selectedModel != null) {
                handleUserMessage(messageText)
            }
        }
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView.")
        chatAdapter = ChatAdapter(messageList)
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = chatAdapter
        }
    }

    private fun fetchAndSetupModels() {
        Log.d(TAG, "Fetching models from server...")
        setUiEnabled(false)
        supportActionBar?.subtitle = "Fetching Models..." // Update subtitle

        lifecycleScope.launch {
            try {
                val response = ollamaService.getModels()
                val modelNames = response.models.map { it.name }
                Log.i(TAG, "Models fetched: $modelNames")

                runOnUiThread {
                    if (modelNames.isEmpty()) {
                        supportActionBar?.subtitle = "No Models Found" // Update subtitle
                        addMessage("Fatal error: No models found on the Ollama server.", false)
                        Toast.makeText(this@MainActivity, "No models found on server.", Toast.LENGTH_LONG).show()
                        setUiEnabled(false)
                        return@runOnUiThread
                    }
                    setupModelSpinner(modelNames)
                    binding.modelSpinner.isEnabled = true
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Log.e(TAG, "Failed to fetch models.", e)
                    supportActionBar?.subtitle = "Connection Failed" // Update subtitle
                    addMessage("Fatal error: Could not fetch models from the API.", false)
                    Toast.makeText(this@MainActivity, "Failed to fetch models: ${e.message}", Toast.LENGTH_LONG).show()
                    setUiEnabled(false)
                }
            }
        }
    }

    private fun setupModelSpinner(models: List<String>) {
        Log.d(TAG, "Setting up model spinner.")
        // Use the 'models' list directly. No hint is added.
        val adapter = object : ArrayAdapter<String>(this, R.layout.spinner_item_text, models) {
            // Allow all items to be enabled and selectable.
            override fun isEnabled(position: Int): Boolean {
                return true
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
               return view
            }
        }

        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.modelSpinner.adapter = adapter

        binding.modelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get the selected model directly from the models list.
                val newlySelectedModel = models[position]
                if (selectedModel != newlySelectedModel) {
                    selectedModel = newlySelectedModel
                    supportActionBar?.subtitle = selectedModel // Update the subtitle
                    Log.i(TAG, "Model selected: $selectedModel")
                    messageList.clear()
                    chatAdapter.notifyDataSetChanged()
                    checkApiConnection()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedModel = null
                supportActionBar?.subtitle = "Choose a Model"
                setUiEnabled(false)
            }
        }

        // Ensure the first model is selected by default when the spinner is first set up.
        if (models.isNotEmpty()) {
            binding.modelSpinner.setSelection(0)
        }
    }

    private fun checkApiConnection() {
        val currentModel = selectedModel ?: return
        setUiEnabled(false)
        addMessage("Connecting to '$currentModel'...", false)

        lifecycleScope.launch {
            try {
                val request = OllamaRequest(model = currentModel, prompt = "Are you ready?")
                ollamaService.generateResponse(request)
                runOnUiThread {
                    messageList.removeLast()
                    chatAdapter.notifyItemRemoved(messageList.size)
                    Log.i(TAG, "API connection successful for $currentModel.")
                    addMessage("Connected to $currentModel. How can I help you?", false)
                    setUiEnabled(true)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    messageList.removeLast()
                    chatAdapter.notifyItemRemoved(messageList.size)
                    Log.e(TAG, "API connection failed for $currentModel.", e)
                    addMessage("Fatal error: Could not connect to '$currentModel'. Please choose another model.", false)
                    binding.modelSpinner.isEnabled = true
                }
            }
        }
    }

    private fun handleUserMessage(text: String) {
        val currentModel = selectedModel ?: return
        Log.d(TAG, "Handling user message: $text")
        addMessage(text, true)
        binding.messageEditText.text.clear()

        addMessage("Thinking...", false)
        setUiEnabled(false)

        Log.d(TAG, "Generating model response via API...")
        lifecycleScope.launch {
            try {
                val request = OllamaRequest(model = currentModel, prompt = text)
                val response = ollamaService.generateResponse(request)
                Log.i(TAG, "API response received: '${response.response}'")
                runOnUiThread {
                    messageList.removeLast()
                    chatAdapter.notifyItemRemoved(messageList.size)
                    if (response.response.isNotBlank()) {
                        addMessage(response.response, false)
                    } else {
                        addMessage("Sorry, I received an empty response. Please try again.", false)
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Log.e(TAG, "Error generating response from API.", e)
                    messageList.removeLast()
                    chatAdapter.notifyItemRemoved(messageList.size)
                    addMessage("Sorry, I encountered an error connecting to the API.", false)
                }
            } finally {
                runOnUiThread {
                    setUiEnabled(true)
                }
            }
        }
    }

    private fun addMessage(text: String, isSentByUser: Boolean) {
        val newMessage = ChatMessage(text, System.currentTimeMillis(), isSentByUser)
        messageList.add(newMessage)
        chatAdapter.notifyItemInserted(messageList.size - 1)
        binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
    }

    private fun setUiEnabled(isEnabled: Boolean) {
        binding.sendButton.isEnabled = isEnabled
        binding.messageEditText.isEnabled = isEnabled
        binding.modelSpinner.isEnabled = isEnabled
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity being destroyed.")
    }
}
