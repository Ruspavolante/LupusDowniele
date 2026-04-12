package com.lupus.game.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lupus.game.R
import com.lupus.game.databinding.FragmentSetupBinding
import com.lupus.game.viewmodel.GameViewModel

class SetupFragment : Fragment() {

    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            val countText = binding.etPlayerCount.text.toString()
            val count = countText.toIntOrNull()
            if (count == null || count < 3) {
                Toast.makeText(requireContext(), "Inserisci almeno 3 giocatori", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bundle = Bundle().apply { putInt("playerCount", count) }
            findNavController().navigate(R.id.action_setup_to_names, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
