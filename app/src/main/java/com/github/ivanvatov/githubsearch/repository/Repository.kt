package com.github.ivanvatov.githubsearch.repository

import com.github.ivanvatov.githubsearch.api.BackendApi
import com.github.ivanvatov.githubsearch.repository.model.GitHubRepository

class Repository(private val api: BackendApi) {

    private var searchQuery: String? = null
    private var page: Int = 1

    suspend fun search(searchQuery: String): List<GitHubRepository> {

        this.searchQuery = searchQuery
        this.page = 1

        return api.searchRepository(searchQuery)
    }

    suspend fun nextPage(): List<GitHubRepository> {

        return searchQuery?.let {

            api.searchRepository(it, ++page)
        } ?: emptyList()
    }
}