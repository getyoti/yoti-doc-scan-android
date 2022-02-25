package com.yoti.mobile.android.sdk.yotidocscan.websample


import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yoti.mobile.android.sdk.yotidocscan.websample.databinding.BottomSheetBinding

private const val SESSION_PREFERENCES_ID = "SESSION_PREFERENCES_ID"
private const val SESSION_CONFIGURATION_KEY = "SESSION_CONFIGURATION_KEY"

class SessionBottomSheet: BottomSheetDialogFragment() {

    private var sessionConfigurationListener: SessionConfigurationListener? = null
    lateinit var sharedPreferences: SharedPreferences

    private var _binding: BottomSheetBinding? = null
    private val binding: BottomSheetBinding get() = _binding!!

    interface SessionConfigurationListener {
        fun onSessionConfigurationSuccess(sessionUrl: String)
        fun onSessionConfigurationDismiss()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {
            sessionUrl.setText(sharedPreferences.getString(SESSION_CONFIGURATION_KEY, ""))

            startSessionButton.setOnClickListener {
                sessionConfigurationListener?.onSessionConfigurationSuccess(
                        sessionUrl.text.toString()
                )
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        sharedPreferences.edit().putString(SESSION_CONFIGURATION_KEY, binding.sessionUrl.text.toString()).apply()
        sessionConfigurationListener?.onSessionConfigurationDismiss()
        super.onDismiss(dialog)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = requireContext().getSharedPreferences(SESSION_PREFERENCES_ID, Context.MODE_PRIVATE)
        sessionConfigurationListener = context as? SessionConfigurationListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        sessionConfigurationListener = null
        super.onDetach()
    }

    companion object {
        const val FRAGMENT_TAG = "com.yoti.mobile.android.sdk.yotidocscan.websample.SessionBottomSheet"

        fun newInstance() = SessionBottomSheet()
    }
}