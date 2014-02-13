package model.query

trait Query
case class ResolveToken(token: String) extends Query
