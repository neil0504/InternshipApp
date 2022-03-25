package com.example.internshipapp.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.internshipapp.*
import com.example.internshipapp.databinding.FragmentUserBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = binding.imageView
        val email = binding.email
        val name = binding.name
        val logout = binding.logout
        logout.setOnClickListener {
            if (googleAccount == null){
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(ccc, RegisterLogin::class.java))
            }else{
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
                val mGoogleSignInClient = GoogleSignIn.getClient(registerLogincontext, gso)
                mGoogleSignInClient.signOut()
                startActivity(Intent(ccc, RegisterLogin::class.java))
            }
        }
        if (googleAccount != null){
            val imageUri = googleAccount!!.photoUrl
            if (imageUri != null){
                Picasso.with(context).load(imageUri).noFade()
                    .placeholder(R.drawable.account_white)
                    .error(R.drawable.account_white)
                    .into(imageView)
            }else{
                Picasso.with(context).load(R.drawable.account_white).noFade()
                    .placeholder(R.drawable.account_white)
                    .error(R.drawable.account_white)
                    .into(imageView)
            }

            email.text = googleAccount!!.email
            name.text = googleAccount!!.displayName

        }else{
            val imageUri = R.drawable.account_white
            Picasso.with(context).load(imageUri).noFade()
                .placeholder(R.drawable.account_white)
                .error(R.drawable.account_white)
                .into(imageView)
            email.text = nonGoogleAccount!!.email
            name.text = nonGoogleAccount!!.displayName
        }



    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}