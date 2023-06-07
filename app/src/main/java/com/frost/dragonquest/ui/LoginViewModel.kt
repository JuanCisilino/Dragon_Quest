package com.frost.dragonquest.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frost.dragonquest.model.User
import com.frost.dragonquest.utils.LoadState
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class LoginViewModel: ViewModel() {

    var loadStateLiveData = MutableLiveData<LoadState>()
    var userLiveData = MutableLiveData<User?>()
    var errorLiveData = MutableLiveData<String>()

    fun saveUser(account: GoogleSignInAccount?){
        //loadStateLiveData.postValue(LoadState.Loading)
        account?.let {
            it.email
                ?.let { email ->
                    val user = User(email = email, nombre = it.displayName!!)
          //          loadStateLiveData.postValue(LoadState.Success)
                    userLiveData.postValue(user)
                }
                ?:run {
           //         loadStateLiveData.postValue(LoadState.Error)
                    errorLiveData.postValue("Usuario de google no encontrado...")
                }
        }

    }

}