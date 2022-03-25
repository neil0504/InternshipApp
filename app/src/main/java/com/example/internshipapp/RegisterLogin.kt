package com.example.internshipapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.internshipapp.databinding.ActivityRegisterLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser

//Client ID: 342380972757-b4herm7opi4qpiajqf6l58mhonsk86ch.apps.googleusercontent.com
//Client Secret: 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

lateinit var registerLogincontext: Context

var googleAccount: GoogleSignInAccount? = null
var nonGoogleAccount: FirebaseUser? = null
private const val TAG = "RegisterLogin"
class RegisterLogin : AppCompatActivity(), RegisterFragment.FromRegisterFragment, LoginFragment.FromLoginFragment {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var googleSignInButton: SignInButton
    private val SIGN_IN = 1
    private lateinit var binding: ActivityRegisterLoginBinding
    private lateinit var loginFragment: LoginFragment
    private lateinit var registerFragment: RegisterFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerLogincontext = this
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        loginFragment = LoginFragment(this, this)
        registerFragment = RegisterFragment(this, this)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, loginFragment).addToBackStack(null).commit()

        binding.googleSignIn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SIGN_IN)
        {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            updateUI(account)
            // Signed in successfully, show authenticated UI.
//            mListener.executeUpdateUI(account)
//            Log.d(TAG, "handleSignInResult: Account sent")
//            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.statusCode)
//            mListener.executeUpdateUI(null)
            Log.d(TAG, "handleSignInResult: null sent")
//            loginAsGuest()
//            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            Toast.makeText(this, "SignIn Failed. Try Again in Handlesignin", Toast.LENGTH_SHORT).show()
        }
//        finally {
//            loginAsGuest()
//            Log.d(TAG, "handleSignInResult: Loging In as Guest. Error Occured during SignIn")
//        }
    }

    private fun updateUI(acc: GoogleSignInAccount?) {
        if (acc != null) {
//            Log.d(TAG_MAIN_ACTIVITY, "Inside UpdateUI Method: Name = ${acc.displayName} and profileURL = ${acc.photoUrl}")
            googleAccount = acc
//            signedInGoogle = true
//            signedInGuest = false
//            player = PlayerDetails(this, acc.id!!, acc.displayName!!, acc.email, acc.photoUrl?: null)
//            Picasso.get().load(acc.photoUrl).placeholder(R.mipmap.ic_launcher).into(profileImg)
//            Picasso.get().load(player.getPhotoURL()).placeholder(R.mipmap.ic_launcher).into(profileImg)
//            name.text = ""
//            name.text = acc.displayName
//            name.text = player.getName()

//            val a = PlayerDetails.getInstance(acc.id!!, acc.displayName!!, acc.email!!, acc.photoUrl!!.toString())

//            FirebaseMessaging.getInstance().isAutoInitEnabled = true

//            FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
//                if(result != null){
//                    myToken = result
//                    Log.d("TAG_MAIN_ACTIVITY", "Got the Token Creater= $myToken")
//                     DO your thing with your firebase token
//                }
//            }
            startActivity(Intent(this, Navigation::class.java))
            Toast.makeText(this, "SignIn Successful", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "SignIn Unsuccessfull. Try Again", Toast.LENGTH_SHORT).show()

        }

    }

    override fun onLoginClicked() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, loginFragment).commit()
    }
    override fun onregisterClicked() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, registerFragment).commit()
    }

    override fun userRegistered(currentUser: FirebaseUser?) {
        nonGoogleAccount = currentUser
        startActivity(Intent(this, Navigation::class.java))
    }

    override fun userSignedIn(currentUser: FirebaseUser?) {
        nonGoogleAccount = currentUser
        startActivity(Intent(this, Navigation::class.java))
    }

    override fun onStart() {
        super.onStart()
        googleAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (googleAccount != null){
            nonGoogleAccount = null
            startActivity(Intent(this, Navigation::class.java))
        }
    }
}