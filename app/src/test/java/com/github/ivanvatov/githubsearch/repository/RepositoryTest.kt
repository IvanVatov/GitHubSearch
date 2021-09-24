package com.github.ivanvatov.githubsearch.repository

import com.github.ivanvatov.githubsearch.api.BackendApi
import com.github.ivanvatov.githubsearch.api.mockHttpClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class RepositoryTest {

    /**
     * Test parsing the response search and loading next page
     */
    @Test
    fun test() = runBlocking {

        val repo = Repository(BackendApi(mockHttpClient))
        val searchResult = repo.search("api")

        // From configuration we request 15 items from the api
        Assert.assertEquals(15, searchResult.size)

        // chek that the response is parsed as expected
        val firstItem = searchResult.first()
        Assert.assertEquals("api", firstItem.name)
        Assert.assertEquals("dingo", firstItem.owner.login)
        Assert.assertEquals("https://avatars.githubusercontent.com/u/4798539?v=4", firstItem.owner.avatar_url)
        Assert.assertEquals("https://github.com/dingo/api", firstItem.html_url)
        Assert.assertEquals("A RESTful API package for the Laravel and Lumen frameworks.", firstItem.description)

        // here we request the second page of the result
        val theSecondPage = repo.nextPage()

        // it should be next 15 items
        Assert.assertEquals(15, theSecondPage.size)

        //check it's different result
        val sixteenthItem = theSecondPage.first()
        Assert.assertEquals("https://github.com/pushshift/api", sixteenthItem.html_url)
    }
}