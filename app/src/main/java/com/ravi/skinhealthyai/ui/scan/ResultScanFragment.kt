package com.ravi.skinhealthyai.ui.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.ravi.skinhealthyai.R
import com.ravi.skinhealthyai.data.model.History
import com.ravi.skinhealthyai.databinding.FragmentResultScanBinding
import com.ravi.skinhealthyai.ui.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultScanFragment : Fragment() {
    private var _binding: FragmentResultScanBinding? = null
    private val binding get() = _binding!!

    private val viewModelResultScan by viewModels<ResultScanViewModel>{
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        if (_binding == null) return
        setData()
        setupActionBack()
        binding.btnToHistory.setOnClickListener {
            findNavController().navigate(ResultScanFragmentDirections.actionResultScanFragmentToHistoryFragment())
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

    private fun setData(isResume: Boolean = false) {
        val skinDisease = ResultScanFragmentArgs.fromBundle(requireArguments()).skinDisease
        val image = ResultScanFragmentArgs.fromBundle(requireArguments()).imageScan
        val accuracySkinDisease = ResultScanFragmentArgs.fromBundle(requireArguments()).accuracySkinDisease
        val convertToPercent = (accuracySkinDisease * 100).toInt().toString()
        val cornerRadius = 20
        binding.apply {
            nameDiseaseScan.text = skinDisease
            accuracyDiseaseScan.text = resources.getString(R.string.accuracy, "$convertToPercent%")
            Glide.with(requireActivity())
                .load(image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(cornerRadius)))
                .into(imageResultScan)
        }
        if (!isResume) {
            saveSkinDisease(skinDisease, image, accuracySkinDisease)
        }
    }

    private fun saveSkinDisease(skinDisease: String, image: String, accuracy: Float) {
        val currentTime = System.currentTimeMillis()
        val newHistory = History(
            nameSkinDisease = skinDisease,
            photo = image,
            accuracy = accuracy,
            createdAt = currentTime
        )
        CoroutineScope(Dispatchers.IO).launch {
            viewModelResultScan.insertData(newHistory)
        }
    }

    override fun onResume() {
        super.onResume()
        setData(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}