package com.diary.paintlog.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diary.paintlog.R
import com.diary.paintlog.databinding.FragmentArtWorkBinding
import com.diary.paintlog.view.adapter.ArtWorkAdapter
import com.diary.paintlog.viewmodel.ArtWorkViewModel


class ArtWorkFragment : Fragment() {

    data class Artwork(
        var title: String,
        var summary: String
    )

    private var _binding: FragmentArtWorkBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter

    private lateinit var viewModel: ArtWorkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set data
        val data = mutableListOf(
            Artwork("Do", "a deer a female deer"),
            Artwork("Re", "a drop of golden sun"),
            Artwork("Mi", "a name, I call myself"),
            Artwork("Fa", "a long, long way to run"),
            Artwork("So", "a needle pulling thread"),
            Artwork("La", "a note to follow So"),
            Artwork("Ti", "a drink with jam and bread"),
            Artwork("Do", "That will bring us back to Do, oh oh oh~"),
            Artwork("Doremifasolasido Sodo!", "- fin -")
        )

        if (data.isEmpty()) {
            binding.artworkListEmpty.visibility = View.VISIBLE
        } else {
            binding.artworkListEmpty.visibility = View.GONE

            // set adapter
            val adapter = ArtWorkAdapter(data)

            binding.artworkCount.text = getString(R.string.artwork_count, data.size.toString())
            binding.artworkList.layoutManager = LinearLayoutManager(requireContext())
            binding.artworkList.adapter = adapter

            binding.artworkOrderView.setOnClickListener {
                data.reverse()
                adapter.notifyItemRangeChanged(0, data.size)
            }
        }
    }
}