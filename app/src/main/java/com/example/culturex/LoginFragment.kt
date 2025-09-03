package com.example.culturex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment() {

    // UI Components
    private var btnBack: ImageView? = null
    private var setEmail: TextInputEditText? = null
    private var setPassword: TextInputEditText? = null
    private var tvForgotPassword: TextView? = null
    private var btnLogin: Button? = null
    private var btnGoogle: ImageView? = null
    private var btnFacebook: ImageView? = null
    private var btnApple: ImageView? = null
    private var tvSignUp: TextView? = null

    companion object {
        // Hardcoded credentials for testing
        private const val VALID_USERNAME = "student"
        private const val VALID_PASSWORD = "student"

        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        initViews(view)

        // Set up click listeners
        setupClickListeners()

        // Pre-fill email field for testing
        setEmail?.setText("student")
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.back_arrow)
        setEmail = view.findViewById(R.id.email_input)
        setPassword = view.findViewById(R.id.password_input)
        tvForgotPassword = view.findViewById(R.id.forgot_password)
        btnLogin = view.findViewById(R.id.login_button)
        btnGoogle = view.findViewById(R.id.google_login)
        btnFacebook = view.findViewById(R.id.facebook_login)
        btnApple = view.findViewById(R.id.apple_login)
        tvSignUp = view.findViewById(R.id.sign_up_link)
    }

    private fun setupClickListeners() {
        // Back button
        btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        // Login button
        btnLogin?.setOnClickListener {
            performLogin()
        }

        // Forgot password
        tvForgotPassword?.setOnClickListener {
            showToast("Forgot password clicked")
        }

        // Social login buttons
        btnGoogle?.setOnClickListener {
            showToast("Google login clicked")
        }

        btnFacebook?.setOnClickListener {
            showToast("Facebook login clicked")
        }

        btnApple?.setOnClickListener {
            showToast("Apple login clicked")
        }

        // Sign up link
        tvSignUp?.setOnClickListener {
            showToast("Sign up clicked")
        }
    }

    private fun performLogin() {
        val username = setEmail?.text.toString().trim()
        val password = setPassword?.text.toString().trim()

        if (username.isEmpty()) {
            setEmail?.error = "Username/Email is required"
            setEmail?.requestFocus()
            return
        }

        if (password.isEmpty()) {
            setPassword?.error = "Password is required"
            setPassword?.requestFocus()
            return
        }

        // Clear errors
        setEmail?.error = null
        setPassword?.error = null

        if (isValidCredentials(username, password)) {
            showToast("Login successful! Welcome, $username")
            // TODO: Navigate to dashboard or MainActivity
        } else {
            showToast("Invalid credentials. Use 'student' for both username and password.")
            setPassword?.setText("")
            setPassword?.requestFocus()
        }
    }

    private fun isValidCredentials(username: String, password: String): Boolean {
        return VALID_USERNAME == username && VALID_PASSWORD == password
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up references
        btnBack = null
        setEmail = null
        setPassword = null
        tvForgotPassword = null
        btnLogin = null
        btnGoogle = null
        btnFacebook = null
        btnApple = null
        tvSignUp = null
    }
}
