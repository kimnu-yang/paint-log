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
import com.diary.paintlog.viewmodel.UserViewModel
import androidx.lifecycle.observe
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.User
import com.diary.paintlog.model.repository.TokenRepository
import com.diary.paintlog.view.activities.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

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
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        val token = TokenRepository((binding.root.context.applicationContext as GlobalApplication).dataStore)

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

        // activity_main.xml에서 정의한 FloatingActionButton에 접근
        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)

        // fab을 사용하여 작업 수행
        fab.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                // 클릭 이벤트 처리
                viewModel.saveUser(
                    User(
                        0L,
                        "REGISTERED",
                        43643436L,
                        64364363643L,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                    )
                )
            }
        }

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}