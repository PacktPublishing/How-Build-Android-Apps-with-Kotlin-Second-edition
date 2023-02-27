package com.example.myrecyclerviewapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecyclerviewapp.model.CatUiModel
import com.example.myrecyclerviewapp.model.ListItemUiModel
import com.example.myrecyclerviewapp.viewholder.CatViewHolder
import com.example.myrecyclerviewapp.viewholder.ListItemViewHolder
import com.example.myrecyclerviewapp.viewholder.TitleViewHolder

private const val VIEW_TYPE_TITLE = 0
private const val VIEW_TYPE_CAT = 1

class ListItemsAdapter(
    private val layoutInflater: LayoutInflater,
    private val imageLoader: ImageLoader,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<ListItemViewHolder>() {
    private val listData = mutableListOf<ListItemUiModel>()

    fun setData(newListData: List<ListItemUiModel>) {
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_TITLE -> {
            val view = layoutInflater.inflate(R.layout.item_title, parent, false)
            TitleViewHolder(view)
        }
        VIEW_TYPE_CAT -> {
            val view = layoutInflater.inflate(R.layout.item_cat, parent, false)
            CatViewHolder(
                view,
                imageLoader,
                object : CatViewHolder.OnClickListener {
                    override fun onClick(catData: CatUiModel) =
                        onClickListener.onItemClick(catData)
                })
        }
        else -> throw IllegalArgumentException("Unknown view type requested: $viewType")
    }

    override fun getItemCount() = listData.size

    override fun getItemViewType(position: Int) = when (listData[position]) {
        is ListItemUiModel.Title -> VIEW_TYPE_TITLE
        is ListItemUiModel.Cat -> VIEW_TYPE_CAT
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        holder.bindData(listData[position])
    }

    interface OnClickListener {
        fun onItemClick(catData: CatUiModel)
    }
}
