package com.diary.paintlog.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.diary.paintlog.R
import com.diary.paintlog.databinding.FragmentSecondBinding
import com.diary.paintlog.model.repository.TokenRepository
import com.diary.paintlog.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    companion object {
        const val TAG = "SecondFragment"
    }

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        val token =
            TokenRepository()

        CoroutineScope(Dispatchers.Main).launch {
            Log.i(TAG, token.getToken())
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var textView = binding.textviewSecond

        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // ViewModel을 사용하여 데이터 가져오기 또는 업데이트
        viewModel.getUserAll().observe(viewLifecycleOwner, Observer { data ->
            // 데이터가 변경될 때 수행할 작업
            textView.text = data.toString()
        })

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}