package model.event

case class UrlStatFound(accessCount: Int) extends Event
case object UrlStatNotFound extends Event
