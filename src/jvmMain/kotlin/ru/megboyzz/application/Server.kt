package ru.megboyzz.application

import entities.GameLanguage
import entities.Replacement
import entities.SaveTrackingInfo
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import kotlinx.html.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.megboyzz.factory.ApiFactory
import java.io.FileNotFoundException
import kotlin.concurrent.thread


fun main(args: Array<String>) {
    val replacement = Replacement(0, "lol", "lol1")
    val tracking = SaveTrackingInfo(true,2000, "/path/to")
    println(Json.encodeToString(tracking))
    println(Json.encodeToString(replacement))
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
private suspend fun ApplicationCall.file(basePath: String) {
    val its = this
    val api = apiFactory.createFileAPI()
    this.getParams("folder")
        .runCatching {
            val path = basePath + this!!
            if (its.containsParam("info"))
                its.respond(api.getInfo(path))
            else {
                if (api.isFile(path))
                    its.respondFile(api.openFile(path))
                else
                    its.respond(api.getFile(path))
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
private suspend fun ApplicationCall.ioFile(basePath: String) {
    val its = this

    val api = apiFactory.createFileAPI()
    this.getParams("folder")
        .runCatching {
            if(its.containsParam("save") && its.containsParam("create"))
                its.respond(HttpStatusCode.BadRequest)

            if(its.containsParam("save")){
                val channel = its.request.receiveChannel()
                api.saveFile(basePath + this, channel.readRemaining().inputStream())
            }
            if(its.containsParam("create")){
                api.createFile(basePath + this)
            }

        }
        .onSuccess { its.respond(HttpStatusCode.OK) }
        .onFailure {
            println(it)
            its.respond(HttpStatusCode.NotFound)
        }
}

//Используется в методе DELETE
private suspend fun ApplicationCall.remove(basePath: String) {

    val api = apiFactory.createFileAPI()

    this
        .getParams("folder")
        .runCatching {
            api.removeFile(basePath + this)
        }
        .onFailure {
            this.respond(HttpStatusCode.NotFound)
        }
        .onSuccess { this.respond(HttpStatusCode.OK) }
}

//Блок настроек

//POST
 private suspend fun ApplicationCall.setLang(){
    val language = receive<GameLanguage>()
    apiFactory.createOptionsAPI().setLanguage(language)
    this.respond(HttpStatusCode.OK)
 }

//GET
private suspend fun ApplicationCall.getLang(){
    respond(apiFactory.createOptionsAPI().getLanguage())
}


private fun HTML.index() {
    head { title("DevMenu") }
    body {
        div { id = "root" }
        script(src = "/static/DevMenuWeb.js") {}
    }
}

lateinit var apiFactory: ApiFactory

fun start(port: Int, factory: ApiFactory, internalPath: String, externalPath: String) {
    apiFactory = factory
    thread {

        embeddedServer(CIO, port) {

            install(DefaultHeaders) { header(HttpHeaders.Server, "From DevMenuServer With Love!") }

            install(ContentNegotiation) { json() }

            install(Compression) { gzip() }

            routing {
                get("/") {
                    call.respondHtml(HttpStatusCode.OK, HTML::index)
                }
                static("/static") { resources() }

                //TODO оформить доступ к файлам в виде REST
                // и перенести их в стейт
                route("/files") {
                    route("/internal") {
                        post { call.ioFile(internalPath) }     //Создание и cохранение файла
                        get { call.file(internalPath) }        //Получение инфы о ФС
                        delete { call.remove(internalPath) }   //Удаление файла
                    }
                    route("/external") {
                        post { call.ioFile(externalPath) }
                        get { call.file(externalPath) }
                        delete { call.remove(externalPath) }
                    }

                    route("/replacement"){
                        val replacementsAPI = apiFactory.createReplacementsAPI()
                        post {
                            kotlin.runCatching {
                                val replacement = call.receive<Replacement>()
                                replacementsAPI.addReplacement(replacement)
                            }
                                .onFailure {
                                    println(it)
                                    call.respond(HttpStatusCode.BadRequest)
                                }
                                .onSuccess { call.respond(HttpStatusCode.OK) }
                        }          // Добавление замены
                        delete {
                            if(!call.containsParam("id")) call.respond(HttpStatusCode.BadRequest)
                            val params = call.getParams("id")
                            replacementsAPI.recoverReplacementByID(params!!.toInt())
                            call.respond(HttpStatusCode.OK)
                        }       // Воосталение замены
                        route("/all"){
                            get {
                                val allReplacements = replacementsAPI.getAllReplacements()
                                call.respond(allReplacements)
                            }       // Получить список всех замен
                            delete {
                                replacementsAPI.recoverAllReplacements()
                                call.respond(HttpStatusCode.OK)
                            }   // Воостановить все
                        }
                    }
                }
                route("/options") {

                    val options = factory.createOptionsAPI()

                    route("/save"){
                        get{
                            call.respondFile(options.getSaveFile())
                        }
                        post{
                            val channel = call.request.receiveChannel()
                            println("channel is " + channel.availableForRead)
                            options.setSaveFile(channel.readRemaining().inputStream())
                            call.respond(HttpStatusCode.OK)
                        }
                    }

                    route("/lang") {
                        post { call.setLang() } //Поменять текущий язык
                        get { call.getLang() } //Получить текущий язык
                    }
                    route("/tracking"){

                        get{
                            call.respond(options.getSaveTrackingInfo())
                        }
                        post{
                            call.receive<SaveTrackingInfo>()
                                .runCatching { options.setSaveTrackingInfo(this) }
                                .onSuccess { call.respond(HttpStatusCode.OK) }
                                .onFailure { call.respond(HttpStatusCode.BadRequest) }
                        }

                    } //TODO сделать отслеживание файла

                    route("/svmw"){
                        post{

                        }
                        get{

                        }
                    }
                }



                //TODO files - это тоже стейт(Возможно:))
                route("/state"){
                    route("/system"){
                        get{
                            call.respondText("Система перегружена!!!")
                            //TODO реализовать подгрузку состояния устройства
                        }
                    }
                }

            }
        }.start(wait = true)
    }
}
