package io.mitter.recipes

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.mitter.android.Mitter
import io.mitter.android.error.model.base.ApiError
import io.mitter.models.mardle.messaging.Message
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.content.Intent
import android.util.Log

class MainActivity : AppCompatActivity() {
    private val IMAGE_PICKER_CODE = 123

    private val channelId = "cf4a4b80-65a6-11e9-abaa-5750cb139a6a"
    private val messageList: MutableList<Message> = mutableListOf()
    private lateinit var chatRecyclerViewAdapter: ChatRecyclerViewAdapter
    private lateinit var mitter: Mitter
    private lateinit var messaging: Mitter.Messaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mitter = (application as App).mitter
        messaging = mitter.Messaging()
        EventBus.getDefault().register(this)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        chatRecyclerView?.layoutManager = linearLayoutManager

        messaging.getMessagesInChannel(
            channelId = channelId,
            onValueAvailableCallback = object : Mitter.OnValueAvailableCallback<List<Message>> {
                override fun onError(apiError: ApiError) {

                }

                override fun onValueAvailable(value: List<Message>) {
                    val messages = value.reversed()

                    messageList.addAll(messages)
                    chatRecyclerViewAdapter = ChatRecyclerViewAdapter(
                        messageList = messageList,
                        currentUserId = mitter.getUserId()
                    )

                    chatRecyclerView?.adapter = chatRecyclerViewAdapter
                }
            }
        )

        chooseImage?.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                IMAGE_PICKER_CODE
            )
        }

        sendButton?.setOnClickListener {
            messaging.sendTextMessage(
                channelId = channelId,
                message = messageEditText.text.toString()
            )
            messageEditText.text.clear()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICKER_CODE) {
                val imageUri = data?.data
                val previewImageIntent = Intent(this, PhotoActivity::class.java)
                previewImageIntent.putExtra(Constants.PREVIEW_IMAGE, imageUri)
                previewImageIntent.putExtra(Constants.CHANNEL_ID, channelId)

                startActivity(previewImageIntent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewMessage(message: Message) {
        messageList.add(message)
        chatRecyclerViewAdapter.notifyItemInserted(messageList.size - 1)
        chatRecyclerView.scrollToPosition(messageList.size - 1)
    }
}
