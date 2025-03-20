package com.ravi.skinhealthyai.ui.history

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.ravi.skinhealthyai.R
import com.ravi.skinhealthyai.data.model.History
import com.ravi.skinhealthyai.databinding.FragmentHistoryBinding
import com.ravi.skinhealthyai.ui.ViewModelFactory
import com.ravi.skinhealthyai.ui.adapter.HistoryAdapter
import com.ravi.skinhealthyai.utils.displayDate
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            if (_binding == null) return@launch
            setupUI()
            setupActionBack()
            setupObserve()
            viewModel.getAllHistory()
        }
    }

    private fun setupUI() {
        binding.topAppBar.inflateMenu(R.menu.top_app_bar_history)
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.calender_menu -> {
                    showCalender()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupActionBack() {
        val topAppBar: MaterialToolbar = binding.topAppBar
        topAppBar.setNavigationIcon(R.drawable.ic_arrow_back)

        topAppBar.setNavigationOnClickListener {
            @Suppress("DEPRECATION")
            requireActivity().onBackPressed()
        }
    }

    private fun setupObserve() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            showEmptyText(isEmpty)
            showDataList(isEmpty)
        }

        viewModel.historyData.observe(viewLifecycleOwner) { data ->
            getAllHistory(data)
        }
    }

    private fun getAllHistory(data: PagedList<History>) {
        if (_binding == null) return

        val adapter = HistoryAdapter(object : HistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: History) {
                showSelectedHistory(data)
            }
        })
        binding.rvHistory.adapter = adapter
        adapter.submitList(data)
    }

    private fun showSelectedHistory(data: History) {
        val action = HistoryFragmentDirections.actionNavigationHistoryToNavigationDetailHistory(data.id)
        findNavController().navigate(action)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun showCalender() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select date range")
            .setTheme(R.style.ThemeCalender)
            .setSelection(Pair(null, null))
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second
            if (startDate != null && endDate != null) {
                val adapter = binding.rvHistory.adapter as? HistoryAdapter
                adapter?.submitList(null)
                adapter?.notifyDataSetChanged()
                viewModel.getAllHistory(startDate, endDate)
                binding.listTitleHistory.text = "Filter : " + displayDate(startDate) + " - " + displayDate(endDate)
            }
        }
        datePicker.show(parentFragmentManager, datePicker.toString())
    }

    private fun showLoading(isLoading: Boolean) {
        if (_binding != null) {
            binding.progresBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showEmptyText(isEmpty: Boolean) {
        if (_binding != null) {
            binding.textNotFound.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    private fun showDataList(isEmpty: Boolean) {
        if (_binding != null) {
            binding.rvHistory.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
