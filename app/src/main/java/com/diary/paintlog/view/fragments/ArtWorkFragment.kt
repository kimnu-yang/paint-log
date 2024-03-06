package com.diary.paintlog.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diary.paintlog.R
import com.diary.paintlog.databinding.FragmentArtWorkBinding
import com.diary.paintlog.utils.Common
import com.diary.paintlog.view.adapter.ArtWorkAdapter
import com.diary.paintlog.viewmodel.MyArtViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ArtWorkFragment : Fragment() {

    data class Artwork(
        var title: String,
        var summary: String
    )

    private var _binding: FragmentArtWorkBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter

    private lateinit var myArtViewModel: MyArtViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myArtViewModel = ViewModelProvider(this)[MyArtViewModel::class.java]

        CoroutineScope(Dispatchers.Default).launch {
            val data = myArtViewModel.getMyArtAll().toMutableList()

            activity?.runOnUiThread {
                if (data.isEmpty()) {
                    binding.artworkListEmpty.visibility = View.VISIBLE
                } else {
                    binding.artworkListEmpty.visibility = View.GONE

                    // set adapter
                    val adapter = ArtWorkAdapter(data, resources, requireContext())
                    binding.artworkCount.text = getString(R.string.artwork_count, data.size.toString())
                    binding.artworkList.layoutManager = LinearLayoutManager(requireContext())
                    binding.artworkList.adapter = adapter

                    binding.artworkOrderView.setOnClickListener {
                        data.reverse()
                        adapter.notifyItemRangeChanged(0, data.size)
                    }
                }

                binding.artworkColorFilter.setOnClickListener {
                    binding.artworkColorFilter.children.forEach { color ->
                        color.alpha = 0.5f
                    }

                    val adapter = ArtWorkAdapter(data, resources, requireContext())
                    binding.artworkCount.text = getString(R.string.artwork_count, data.size.toString())
                    binding.artworkList.layoutManager = LinearLayoutManager(requireContext())
                    binding.artworkList.adapter = adapter

                    if(data.size > 0){
                        binding.artworkListEmpty.visibility = View.GONE

                        binding.artworkOrderView.setOnClickListener {
                            data.reverse()
                            adapter.notifyItemRangeChanged(0, data.size)
                        }
                    } else {
                        binding.artworkListEmpty.visibility = View.VISIBLE
                    }
                }

                binding.artworkColorFilter.children.forEach {
                    it.setOnClickListener { clickIt ->
                        binding.artworkColorFilter.children.forEach { color ->
                            if (color.tooltipText != clickIt.tooltipText) {
                                color.alpha = 0.5f
                            } else {
                                color.alpha = 1f
                            }
                        }

                        val select = Common.getColorByString(clickIt.tooltipText.toString())
                        val selectColor = ContextCompat.getColor(requireContext(), select)

                        val newData = Common.getMyArtWithInfoByRGB(data, selectColor)

                        binding.artworkList.adapter = null
                        if(newData.size > 0){
                            val adapter = ArtWorkAdapter(newData, resources, requireContext())
                            binding.artworkCount.text = getString(R.string.artwork_count, newData.size.toString())
                            binding.artworkList.layoutManager = LinearLayoutManager(requireContext())
                            binding.artworkList.adapter = adapter
                            adapter.notifyItemRangeChanged(0, newData.size)

                            binding.artworkOrderView.setOnClickListener {
                                newData.reverse()
                                adapter.notifyItemRangeChanged(0, newData.size)
                            }

                            binding.artworkListEmpty.visibility = View.GONE
                        } else {
                            binding.artworkCount.text = getString(R.string.artwork_count, "0")
                            binding.artworkListEmpty.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}