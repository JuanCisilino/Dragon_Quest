package com.frost.dragonquest.utils


sealed class LoadState{
    object Loading: LoadState()
    object Success : LoadState()
    object Error : LoadState()
}
