package com.ravi.skinhealthyai.ui.scan

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.ravi.skinhealthyai.utils.ImageHelper
import java.io.File

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
        val image = ResultScanFragmentArgs.fromBundle(requireArguments()).imageScan.toUri()
        val accuracySkinDisease = ResultScanFragmentArgs.fromBundle(requireArguments()).accuracySkinDisease
        val convertToPercent = (accuracySkinDisease * 100).toInt().toString()
        val cornerRadius = 20
        Log.d("data kirm", image.toString())
        binding.apply {
            nameDiseaseScan.text = skinDisease
            accuracyDiseaseScan.text = resources.getString(R.string.accuracy, "$convertToPercent%")
            Glide.with(requireActivity())
                .load(image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(cornerRadius)))
                .into(imageResultScan)
        }
        if (!isResume) saveSkinDisease(skinDisease, image, accuracySkinDisease)
    }

    private fun saveSkinDisease(skinDisease: String, imageUri: Uri?, accuracy: Float) {
        if (imageUri == null) return

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val imagePath =
                ImageHelper.copyAndCompressImageFromUri(requireContext(), imageUri, quality = 80)
                    ?: return@launch

            val newHistory = History(
                nameSkinDisease = skinDisease,
                photo = imagePath,
                accuracy = accuracy,
                createdAt = System.currentTimeMillis()
            )

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