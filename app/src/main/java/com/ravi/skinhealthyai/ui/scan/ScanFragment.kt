package com.ravi.skinhealthyai.ui.scan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ravi.skinhealthyai.R
import com.ravi.skinhealthyai.databinding.FragmentScanBinding
import com.ravi.skinhealthyai.ui.ScanViewModelFactory
import com.ravi.skinhealthyai.ui.camera.CameraActivity
import com.yalantis.ucrop.UCrop
import java.io.File

class ScanFragment : Fragment() {

    private var currentImageUri: Uri? = null
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var loadingDialog: AlertDialog? = null

    private lateinit var scanViewModel: ScanViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            val message = if (isGranted) "Permission granted" else "Permission denied"
            showToast(message)
        }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            startUCrop(it)
        } ?: showToast(getString(R.string.no_media_selected))
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            currentImageUri?.let { uri -> startUCrop(uri) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        val factory = ScanViewModelFactory(requireActivity().application)
        scanViewModel = ViewModelProvider(this, factory)[ScanViewModel::class.java]

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnFromGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.scanButton.setOnClickListener { startScan() }

        binding.scanButton.isEnabled = false

        binding.topAppBar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        scanViewModel.classificationResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.firstOrNull()?.categories
                ?.maxByOrNull { it?.score ?: 0f }
                ?.let { category ->
                    val action = ScanFragmentDirections
                        .actionNavigationScanToNavigationResultScan(
                            category.label,
                            currentImageUri.toString(),
                            category.score
                        )
                    findNavController().navigate(action)
                }
        }

        scanViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
            } else {
                dismissLoadingDialog()
            }
        }
    }

    private fun startScan() {
        currentImageUri?.let {
            scanViewModel.classifyImage(it)
        } ?: showToast("No image selected.")
    }

    @Suppress("DEPRECATION")
    private fun startUCrop(uri: Uri) {
        val destinationFileName = "${System.currentTimeMillis()}.jpg"
        val destinationDir = File(requireContext().cacheDir, "cropped_images")
        if (!destinationDir.exists()) destinationDir.mkdirs()

        val destinationFile = File(destinationDir, destinationFileName)
        val destinationUri = Uri.fromFile(destinationFile)

        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(100)
        }

        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1080, 1080) // <── resize output supaya tidak out of memory
            .withOptions(options)
            .getIntent(requireContext()).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }.also {
                startActivityForResult(it, UCrop.REQUEST_CROP)
            }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(requireContext(), REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    private fun showImage() {
        currentImageUri?.let {
            binding.imgBox.background = null
            binding.iconImage.visibility = View.GONE
            binding.scanButton.isEnabled = true

            Glide.with(requireContext())
                .load(it)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .into(binding.imgBox)
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            currentImageUri = resultUri
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(data!!)
            showToast("Crop error: ${error?.localizedMessage}")
        }
    }

    private fun showToast(message: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoadingDialog(){
        if (loadingDialog == null) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_loading, null)
            loadingDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create()
        }
        loadingDialog?.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onResume() {
        super.onResume()
        showImage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissLoadingDialog() // pastikan dialog ditutup
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val CAMERAX_RESULT = 200
    }
}

