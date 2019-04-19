package io.mitter.recipes

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.mitter.android.Mitter
import io.mitter.android.error.model.base.ApiError
import io.mitter.models.delman.DeliveryEndpoint

class DemoFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String?) {
        val mitter = (application as App).mitter
        mitter.registerFcmToken(token!!, object : Mitter.OnValueAvailableCallback<DeliveryEndpoint> {
            override fun onError(apiError: ApiError) {

            }

            override fun onValueAvailable(value: DeliveryEndpoint) {

            }
        })
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val mitter = (application as App).mitter
        val messagingPipelinePayload = mitter.parseFcmMessage(remoteMessage!!.data)

        if (mitter.isMitterMessage(messagingPipelinePayload)) {
            mitter.processPushMessage(messagingPipelinePayload)
        }
    }
}
