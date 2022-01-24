package com.sam.googlelogintest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sam.googlelogintest.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    companion object {
        private const val TAG = "HomeFragment"
    }

    private var isLogin: Boolean = false
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentHomeBinding
    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
            isLogin = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_server_id))
            .requestServerAuthCode(getString(R.string.google_login_server_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            if (isLogin) {
                signOut()
            } else {
                signIn()
            }
        }

        binding.btnNew.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isLogin) signOut()
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        googleLauncher.launch(signInIntent)
    }

    private fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener {
            isLogin = false
            binding.tvInfo.text = "info"
            binding.btnLogin.text = "Google Login"
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            binding.tvInfo.text = "idToken=${account.idToken}\nname=${account.displayName}\nemail=${account.email}" +
                    "\nid=${account.id}\nauthCode=${account.serverAuthCode} "
            binding.btnLogin.text = "Google Log Out"

        } catch (e: ApiException) {
            binding.tvInfo.text = "${e.statusCode} ${e.localizedMessage}"
            binding.btnLogin.text = "Google Login"
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }
}