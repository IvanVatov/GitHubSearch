package com.github.ivanvatov.githubsearch

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.serialization.json.Json

@HiltAndroidApp
class GitHubSearch : Application()

val jsonInstance = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}