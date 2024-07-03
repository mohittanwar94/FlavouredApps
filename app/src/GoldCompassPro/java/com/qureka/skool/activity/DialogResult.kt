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
import androidx.fragment.app.DialogFragment
import com.qureka.skool.R
import com.qureka.skool.databinding.FdResultDialogBinding

class DialogResult : DialogFragment(), View.OnClickListener {
    private lateinit var _binding: FdResultDialogBinding
    private var clickViewListener: OnClickType? = null

    companion object {
        fun newInstance() =
            DialogResult().apply {
                arguments = Bundle().apply {
                }
            }
    }

    fun setData(
        p: Double,
        r: Double,
        t: Double,
        s: String,
        amount: Double,
        gain: Double,
        currency: String
    ) {
        initUi(p, r, t, s, amount, gain, currency)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FdResultDialogBinding.inflate(inflater, container, false)
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.AppDialogTheme
        )
    }

    @SuppressLint("SetTextI18n")
    private fun initUi(
        p: Double,
        r: Double,
        t: Double,
        s: String,
        amount: Double,
        gain: Double,
        currency: String
    ) {
        with(_binding) {
            ivClose.setOnClickListener(View.OnClickListener { v: View? ->
                dialog!!.dismiss()
            })
            btnOk.setOnClickListener(View.OnClickListener { v: View? ->
                dialog!!.dismiss()
            })
            tvPrinciple.setText(currency + p)
            tvInterest.setText(r.toString() + "%")
            tvYears.setText(t.toString())
            tvFrequency.setText(s)
            tvGrossAmount.setText(currency + String.format("%.2f", amount))
            tvGain.setText(currency + String.format("%.2f", gain))
        }
        val width =
            (resources.displayMetrics.widthPixels * if (getString(R.string.app_36) == requireActivity().packageName) 0.70 else 0.90).toInt()
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