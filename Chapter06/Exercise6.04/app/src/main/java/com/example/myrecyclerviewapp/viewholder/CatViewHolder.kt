package com.example.myrecyclerviewapp.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.myrecyclerviewapp.ImageLoader
import com.example.myrecyclerviewapp.R
import com.example.myrecyclerviewapp.model.CatBreed
import com.example.myrecyclerviewapp.model.CatUiModel
import com.example.myrecyclerviewapp.model.Gender
import com.example.myrecyclerviewapp.model.ListItemUiModel

private const val FEMALE_SYMBOL = "\u2640"
private const val MALE_SYMBOL = "\u2642"
private const val UNKNOWN_SYMBOL = "?"

class CatViewHolder(
    private val containerView: View,
    private val imageLoader: ImageLoader,
    private val onClickListener: OnClickListener
) : ListItemViewHolder(containerView) {
    private val catBiographyView: TextView
        by lazy { containerView.findViewById(R.id.item_cat_biography) }
    private val catBreedView: TextView
        by lazy { containerView.findViewById(R.id.item_cat_breed) }
    private val catGenderView: TextView
        by lazy { containerView.findViewById(R.id.item_cat_gender) }
    private val catNameView: TextView
        by lazy { containerView.findViewById(R.id.item_cat_name) }
    private val catPhotoView: ImageView
        by lazy { containerView.findViewById(R.id.item_cat_photo) }

    override fun bindData(listItem: ListItemUiModel) {
        require(listItem is ListItemUiModel.Cat) { "Expected ListItemUiModel.Cat" }
        val catData = listItem.data

        containerView.setOnClickListener { onClickListener.onClick(catData) }
        imageLoader.loadImage(catData.imageUrl, catPhotoView)
        catNameView.text = catData.name
        catBreedView.text = when (catData.breed) {
            CatBreed.AmericanCurl -> "American Curl"
            CatBreed.BalineseJavanese -> "Balinese-Javanese"
            CatBreed.ExoticShorthair -> "Exotic Shorthair"
        }
        catBiographyView.text = catData.biography
        catGenderView.text = when (catData.gender) {
            Gender.Female -> FEMALE_SYMBOL
            Gender.Male -> MALE_SYMBOL
            else -> UNKNOWN_SYMBOL
        }
    }

    interface OnClickListener {
        fun onClick(catData: CatUiModel)
    }
}
