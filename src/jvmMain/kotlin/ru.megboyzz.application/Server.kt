package ru.megboyzz.application

import entities.GameLanguage
import entities.GameLanguageType
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.streams.*
import kotlinx.html.*

import org.slf4j.event.Level
import java.io.FileNotFoundException
import kotlin.concurrent.thread
fun main(args: Array<String>) {

    val internalBasePath = "D:\\"
    val externalBasePath = "D:\\DevMenuWebTest\\external"

    val english = GameLanguageType.English.invoke

    start(8080, PlatformImpl(english), internalBasePath, externalBasePath)

}

private fun ApplicationCall.getParams(param: String): String? = request.queryParameters[param]

// Список query параметров в массив
private fun ApplicationCall.getParamsAsList(): List<Any>{
    val list = mutableListOf<Any>()
    this.request.queryParameters.forEach { _, strings ->
        list.add(strings[0])
    }
    return list
}

private fun ApplicationCall.containsParam(param: String): Boolean = this.request.queryParameters.contains(param)


//Используется в методе GET
private suspend fun ApplicationCall.file(api: PlatformAPI, basePath: String) {
    val its = this

    this.getParams("folder")
        .runCatching {
            if (its.containsParam("info"))
                its.respond(api.getInfo(this!!, basePath))
            else {
                if (api.isFile(this!!, basePath))
                    its.respondFile(api.openFile(this, basePath))
                else
                    its.respond(api.getFile(this, basePath))
            }
        }
        .onFailure {
            System.err.println(it)

            when (it) {
                is FileNotFoundException -> its.respond(HttpStatusCode.NotFound)
                is NullPointerException -> its.respond(HttpStatusCode.BadRequest)
            }

        }
        .onSuccess {
            its.respond(HttpStatusCode.OK)
        }
}


//Метод для создания и сохранения файла
private suspend fun ApplicationCall.ioFile(api: PlatformAPI, basePath: String) {
    val its = this

    //this.request.
    this.getParams("folder")
        .runCatching {
            if(its.containsParam("save") && its.containsParam("create"))
                its.respond(HttpStatusCode.BadRequest)

            if(its.containsParam("save")){
                val channel = its.request.receiveChannel()
                api.saveFile(this!!, basePath, channel.readRemaining().inputStream())
            }
            if(its.containsParam("create")){
                api.createFile(this!!, basePath)
            }

        }
        .onSuccess { its.respond(HttpStatusCode.OK) }
        .onFailure {
            println(it)
            its.respond(HttpStatusCode.NotFound)
        }
}

//Используется в методе DELETE
private suspend fun ApplicationCall.remove(api: PlatformAPI, basePath: String) {
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

//Блок настроек

//POST
 private suspend fun ApplicationCall.setLang(api: PlatformAPI){
    val language = receive<GameLanguage>()
    api.setLanguage(language)
    this.respond(HttpStatusCode.OK)
 }

//GET
private suspend fun ApplicationCall.getLang(api: PlatformAPI){
    respond(api.getLanguage())
}


private fun HTML.index() {
    head { title("DevMenu") }
    body {
        div { id = "root" }
        script(src = "/static/DevMenuWeb.js") {}
    }
}

fun start(port: Int, api: PlatformAPI, internalPath: String, externalPath: String) {
    thread {
        embeddedServer(CIO, port) {

            install(DefaultHeaders) { header(HttpHeaders.Server, "From DevMenuServer With Love!") }

            //install(CallLogging) { level = Level.INFO }

            install(ContentNegotiation) { json() }

            install(Compression) { gzip() }

            routing {
                get("/") {
                    call.respondHtml(HttpStatusCode.OK, HTML::index)
                }
                static("/static") { resources() }

                route("/files") {
                    route("/internal") {
                        post { call.ioFile(api, internalPath) }     //Создание и cохранение файла
                        get { call.file(api, internalPath) }        //Получение инфы о ФС
                        delete { call.remove(api, internalPath) }   //Удаление файла
                    }
                    route("/external") {
                        post { call.ioFile(api, externalPath) }
                        get { call.file(api, externalPath) }
                        delete { call.remove(api, externalPath) }
                    }
                }
                route("/options") {
                    route("/lang") {
                        post { call.setLang(api) } //Поменять текущий язык
                        get { call.getLang(api) } //Получить текущий язык
                    }
                }

            }
        }.start(wait = true)
    }
}
