package com.github.ivanvatov.githubsearch.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ivanvatov.githubsearch.repository.Repository
import com.github.ivanvatov.githubsearch.repository.model.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchActivityViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    val items: MutableLiveData<List<GitHubRepository>> = MutableLiveData(emptyList())

    val error: MutableLiveData<Throwable?> = MutableLiveData(null)

    val itemsCount: Int get() = items.value?.size ?: 0


    fun search(searchTerm: String) {

        isLoading.postValue(true)
        items.postValue(emptyList())

        viewModelScope.launch {
            try {

                val result = repository.search(searchTerm)

                items.postValue(result)

            } catch (e: Throwable) {

                error.postValue(e)
            } finally {

                isLoading.postValue(false)
            }
        }
    }


    fun loadNextPage() {

        isLoading.postValue(true)

        viewModelScope.launch {
            try {

                val result = repository.nextPage()

                val addedResult = items.value?.toMutableList()?.also {
                    it.addAll(result)
                } ?: emptyList()

                items.postValue(addedResult)

            } catch (e: Throwable) {

                error.postValue(e)
            } finally {

                isLoading.postValue(false)
            }
        }
    }
}