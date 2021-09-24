package com.github.ivanvatov.githubsearch.repository.model

import kotlinx.serialization.Serializable

@Serializable
class GitHubRepository(
    val name: String,
    val owner: Owner,
    val html_url: String,
    val description: String? = null
) {

    @Serializable
    class Owner(
        val login: String,
        val avatar_url: String
    )

}