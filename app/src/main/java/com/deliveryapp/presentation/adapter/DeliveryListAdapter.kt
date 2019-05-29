package com.deliveryapp.presentation.adapter


import android.arch.paging.PagedListAdapter
import android.databinding.ObservableBoolean
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.deliveryapp.databinding.ItemDeliveryBinding
import com.deliveryapp.databinding.ItemProgressbarBinding
import com.deliveryapp.domain.entity.Delivery
import kotlinx.android.synthetic.main.item_progressbar.view.*


private const val VIEW_TYPE_LOADING: Int = 1

/**
 * DeliveryListAdapter is used to display data for each delivery in the list
 */
class DeliveryListAdapter :
    PagedListAdapter<Delivery, RecyclerView.ViewHolder>(deliveriesDiffCallback) {
    lateinit var onItemClickListener: ((item: Any) -> Unit)
    lateinit var onLoadMoreClickListener: (() -> Unit)
    private var isLoading = ObservableBoolean(false)
    private var isLoadMore = ObservableBoolean(false)

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount())
            super.getItemViewType(position)
        else VIEW_TYPE_LOADING
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (isLoading.get() || isLoadMore.get()) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_LOADING) {
            ProgressViewHolder(
                ItemProgressbarBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), onLoadMoreClickListener
            )
        } else DeliveryItemHolder(
            ItemDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false), onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payload: MutableList<Any>) {
        if (payload.isNotEmpty()) {
            (holder as DeliveryItemHolder).bind(getItem(position) as Delivery)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) != VIEW_TYPE_LOADING) {
            (holder as DeliveryItemHolder).bind(getItem(position) as Delivery)
        } else {
            (holder as ProgressViewHolder).bind(isLoading.get(), isLoadMore.get())
        }
    }

    companion object {
        val deliveriesDiffCallback = object : DiffUtil.ItemCallback<Delivery>() {
            override fun areItemsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ProgressViewHolder(
        val binding: ItemProgressbarBinding,
        private val onLoadMoreClickListener: (() -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(isLoading: Boolean, isLoadingMore: Boolean) {
            binding.root.btnLoadMore.setOnClickListener { onLoadMoreClickListener?.invoke() }
            binding.loading = isLoading
            binding.loadMore = isLoadingMore
        }
    }

    class DeliveryItemHolder(
        val binding: ItemDeliveryBinding,
        private val onItemClickListener: ((item: Any) -> Unit)?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var delivery: Delivery

        fun bind(delivery: Delivery) {
            this.delivery = delivery

            binding.viewModel = delivery
            binding.root.setOnClickListener { onItemClickListener?.invoke(delivery) }
        }
    }

    fun getDeliveryItem(position: Int): Delivery? {
        return super.getItem(position)
    }

    fun setLoading(loading: Boolean, loadMore: Boolean) {
        isLoading.set(loading)
        isLoadMore.set(loadMore)
    }
}