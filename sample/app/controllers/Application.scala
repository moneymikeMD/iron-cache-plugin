package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import cache.Cache

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def cacheSet(value: String) = Action {
    Cache.set("test", value)
    Ok(value + " set.")
  }

  def cacheGet() = Action {
    Cache.get("test") match {
      case Some(value) => Ok("Cache value found: " + value)
      case None => Ok("Cache value not found")
    }
  }

  def cacheDelete() = Action {
    Cache.remove("test")
    Ok("Removed.")
  }
  
}