package io.mitter.recipes

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import io.mitter.android.Mitter
import io.mitter.android.error.model.base.ApiError
import io.mitter.models.delman.DeliveryEndpoint
import io.mitter.recipes.remote.ApiService
import io.mitter.recipes.remote.LoginRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var mitter: Mitter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val app = application as App
        apiService = app.apiService
        mitter = app.mitter

        johnLoginButton?.setOnClickListener {
            loginUser("@john")
        }

        amyLoginButton?.setOnClickListener {
            loginUser("@amy")
        }

        candiceLoginButton?.setOnClickListener {
            loginUser("@candice")
        }
    }

    private fun loginUser(username: String) {
        apiService.login(
            loginRequest = LoginRequest(
                username = username
            )
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { loginResponse, _ ->
                mitter.setUserId(loginResponse.userId)
                mitter.setUserAuthToken(loginResponse.userAuth)

                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            showErrorToast()
                            return@addOnCompleteListener
                        }

                        val token = task.result?.token
                        mitter.registerFcmToken(
                            token = token!!,
                            onValueAvailableCallback = object : Mitter.OnValueAvailableCallback<DeliveryEndpoint> {
                                override fun onError(apiError: ApiError) {
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                }

                                override fun onValueAvailable(value: DeliveryEndpoint) {
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                }
                            }
                        )
                    }
            }
    }

    private fun showErrorToast() {
        Toast.makeText(
            this,
            "Unable to register delivery endpoint. Please try logging in again!",
            Toast.LENGTH_LONG
        ).show()
    }
}
