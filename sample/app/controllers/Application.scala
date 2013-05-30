package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import cache.Cache
import com.github.tmwtmp100.cache.IronCachePlugin

object Application extends Controller {

  private val ironPlugin = play.api.Play.current.plugin[IronCachePlugin].get
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def cacheSetExpiration(value: String) = Action {
    Cache.set("test", value, 3600)
    Ok(value + " set.")
  }

  def cacheSetString(key: String, value: String) = Action {
    Cache.set(key, value)
    Ok("Set " + key +  " to " + value)
  }

  def cacheSetInt(key: String, value: Int) = Action {
    Cache.set(key, value)
    Ok("Set " + key + " to " + value)
  }

  def cacheGet() = Action {
    Cache.get("test") match {
      case Some(value) => Ok("Cache value found: " + value)
      case None => Ok("Cache value not found")
    }
  }

  def cacheIncrement(key: String, incVal: Int) = Action {
    ironPlugin.increment(key, incVal) match {
      case Some(amount) => Ok("Cache value increased by " + incVal + " by " + amount)
      case _ => Ok("Error with key inc-key while incrementing value.")
    }
  }

  def clearCache() = Action {
    ironPlugin.clearCache()
    Ok("Cache has been cleared.")
  }

  def cacheDelete() = Action {
    Cache.remove("test")
    Ok("Removed.")
  }
  
}