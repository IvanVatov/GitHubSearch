package com.github.ivanvatov.githubsearch.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ivanvatov.githubsearch.Configuration
import com.github.ivanvatov.githubsearch.R
import com.github.ivanvatov.githubsearch.databinding.LoadingViewBinding
import com.github.ivanvatov.githubsearch.databinding.ResultViewBinding
import com.github.ivanvatov.githubsearch.databinding.SearchActivityBinding
import com.github.ivanvatov.githubsearch.repository.model.GitHubRepository
import com.github.ivanvatov.githubsearch.ui.viewholder.LoadingViewHolder
import com.github.ivanvatov.githubsearch.ui.viewholder.ResultViewHolder
import com.github.ivanvatov.githubsearch.viewmodel.SearchActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: SearchActivityBinding? = null

    private val viewModel: SearchActivityViewModel by viewModels()

    private val isLoadingObserver by lazy { viewModel.isLoading.observe(this) { notifyLoading() } }

    private val itemsObserver by lazy { viewModel.items.observe(this) { notifyItemsChanged() } }

    private val errorObserver by lazy {
        viewModel.error.observe(this) { nullable ->
            nullable?.let {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                viewModel.error.postValue(null)
            }
        }
    }


    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (dy > 0) {
                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                if (viewModel.isLoading.value == false && lastVisibleItemPosition >= recyclerViewItemsCount - 1) {
                    viewModel.loadNextPage()
                }
            }
        }
    }

    private val recyclerViewItemsCount get() = binding?.recyclerView?.layoutManager?.itemCount ?: 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = SearchActivityBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        binding?.recyclerView?.apply {

            addItemDecoration(object : RecyclerView.ItemDecoration() {

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val space = view.context.resources.displayMetrics.density * 4
                    outRect.bottom = space.toInt()
                }
            })

            addOnScrollListener(scrollListener)

            adapter = SearchResultAdapter()

            layoutManager = LinearLayoutManager(this@SearchActivity)
        }

        handleIntent(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.search_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        (menu.findItem(R.id.search).actionView as SearchView).apply {

            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        return true
    }


    override fun onStart() {
        super.onStart()

        // invoke observers in order to be initialized
        isLoadingObserver
        itemsObserver
        errorObserver
    }


    override fun onClick(view: View) {
        (view.tag as? GitHubRepository)?.let {

            // Open in browser
            val openInBrowserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.html_url))
            if (openInBrowserIntent.resolveActivity(packageManager) != null) {

                startActivity(openInBrowserIntent)
            }
        }
    }


    override fun onNewIntent(intent: Intent) {

        super.onNewIntent(intent)

        handleIntent(intent)
    }


    private fun handleIntent(intent: Intent) {

        if (Intent.ACTION_SEARCH == intent.action) {

            intent.getStringExtra(SearchManager.QUERY)?.let {

                doSearch(it)
            }
        }
    }


    private fun doSearch(searchTerm: String) {

        Log.d("GitHubSearch", "Searching for: $searchTerm")

        val trimmed = searchTerm.trim()

        if (trimmed.isBlank()) return

        viewModel.search(trimmed)
    }


    private fun notifyLoading() {

        if (viewModel.isLoading.value == true) {

            binding?.recyclerView?.adapter?.notifyItemInserted(recyclerViewItemsCount)
        }
    }

    private fun notifyItemsChanged() {

        if (viewModel.itemsCount <= Configuration.SEARCH_PAGE_SIZE) {

            binding?.recyclerView?.adapter?.notifyDataSetChanged()
            return
        }

        binding?.recyclerView?.adapter?.notifyItemRangeChanged(
            recyclerViewItemsCount - 1,
            viewModel.itemsCount
        )
    }

    // region Adapter
    /**
     * We are using inner adapter in order to access the viewModel directly
     * without providing it as a dependency.
     */
    inner class SearchResultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        init {

            stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val inflater = LayoutInflater.from(parent.context)

            return when (viewType) {

                LoadingViewHolder.VIEW_TYPE -> LoadingViewHolder(
                    LoadingViewBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )

                ResultViewHolder.VIEW_TYPE -> ResultViewHolder(
                    ResultViewBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                ).apply {
                    itemView.setOnClickListener(
                        this@SearchActivity
                    )
                }

                else -> throw NotImplementedError("View Type $viewType is not implemented")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            if (position >= viewModel.itemsCount) return

            viewModel.items.value?.get(position)?.let {

                (holder as? ResultViewHolder)?.bind(it)
            }
        }

        override fun getItemCount(): Int {

            var count = viewModel.itemsCount

            if (viewModel.isLoading.value == true) {

                count++
            }

            return count
        }

        override fun getItemViewType(position: Int): Int {

            if (position >= viewModel.itemsCount) {

                return LoadingViewHolder.VIEW_TYPE
            }

            return ResultViewHolder.VIEW_TYPE
        }
    }
    // endregion
}