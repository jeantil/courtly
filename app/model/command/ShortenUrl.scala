package model.command

case class ShortenUrl(url: String) extends Command {
  override def id = url
}
