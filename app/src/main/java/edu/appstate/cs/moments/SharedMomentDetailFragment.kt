package edu.appstate.cs.moments

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.appstate.cs.moments.databinding.FragmentSharedMomentDetailBinding
import edu.appstate.cs.moments.databinding.ListItemPhotoBinding
import kotlinx.coroutines.launch
import java.io.File
class SharedMomentDetailFragment : Fragment() {
    private var _binding: FragmentSharedMomentDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: SharedMomentDetailFragmentArgs by navArgs() // Safe Args for passing momentId
    private val sharingRepository = SharingRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentSharedMomentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Fetch and set photos
        viewLifecycleOwner.lifecycleScope.launch {
            // Getting the Moment argument and updating the UI
            val moment = args.moment
            updateUi(moment)
            val imageUrls = sharingRepository.sharedMomentsImages(moment.id)
            // for debugging...
                Log.d("SharedMomentDetail", "Calling sharedMomentsPics for momentId: ${moment.id}")
                Log.d("SharedMomentDetail", "Fetched URLs: $imageUrls")

            binding.pictureList.adapter = SharedPhotoAdapter(imageUrls)
            binding.pictureList.layoutManager = GridLayoutManager(context, 3)

            // for debugging...
                Log.d("SharedMomentDetail", "Image URLs fetched: $imageUrls")
                Log.d("SharedMomentDetail", "Adapter set with item count: ${imageUrls.size}")
        }
    }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        private fun updateUi(moment: Moment) {
            binding.apply {
                // Display the Moment's details
                momentTitle.text = moment.title
                momentDescription.text = moment.description
                momentDate.text = DateFormat.format("MM dd yyyy", moment.timestamp)
                momentTime.text = DateFormat.format("HH:mm:ss z", moment.timestamp)
                postedBy.text = moment.postedBy

                // Disable the fields to make them read-only
                momentTitle.isEnabled = false
                momentDescription.isEnabled = false
                momentDate.isEnabled = false
                momentTime.isEnabled = false
            }
        }


}

class SharedPhotoHolder(private val binding: ListItemPhotoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(photoUrl: String) {
        binding.itemImageView.load(photoUrl)
    }
}

class SharedPhotoAdapter(private val photoUrls: List<String>) :
    RecyclerView.Adapter<SharedPhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedPhotoHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemPhotoBinding.inflate(inflater, parent, false)
        return SharedPhotoHolder(binding)
    }

    override fun getItemCount(): Int = photoUrls.size

    override fun onBindViewHolder(holder: SharedPhotoHolder, position: Int) {
        holder.bind(photoUrls[position])
    }
}