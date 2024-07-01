package com.sherrif.mediclab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.textview.MaterialTextView
import com.sherrif.mediclab.databinding.FragmentHomeBinding
import com.sherrif.mediclab.helpers.PrefsHelper
import org.json.JSONObject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
        //        homeViewModel.text.observe(viewLifecycleOwner) {
//
//        }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        fetch the textviews
        val surname : MaterialTextView = binding.surname
        val others : MaterialTextView = binding.others
        val email : MaterialTextView = binding.email
        val phone : MaterialTextView = binding.phone
        val gender : MaterialTextView = binding.gender
        val dob : MaterialTextView = binding.dob

        //get member from shared preferences using member userObject key
        val member = PrefsHelper.getPrefs(requireContext(),"userObject")
        //convert to json object
        val user = JSONObject(member)
        //get surname
        surname.text = user.getString("surname")
        others.text = user.getString("others")
        email.text = user.getString("email")
        phone.text = user.getString("phone")
        gender.text = user.getString("gender")
        dob.text = user.getString("dob")





        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}