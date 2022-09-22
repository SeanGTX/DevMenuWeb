package ru.megboyzz.application

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
//import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*

import org.slf4j.event.Level
import java.io.FileNotFoundException
import kotlin.concurrent.thread

//TODO Реализовать интерфейс предоставляющий реализацию методов api

//Контекст запуска
private fun main() {

    val internalBasePath = "D:\\"
    val externalBasePath = "D:\\DevMenuWebTest\\external"

    start(8080, PlatformImpl(), internalBasePath, externalBasePath)
}

private fun ApplicationCall.getParams(param: String): String? {
    return request.queryParameters[param]
}

private fun ApplicationCall.containsParam(param: String): Boolean{
    return this.request.queryParameters.contains(param)
}

private suspend fun ApplicationCall.file(api: PlatformAPI, basePath: String){
    val its = this

    this.getParams("folder")
    .runCatching {
        if(its.containsParam("info"))
            its.respond(api.getInfo(this!!, basePath))
        else
            its.respond(api.getFile(this!!, basePath))
    }
    .onFailure {
        System.err.println(it)

        when(it){
            is FileNotFoundException -> its.respond(HttpStatusCode.NotFound)
            is NullPointerException -> its.respond(HttpStatusCode.BadRequest)
        }

    }
    .onSuccess {
        its.respond(HttpStatusCode.OK)
    }
}

private suspend fun ApplicationCall.remove(api: PlatformAPI, basePath: String){
    this
        .getParams("folder")
        .runCatching {
            api.removeFile(this!!, basePath)
        }
        .onFailure {
            this.respond(HttpStatusCode.NotFound)
        }
        .onSuccess { this.respond(HttpStatusCode.OK) }
}



private fun HTML.index() {
    head { title("DevMenu") }
    body {
        div { id = "root" }
        script(src = "/static/DevMenuWeb.js") {}
    }
}

fun start(port: Int, api: PlatformAPI, internalPath: String, externalPath: String){
    thread {
        embeddedServer(CIO, port = port) {

            install(DefaultHeaders) { header(HttpHeaders.Server, "From DevMenuServer With Love!") }

            install(CallLogging) { level = Level.INFO }

            install(ContentNegotiation){ json() }

            install(Compression) { gzip() }



            routing {
                get("/") {
                    call.respondHtml(HttpStatusCode.OK, HTML::index)
                }
                static("/static") { resources() }

                route("/files"){
                    route("/internal"){
                        get{ call.file(api, internalPath)}
                        delete {call.remove(api, internalPath) }
                    }
                    route("/external"){
                        get{call.file(api, externalPath)}
                        delete {call.remove(api, externalPath) }
                    }
                }

            }
        }.start(wait = true)
    }
}