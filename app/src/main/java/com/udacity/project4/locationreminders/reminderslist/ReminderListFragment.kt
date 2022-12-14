package com.udacity.project4.locationreminders.reminderslist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.authentication.LoginViewModel
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.setTitle
import com.udacity.project4.utils.setup
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {
    //use Koin to retrieve the ViewModel instance
    private val loginViewModel: LoginViewModel by activityViewModels()
    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentRemindersBinding

    private val TAG = "ReminderListFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reminders, container, false
            )
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.refreshLayout.setOnRefreshListener { _viewModel.loadReminders() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
        observeAuthenticationState()
        binding.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }
    }
//    private fun observeAuthenticationState() {
//        //val factToDisplay = viewModel.getFactToDisplay(requireContext())
//        LoginViewModel.AuthenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
//            when (authenticationState) {
//                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
//                    Log.e(TAG, FirebaseAuth.getInstance().currentUser?.displayName.toString())
//                    Log.e(TAG, FirebaseAuth.getInstance().currentUser?.email.toString())
//                    Log.i(TAG, "Authenticated")
////                    binding.authButton.text = getString(R.string.logout_button_text)
////                    binding.authButton.setOnClickListener {
////                        AuthUI.getInstance().signOut(requireContext())
//                    }
//
//                    binding.welcomeText.text = getFactWithPersonalization(factToDisplay)
//
//                }
//                else -> {
//                    binding.authButton.text = getString(R.string.login_button_text)
//                    binding.authButton.setOnClickListener { launchSignInFlow() }
//                    binding.welcomeText.text = factToDisplay
//                }
//            }
//        })

private fun observeAuthenticationState() {
    loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
        when (authenticationState) {
            LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                Log.e(TAG, FirebaseAuth.getInstance().currentUser?.displayName.toString())
                Log.e(TAG, FirebaseAuth.getInstance().currentUser?.email.toString())
                Log.i(TAG, "Authenticated")
            }

            LoginViewModel.AuthenticationState.UNAUTHENTICATED -> {
                val intent = Intent(context, AuthenticationActivity::class.java)
                activity?.finish()
                startActivity(intent)
            }
            else -> Log.e(
                TAG, "New $authenticationState state that doesn't require any UI change"
            )
        }
    })
}


    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        _viewModel.loadReminders()
    }

    private fun navigateToAddReminder() {
        //use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                ReminderListFragmentDirections.toSaveReminder()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
        }

//        setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                AuthUI.getInstance().signOut(requireContext())
                Log.e(TAG,"Logged Out")
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        display logout as menu item
        inflater.inflate(R.menu.main_menu, menu)
    }

}



