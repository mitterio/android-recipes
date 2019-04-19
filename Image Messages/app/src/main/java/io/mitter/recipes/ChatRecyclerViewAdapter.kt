package io.mitter.recipes

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.fasterxml.jackson.databind.ObjectMapper
import io.mitter.models.mardle.messaging.Message
import io.mitter.models.mardle.messaging.StandardPayloadTypeNames
import io.mitter.models.mardle.messaging.StandardTimelineEventTypeNames
import io.mitter.models.mardle.messaging.TimelineEvent
import kotlinx.android.synthetic.main.item_image_message_self.view.*
import kotlinx.android.synthetic.main.item_message_other.view.*
import kotlinx.android.synthetic.main.item_message_self.view.*
import org.greenrobot.eventbus.EventBus

class ChatRecyclerViewAdapter(
    private val messageList: List<Message>,
    private val currentUserId: String
) : RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>() {
    private val objectMapper = ObjectMapper()

    private val MESSAGE_SELF_VIEW = 0
    private val MESSAGE_OTHER_VIEW = 1
    private val MESSAGE_SELF_IMAGE_VIEW = 2
    private val MESSAGE_OTHER_IMAGE_VIEW = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = when (viewType) {
            MESSAGE_SELF_VIEW -> R.layout.item_message_self
            MESSAGE_SELF_IMAGE_VIEW -> R.layout.item_image_message_self
            else -> R.layout.item_message_other
        }
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMessage(messageList[position])
    }

    override fun getItemViewType(position: Int) = when (messageList[position].payloadType) {
        StandardPayloadTypeNames.TextMessage ->
            if (messageList[position].senderId.domainId() == currentUserId)
                MESSAGE_SELF_VIEW else MESSAGE_OTHER_VIEW
        StandardPayloadTypeNames.ImageMessage ->
            if (messageList[position].senderId.domainId() == currentUserId)
                MESSAGE_SELF_IMAGE_VIEW else MESSAGE_OTHER_IMAGE_VIEW
        else -> MESSAGE_OTHER_VIEW
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bindMessage(message: Message) {
            with(message) {
                if (senderId.domainId() == currentUserId) {
                    if (payloadType == StandardPayloadTypeNames.TextMessage) {
                        itemView?.selfMessageText?.text = textPayload
                    } else {
                        val remoteImage = objectMapper.treeToValue(messageData[0].data, RemoteImage::class.java)

                        Glide.with(itemView.context)
                            .load(getRemoteImagePath(remoteImage))
                            .centerCrop()
                            .into(itemView.selfMessageImage)
                        itemView?.selfMessageCaption?.text = textPayload
                    }
                } else {
                    itemView?.otherMessageText?.text = textPayload
                }
            }
        }

        private fun getRemoteImagePath(remoteImage: RemoteImage): String =
            "https://api.mitter.io${remoteImage.link.replace("\$repr", remoteImage.repr[0])}"
    }
}
