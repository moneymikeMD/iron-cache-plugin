package com.github.tmwtmp100.cache

import play.api.cache.{CacheAPI, CachePlugin}
import play.api.Application
import play.api.libs.ws.WS
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class IronCachePlugin(app: Application) extends CachePlugin{
  val hostAddress = app.configuration.getString("iron.cache.host").getOrElse("cache-aws-us-east-1")
  val cacheName   = app.configuration.getString("iron.cache.name").getOrElse("cache")
  val oAuthToken  = app.configuration.getString("iron.token").get
  val projectId   = app.configuration.getString("iron.project.id").get

  val baseAddress  = "https://" + hostAddress + ".iron.io/1/projects/" + projectId + "/caches/" + cacheName
  val clearAddress = baseAddress + "/clear"
  val address      = baseAddress + "/items/"

  val auth = ("Authorization", "OAuth " + oAuthToken)
  val jsonCT = ("Content-Type","application/json")

  val api = new CacheAPI {


    def set(key: String, value: Any, expiration: Int) {
      play.Logger.info(address + key)
      WS.url(address + key).withHeaders(auth, jsonCT)
        .put(Json.obj("value" -> value.asInstanceOf[String]))
        .onComplete(x =>
            play.Logger.info(x.get.body)
        )
    }

    def get(key: String): Option[String] = {
      val ironGet = WS.url(address + key).withHeaders(auth)
        .get().map { response =>
            (response.json \ "value").asOpt[String]
        }
      Await.result(ironGet, Duration.Inf)
    }

    def remove(key: String) {
      WS.url(address + key).withHeaders(auth)
        .delete()
    }

  }

  def increment(key: String, amount: Int) {
    play.Logger.info("Increment\n" + address + key + "/increment")
    WS.url(address + key + "/increment").withHeaders(auth)
      .post(Json.stringify(Json.obj("amount" -> amount)))
  }

  def clearCache() {
    WS.url(clearAddress).withHeaders(auth).post("")
  }

  override def onStart() {
    play.Logger.info("Iron Cache Plugin started")
    super.onStart()
  }

  override def onStop() {
    super.onStop()
  }

  override def enabled: Boolean = {
    val relatedKeys = List("iron.token", "iron.project.id")
    val isEnabled: Boolean = {
      relatedKeys.count { key =>
        app.configuration.getString(key).isInstanceOf[Some[String]]
      } == relatedKeys.size
    }

    play.Logger.info("Iron Cache Plugin is enabled? " + isEnabled)
    isEnabled
  }
}