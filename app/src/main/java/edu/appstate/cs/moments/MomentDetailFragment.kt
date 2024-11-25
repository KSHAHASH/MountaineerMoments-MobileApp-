package edu.appstate.cs.moments

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.appstate.cs.moments.databinding.FragmentMomentDetailBinding
import edu.appstate.cs.moments.databinding.ListItemPhotoBinding
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

class MomentDetailFragment: Fragment() {
    private var _binding: FragmentMomentDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: MomentDetailFragmentArgs by navArgs()

    private val momentDetailViewModel: MomentDetailViewModel by viewModels {
        MomentDetailViewModelFactory(args.momentId)
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto) {
            updatePhotos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentMomentDetailBinding.inflate(inflater, container, false)
        binding.pictureList.layoutManager = GridLayoutManager(context, 3)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            momentTitle.doOnTextChanged { text, _, _, _ ->
                momentDetailViewModel.updateMoment { oldMoment ->
                    oldMoment.copy(title = text.toString())
                }
            }

            momentDescription.doOnTextChanged { text, _, _, _ ->
                momentDetailViewModel.updateMoment { oldMoment ->
                    oldMoment.copy(description = text.toString())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                momentDetailViewModel.moment.collect { moment ->
                    moment?.let { updateUi(it) }
                }
            }
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            momentDetailViewModel.updateMoment { it.copy(timestamp = newDate) }
        }

        setFragmentResultListener(
            TimePickerFragment.REQUEST_KEY_TIME
        ) { _, bundle ->
            val newDate = bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date
            momentDetailViewModel.updateMoment { it.copy(timestamp = newDate) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_moment_detail, menu)

        val captureImageIntent = takePhoto.contract.createIntent(
            requireContext(),
            Uri.parse("")
        )
        val item: MenuItem = menu.findItem(R.id.take_picture)
        item.isEnabled = canResolveIntent(captureImageIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_moment -> {
                deleteCurrentMoment()
                true
            }
            R.id.share_moment -> {
                shareMoment()
                true
            }
            R.id.take_picture -> {
                takePicture()
                true
            }
            R.id.share_with_api -> {
                shareMomentWithAPI()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteCurrentMoment() {
        momentDetailViewModel.deleteMoment()
        findNavController().navigateUp()
    }

    private fun updateUi(moment: Moment) {
        binding.apply {
            if (momentTitle.text.toString() != moment.title) {
                momentTitle.setText(moment.title)
            }
            if (momentDescription.text.toString() != moment.description) {
                momentDescription.setText(moment.description)
            }
            momentDate.text = DateFormat.format("MM dd yyyy", moment.timestamp)
            momentTime.text = DateFormat.format("HH:mm:ss z", moment.timestamp)
            momentDate.setOnClickListener {
                findNavController().navigate(
                    MomentDetailFragmentDirections.selectDate(moment.timestamp)
                )
            }
            momentTime.setOnClickListener {
                findNavController().navigate(
                    MomentDetailFragmentDirections.selectTime(moment.timestamp)
                )
            }
        }

        updatePhotos()
    }

    private fun shareMoment() {
        momentDetailViewModel.moment.value?.let {
            val reportIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, it.description)
                putExtra(Intent.EXTRA_SUBJECT, it.title)
            }

            val chooserIntent = Intent.createChooser(
                reportIntent,
                getString(R.string.share_via)
            )

            startActivity(chooserIntent)
        }
    }

    private fun shareMomentWithAPI() {
        momentDetailViewModel.shareMomentUsingAPI()
        momentDetailViewModel.moment.value?.let { moment ->
            photosForMoment(moment).forEach {
                momentDetailViewModel.shareMomentFile(it)
            }
        }
    }

    private fun takePicture() {
        momentDetailViewModel.moment.value?.let { moment ->
            val currentPhotos = photosForMoment(moment)
            val photoName = "${moment.id}_${currentPhotos.size}.JPG"
            val photoFile = File(requireContext().applicationContext.filesDir, photoName)
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "edu.appstate.cs.moments.fileprovider",
                photoFile
            )
            takePhoto.launch(photoUri)
        }
    }

    private fun updatePhotos() {
        momentDetailViewModel.moment.value?.let { moment ->
            binding.pictureList.adapter = PhotoAdapter(photosForMoment(moment))
        }
    }

    private fun photosForMoment(moment: Moment): List<File> {
        val photosDir = requireContext().applicationContext.filesDir
        val photos = photosDir.listFiles()
        val fileList = photos?.let { files ->
            files.filter { f ->
                f.exists() && f.isFile && f.name.startsWith(moment.id.toString())
            }
        } ?: emptyList()

        return fileList
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }

    class PhotoHolder(
        private val binding: ListItemPhotoBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(photoPath: File) {
            binding.itemImageView.load(
                photoPath
            )
        }
    }

    class PhotoAdapter(
        private val photoFiles: List<File>
    ): RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemPhotoBinding.inflate(inflater, parent, false)
            return PhotoHolder(binding)
        }

        override fun getItemCount() = photoFiles.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.bind(photoFiles[position])
        }
    }
}