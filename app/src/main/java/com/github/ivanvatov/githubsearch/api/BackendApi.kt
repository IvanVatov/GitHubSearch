package com.github.ivanvatov.githubsearch.api

import com.github.ivanvatov.githubsearch.Configuration
import com.github.ivanvatov.githubsearch.api.response.SearchResponse
import com.github.ivanvatov.githubsearch.repository.model.GitHubRepository
import io.ktor.client.*
import io.ktor.client.request.*

class BackendApi(private val httpClient: HttpClient) {

    suspend fun searchRepository(searchQuery: String, page: Int? = null): List<GitHubRepository> {

        val response =
            httpClient.get<SearchResponse>("${Configuration.GITHUB_API_BASE_URL}/search/repositories") {
                if (Configuration.GITHUB_TOKEN.isNotBlank()) {
                    header("Authorization", "token ${Configuration.GITHUB_TOKEN}")
                }
                parameter("q", searchQuery)
                parameter("per_page", Configuration.SEARCH_PAGE_SIZE)
                page?.let {
                    parameter("page", it)
                }
            }

        return response.items
    }

}