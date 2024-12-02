package com.example.berongsok.ui.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.databinding.FragmentScanBinding
import com.example.berongsok.ui.transaction.TransactionFormActivity
import com.example.berongsok.utils.Injection
import com.example.berongsok.utils.getImageUri
import com.example.berongsok.utils.uriToFile

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private var currentImageUri: Uri? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireActivity(), "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireActivity(), "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val dataStore = requireContext().applicationContext.dataStore

        val scanViewModel: ScanViewModel by viewModels {
            ScanViewModelFactory(SettingPreferences.getInstance(dataStore), Injection.provideUserRepository())
        }

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTitle

        scanViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.uploadBtn.setOnClickListener{
            val imageUri = currentImageUri

            Log.d("Image Post", "imageURI: $currentImageUri")

            if (imageUri != null) {
                showLoading(true)
                scanViewModel.uploadStory(imageUri, requireActivity())
            } else {
                showToast("All fields are required")
                showLoading(false)
            }

        }

//        scanViewModel.uploadResult.observe(requireActivity()) { result ->
//            result.onSuccess { response ->
//                if (response.status == "success") {
//                    showLoading(false)
//                    Log.d("Predict Response", "Predict Result: ${response.result}")
//                    Log.d("Predict Response", "Confidence Score Result: ${response.result.score}")
//                    Log.d("Predict Response", "Price: ${response.result.score}")
//                    val intent = Intent(context, TransactionFormActivity::class.java).apply {
//                        putExtra(TransactionFormActivity.EXTRA_PREDICT_RESULT, response.result.result)
//                        putExtra(TransactionFormActivity.EXTRA_PREDICT_SCORE, response.result.score)
//                        putExtra(TransactionFormActivity.EXTRA_PREDICT_PRICE, response.result.price)
//                        putExtra(TransactionFormActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
//                    }
//                    startActivity(intent)
//                } else {
//                    showLoading(false)
//                    showToast(response.status)
//                }
//            }
//            result.onFailure { throwable ->
//                throwable.localizedMessage?.let {
//                    showErrorDialog(it)
//                    showLoading(false)
//                }
//            }
//        }

        scanViewModel.uploadResult.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { result ->
                result.onSuccess { response ->
                    if (response.status == "success") {
                        showLoading(false)
                        val intent = Intent(context, TransactionFormActivity::class.java).apply {
                            putExtra(TransactionFormActivity.EXTRA_PREDICT_RESULT, response.result.result)
                            putExtra(TransactionFormActivity.EXTRA_PREDICT_SCORE, response.result.score)
                            putExtra(TransactionFormActivity.EXTRA_PREDICT_PRICE, response.result.price)
                            putExtra(TransactionFormActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
                        }
                        startActivity(intent)
                    } else {
                        showLoading(false)
                        showToast(response.status)
                    }
                }
                result.onFailure { throwable ->
                    throwable.localizedMessage?.let {
                        showErrorDialog(it)
                        showLoading(false)
                    }
                }
            }
        }


        binding.openCameraBtn.setOnClickListener { openCamera() }

        binding.openGaleryBtn.setOnClickListener { openGallery() }

        return root
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun openCamera() {
        currentImageUri = getImageUri(requireActivity())
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireActivity())
            .setTitle("Upload failed")
            .setMessage(message)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        val dataStore = requireContext().applicationContext.dataStore

        val scanViewModel: ScanViewModel by viewModels {
            ScanViewModelFactory(SettingPreferences.getInstance(dataStore), Injection.provideUserRepository())
        }
        scanViewModel.uploadResult.removeObservers(viewLifecycleOwner)
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}

class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}