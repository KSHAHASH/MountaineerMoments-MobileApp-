package edu.appstate.cs.moments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import edu.appstate.cs.moments.databinding.ListItemMomentBinding
import edu.appstate.cs.moments.databinding.ListItemSharedMomentBinding
import java.text.SimpleDateFormat
import java.util.UUID

abstract class MomentBaseHolder(
    private val binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(moment: Moment)
}

class MomentHolder(
    private val binding: ListItemMomentBinding,
    private val onMomentClicked: (momentId: UUID) -> Unit
) : MomentBaseHolder(binding) {

    override fun bind(moment: Moment) {
        binding.momentTitle.text = moment.title
        binding.momentDescription.text = moment.description
        binding.momentTimestamp.text = SimpleDateFormat.getDateTimeInstance().format(moment.timestamp)

        binding.root.setOnClickListener {
            onMomentClicked(moment.id)
        }
    }
}

class SharedMomentHolder(
    private val binding: ListItemSharedMomentBinding,
    private val onSharedMomentClicked: (momentId: UUID) -> Unit // TODO: What should the type of this be?
) : MomentBaseHolder(binding) {

    override fun bind(moment: Moment) {
        binding.momentTitle.text = moment.title
        binding.momentPostedBy.text = moment.postedBy
        binding.momentDescription.text = moment.description
        binding.momentTimestamp.text = SimpleDateFormat.getDateTimeInstance().format(moment.timestamp)

        binding.root.setOnClickListener {
            // TODO: What should we pass here?
            onSharedMomentClicked(moment.id)
        }
    }
}

class MomentsListAdapter(
    private val moments: List<Moment>,
    private val onMomentClicked: (momentId: UUID) -> Unit,
    private val onSharedMomentClicked: (momentId: UUID) -> Unit
) : RecyclerView.Adapter<MomentBaseHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentBaseHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == 0) {
            val binding = ListItemMomentBinding.inflate(inflater, parent, false)
            return MomentHolder(binding, onMomentClicked)
        } else {
            val binding = ListItemSharedMomentBinding.inflate(inflater, parent, false)
            return SharedMomentHolder(binding, onSharedMomentClicked)
        }
    }

    override fun getItemCount() = moments.size

    override fun onBindViewHolder(holder: MomentBaseHolder, position: Int) {
        val moment = moments[position]
        if (holder is SharedMomentHolder) {
            holder.bind(moment)
        } else {
            holder.bind(moment)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (moments[position].fromAPI) {
            true -> 1
            false -> 0
        }
    }
}