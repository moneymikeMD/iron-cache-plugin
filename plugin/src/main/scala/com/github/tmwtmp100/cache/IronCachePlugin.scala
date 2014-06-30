package com.github.tmwtmp100.cache

import play.api.cache.{CacheAPI, CachePlugin}
import play.api.Application
import play.api.libs.ws.WS
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import play.api.Play.current

class IronCachePlugin(app: Application) extends CachePlugin{
  val hostAddress = app.configuration.getString("iron.cache.host").getOrElse("cache-aws-us-east-1")
  val cacheName   = app.configuration.getString("iron.cache.name").getOrElse("cache")
  val oAuthToken  = app.configuration.getString("iron.token").get
  val projectId   = app.configuration.getString("iron.project.id").get

  private val projectAddress = "https://" + hostAddress + ".iron.io/1/projects/" + projectId
  private val baseAddress  = projectAddress + "/caches/" + cacheName
  private val clearAddress = baseAddress + "/clear"
  private val address      = baseAddress + "/items/"

  private val auth = ("Authorization", "OAuth " + oAuthToken)
  private val jsonCT = ("Content-Type","application/json")
  private val appName = "Iron Cache Plugin"

  val api = new CacheAPI {


    def set(key: String, value: Any, expiration: Int) {
      play.Logger.info(address + key)

      val typedValue: JsValue = value match {
        case i: String => JsString(value.asInstanceOf[String])
        case i: Int =>    JsNumber(value.asInstanceOf[Int])
        case i: Double => JsNumber(value.asInstanceOf[Double])
        case i: Boolean => JsBoolean(value.asInstanceOf[Boolean])
        case _ => JsNull
      }
      WS.url(address + key).withHeaders(auth, jsonCT)
        .put(Json.obj("value" -> typedValue, "expires_in" -> expiration))
        .onComplete(x =>
            x.get.status match {
              case 200 => {}
              case _ => play.Logger.warn(appName + " experienced a problem setting " + key + ":" + x.get.json \ "msg")
            }
        )
    }

    def get(key: String): Option[String] = {
      val ironGet = WS.url(address + key).withHeaders(auth)
        .get().map { response =>
            response.status match {
              case 200 => (response.json \ "value").asOpt[String]
              case _ => {
                play.Logger.debug(appName + " could not retrieve key " + key + ":" + response.json \ "msg")
                None
              }
            }
        }
      Await.result(ironGet, Duration.Inf)
    }

    def remove(key: String) {
      WS.url(address + key).withHeaders(auth)
        .delete()
    }

  }

  def increment(key: String, amount: Int): Option[Int] = {
    play.Logger.debug("Increment\n" + address + key + "/increment")
    val incrementCall = WS.url(address + key + "/increment").withHeaders(auth, jsonCT)
                          .post(Json.obj("amount" -> amount))
                          .map { response =>
                            response.status match {
                              case 200 => (response.json \ "value").asOpt[Int]
                              case _ => {
                                play.Logger.error(appName + " experienced an error while incrementing " + key + ":" + response.json \ "msg")
                                None
                              }
                            }
                          }

    Await.result(incrementCall, Duration.Inf)
  }

  def clearCache() {
    WS.url(clearAddress).withHeaders(auth).post("")
  }

  override def onStart() {
    play.Logger.info(appName + " started.")
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

    isEnabled match {
      case true => {
        play.Logger.info(appName + " has been enabled.")
        // Now check to see if the service is reachable
        WS.url(projectAddress).withHeaders(auth).get().map { response =>
          if(response.status > 500){
            play.Logger.error("Iron Cache service is unresponsive. The plugin will not work.")
            throw new IllegalAccessException()
          }
        }
      }
      case _ => play.Logger.warn(appName + " is not enabled due to missing required properties. " +
                                  "Check to see if the token and project ID have been set.")
    }
    isEnabled
  }
}