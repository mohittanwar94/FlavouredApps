package com.qureka.skool.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.qureka.skool.QurekaSkoolApplication
import com.qureka.skool.R
import com.qureka.skool.databinding.DialogGiftBoxScreenBinding

class DialogGiftBox : DialogFragment(), View.OnClickListener {
    private lateinit var _binding: DialogGiftBoxScreenBinding
    private var clickViewListener: OnClickType? = null

    companion object {
        fun newInstance() =
            DialogGiftBox().apply {
                arguments = Bundle().apply {
                }
            }
    }

    fun setListener(clickListener: OnClickType) {
        clickViewListener = clickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogGiftBoxScreenBinding.inflate(inflater, container, false)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.sunflower_yellow)
        dialog?.setCancelable(true)
        dialog?.setOnDismissListener {
            clickViewListener?.onClick(2)
            dismissAllowingStateLoss()
        }
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                clickViewListener?.onClick(2)
                dismissAllowingStateLoss()
            }
            true
        }
        WindowManager.LayoutParams.FLAG_SECURE
        WindowManager.LayoutParams.FLAG_SECURE
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.AppDialogTheme
        )
    }

    @SuppressLint("SetTextI18n")
    private fun initUi() {
        with(_binding) {
            btnWatchVideoNow.setOnClickListener(this@DialogGiftBox)
            ivClose.setOnClickListener(this@DialogGiftBox)
            when (QurekaSkoolApplication.getApplication().packageName) {
                requireContext().getString(R.string.app_02) -> {
                    ivGiftBoxStrip.isVisible = false
                }
            }
        }
        val width = (resources.displayMetrics.widthPixels * if (getString(R.string.app_36) == requireActivity().packageName) 0.70 else 0.90).toInt()
        dialog?.window?.setLayout(
            width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnWatchVideoNow -> {
                //QurekaSkoolApplication.getApplication().logFirebaseEvent(Event.WATCH_NOW)
                clickViewListener?.onClick(1)
                dismissAllowingStateLoss()
            }

            R.id.ivClose -> {
                clickViewListener?.onClick(2)
                dismissAllowingStateLoss()
            }
        }
    }

    interface OnClickType {
        fun onClick(type: Int)
    }
}