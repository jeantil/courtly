import play.api.GlobalSettings
import play.api.mvc.{ WithFilters, EssentialFilter }
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.DefaultDB

object LoggingFilter extends EssentialFilter {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import play.api.Logger
  import play.api.mvc.EssentialAction
  import play.api.mvc.RequestHeader

  val logger = Logger("application.timer")
  def excludedUris = """"""
  def apply(nextFilter: EssentialAction) = new EssentialAction {
    def apply(requestHeader: RequestHeader) = {
      val startTime = System.currentTimeMillis
      nextFilter(requestHeader).map { result =>
        val endTime = System.currentTimeMillis
        val requestTime = endTime - startTime
        if (!requestHeader.uri.matches(excludedUris)) {
          logger.info(s"${requestHeader.method} ${requestHeader.uri}" +
            s" took ${requestTime}ms and returned ${result.header.status}")
          result.withHeaders("Request-Time" -> requestTime.toString)
        }
        else {
          result
        }
      }
    }
  }
}

object Global extends WithFilters(LoggingFilter) with GlobalSettings {

  import play.api.Application
  import reactivemongo.api.DefaultDB

  override def onStart(app: Application): Unit = {
    super.onStart(app)
    app.plugin[ReactiveMongoPlugin] match {
      case Some(plugin) => ensureIndexes(plugin.helper.db)
      case _            => ()
    }
  }
  private def ensureIndexes(db: DefaultDB) {
    import model.ShortUrl
    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    db.indexesManager.onCollection("shortUrls").create(ShortUrl.ShortUrlByTokenIndex)
    db.indexesManager.onCollection("shortUrls").create(ShortUrl.ShortUrlByTargetIndex)
  }
}
