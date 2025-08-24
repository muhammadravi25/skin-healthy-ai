package com.ravi.skinhealthyai.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.ravi.skinhealthyai.R
import com.ravi.skinhealthyai.data.model.History
import com.ravi.skinhealthyai.databinding.FragmentDetailHistoryBinding
import com.ravi.skinhealthyai.ui.ViewModelFactory
import kotlinx.coroutines.launch


class DetailHistoryFragment : Fragment() {
    private var _binding: FragmentDetailHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HistoryViewModel>{
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLifecycle()
    }

    private fun setupLifecycle() {
        val historyId = DetailHistoryFragmentArgs.fromBundle(requireArguments()).historyId
        @Suppress("DEPRECATION")
        viewLifecycleOwner.lifecycleScope.launch {
            if (_binding == null) return@launch
            setupActionBack()
            setupObserve()
            viewModel.getHistoryById(historyId)
        }
    }

    private fun setupObserve() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            showEmptyText(isEmpty)
        }

        viewModel.selectedHistory.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                setupUI(true)
                setData(data)
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

    private fun setData(data: History) {
        val convertToPercent = (data.accuracy * 100).toInt().toString()
        val cornerRadius = 20
        binding.apply {
            nameDiseaseScan.text = data.nameSkinDisease
            accuracyDiseaseScan.text = resources.getString(R.string.accuracy, "$convertToPercent%")
            Glide.with(requireActivity())
                .load(data.photo)
                .apply(
                    RequestOptions.bitmapTransform(RoundedCorners(cornerRadius))
                        .error(R.drawable.image_warning)
                )
                .into(imageResultScan)
        }
    }

    private fun setupUI(isShow: Boolean){
        binding.apply {
            imageResultScan.visibility = if (isShow) View.VISIBLE else View.GONE
            diagnoseDiseaseScan.visibility = if (isShow) View.VISIBLE else View.GONE
            cardDetailDiagnose.visibility = if (isShow) View.VISIBLE else View.GONE
        }
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

    override fun onResume() {
        super.onResume()
        setupLifecycle()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}