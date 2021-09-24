package com.github.ivanvatov.githubsearch.api.response

import com.github.ivanvatov.githubsearch.repository.model.GitHubRepository
import kotlinx.serialization.Serializable

@Serializable
class SearchResponse(
    val items: List<GitHubRepository>
)