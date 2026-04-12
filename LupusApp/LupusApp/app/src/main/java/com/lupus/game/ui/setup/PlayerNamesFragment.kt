package com.lupus.game.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lupus.game.R
import com.lupus.game.databinding.FragmentPlayerNamesBinding
import com.lupus.game.viewmodel.GameViewModel

class PlayerNamesFragment : Fragment() {

    private var _binding: FragmentPlayerNamesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()
    private val nameFields = mutableListOf<EditText>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val count = arguments?.getInt("playerCount") ?: 4

        nameFields.clear()
        binding.llNames.removeAllViews()

        for (i in 1..count) {
            val et = EditText(requireContext()).apply {
                hint = "Nome giocatore $i"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 8, 0, 8) }
                setPadding(16)
                background = resources.getDrawable(R.drawable.bg_edit_text, null)
            }
            nameFields.add(et)
            binding.llNames.addView(et)
        }

        binding.btnNext.setOnClickListener {
            val names = nameFields.map { it.text.toString().trim() }
            if (names.any { it.isEmpty() }) {
                Toast.makeText(requireContext(), "Inserisci tutti i nomi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (names.distinct().size != names.size) {
                Toast.makeText(requireContext(), "I nomi devono essere univoci", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bundle = Bundle().apply {
                putStringArray("playerNames", names.toTypedArray())
            }
            findNavController().navigate(R.id.action_names_to_roles, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
