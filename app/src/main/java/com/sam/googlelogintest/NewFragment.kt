package com.sam.googlelogintest

import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.sam.googlelogintest.databinding.FragmentNewBinding


class NewFragment : Fragment() {

    companion object {
        private const val TAG = "NewFragment"
        private const val REQUEST_CODE_GOOGLE_SIGN_IN = 1
    }

    private lateinit var binding: FragmentNewBinding

    private val googleLauncher = registerForActivityResult(StartIntentSenderForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val credential = Identity.getSignInClient(requireActivity()).getSignInCredentialFromIntent(
                result.data
            )
            binding.tvInfo.text = "id=${credential.googleIdToken}\nname=${credential.displayName}\nemail=${credential.id}"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {

            signIn()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun signIn() {
        val request = GetSignInIntentRequest.builder()
            .setServerClientId(getString(R.string.google_login_server_id))
            .build()
        Identity.getSignInClient(requireActivity())
            .getSignInIntent(request)
            .addOnSuccessListener { result ->
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(result.intentSender).build()
                    googleLauncher.launch(intentSenderRequest)
                } catch (e: SendIntentException) {
                    Log.e(TAG, "sam00 Google Sign-in failed e=${e.localizedMessage}")
                }
            }
            .addOnFailureListener { e -> Log.e(TAG, "sam00 Google Sign-in failed", e) }
    }
}