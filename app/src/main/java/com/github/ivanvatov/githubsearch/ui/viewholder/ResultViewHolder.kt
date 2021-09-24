package com.github.ivanvatov.githubsearch.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.ivanvatov.githubsearch.R
import com.github.ivanvatov.githubsearch.databinding.ResultViewBinding
import com.github.ivanvatov.githubsearch.repository.model.GitHubRepository

class ResultViewHolder(private val binding: ResultViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GitHubRepository) {

        binding.apply {

            binding.root.tag = item

            Glide.with(binding.imageOwner).load(item.owner.avatar_url).circleCrop()
                .placeholder(R.drawable.github).into(binding.imageOwner)
            textOwner.text = item.owner.login
            textName.text = item.name
            textDescription.text = item.description
        }
    }

    companion object {

        const val VIEW_TYPE = 1
    }
}