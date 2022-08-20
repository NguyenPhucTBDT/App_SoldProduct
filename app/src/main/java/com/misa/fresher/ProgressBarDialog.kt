package com.example.historyvideokotlin.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import com.misa.fresher.R

class ProgressBarDialog(context: Context) : Dialog(context, R.style.LoadingDialog) {

    private var animation: RotateAnimation? = null
    private var imgLoading: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress_bar)
        animation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        animation?.duration = 900
        animation?.repeatCount = Animation.INFINITE
        imgLoading = findViewById(R.id.imgLoading)
        imgLoading?.startAnimation(animation)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onStart() {
        super.onStart()
        imgLoading?.startAnimation(animation)
    }

    override fun onStop() {
        super.onStop()
        animation?.cancel()
    }

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }
}