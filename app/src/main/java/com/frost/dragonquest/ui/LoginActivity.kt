package com.frost.dragonquest.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.frost.dragonquest.R
import com.frost.dragonquest.databinding.ActivityLoginBinding
import com.frost.dragonquest.extensions.*
import com.frost.dragonquest.model.User
import com.frost.dragonquest.utils.LoadState
import com.frost.dragonquest.utils.LoadingDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    private val loadingDialog = LoadingDialog()

    companion object{
        const val GOOGLE_SIGN_IN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkSession()
        setButtons()
        subscribeToLiveData()
    }

    private fun setButtons() {
        with(binding){
            boedoRadioButton.isClickable
            boedoRadioButton.setOnClickListener {
                if (boedoRadioButton.isActivated) googleButton.visibility = View.VISIBLE
            }
            googleButton.setOnClickListener { setWidget() }
        }
    }

    private fun subscribeToLiveData() {
        viewModel.loadStateLiveData.observe(this) { handleResponse(it) }
        viewModel.userLiveData.observe(this) { handleUser(it) }
        viewModel.errorLiveData.observe(this) { showToast(this, it) }
    }

    private fun handleUser(user: User?) {
        user?.let {
                savePref(it.email)
                MapsActivity.start(this) }
            ?:run { showAlert() }
    }

    private fun handleResponse(loadState: LoadState) {
        when(loadState){
            LoadState.Loading -> loadingDialog.show(supportFragmentManager)
            LoadState.Success -> loadingDialog.dismiss()
            else -> showToast(this, getString(R.string.error))
        }
    }

    private fun checkSession() {
        val session = getEmailPref()
        session?.let { MapsActivity.start(this) }
    }

    private fun setWidget() {
        val googleConfig = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, googleConfig)
        googleClient.signOut()
        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                account?.let { account ->
                    signInWithCredential(GoogleAuthProvider.getCredential(account.idToken, null))
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                account.email?.let { validateAndContinue(account) }
                            }else {
                                showAlert()
                            }
                        }
                }
            }catch (e: ApiException){
                showAlert()
            }
        }
    }

    private fun validateAndContinue(account: GoogleSignInAccount?= null) {
        logEventAnalytics(getString(R.string.analytics_entrance), account?.displayName?:getString(R.string.error))
        viewModel.saveUser(account)
    }
}