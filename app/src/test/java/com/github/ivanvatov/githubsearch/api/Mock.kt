package com.github.ivanvatov.githubsearch.api

import com.github.ivanvatov.githubsearch.jsonInstance
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.ktor.utils.io.*
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader


private val engine = MockEngine { request ->

    val filePath = when (request.url.parameters["page"]) {
        null, "1" -> "/search/search"
        "2" -> "/search/nextPage"
        else -> throw NotImplementedError()
    }

    respond(
        content = ByteReadChannel(getJsonResult(filePath)),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
}

private const val RESOURCES_PATH = "./src/test/res"
private const val FILE_EXTENSION = ".json"

private fun getJsonResult(filePath: String): String {

    println("Read File")
    val fullFilePath =
        StringBuilder(RESOURCES_PATH).append(filePath).append(FILE_EXTENSION).toString()

    val br = BufferedReader(InputStreamReader(FileInputStream(fullFilePath)))
    val sb = StringBuilder()
    br.forEachLine { sb.append(it) }
    println("KUR")
    return sb.toString()
}

val mockHttpClient = HttpClient(engine) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(jsonInstance)
    }
}
