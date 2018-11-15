package controllers

import javax.inject._
import play.api.Configuration
import play.api.libs.oauth.{ConsumerKey, OAuthCalculator, RequestToken}
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()( ws : WSClient,
                                config: Configuration,
                                cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def tweets = Action.async {

    val credentials: Option[(ConsumerKey, RequestToken)] = for {
       apiKey <- config.getString("twitter.apiKey")
       apiSecret <-  config.getString("twitter.apiSecret")
       token <- config.getString("twitter.token")
       tokenSecret <- config.getString("twitter.tokenSecret")
    } yield (
      ConsumerKey(apiKey, apiSecret),
      RequestToken(token, tokenSecret)
    )

    credentials match {
      // Future.successful runs on the current thread in a blocking manner.
      case Some((consumerKey, requestToken)) => {
          ws
          .url("https://stream.twitter.com/1.1/statuses/filter.json")
          .sign(OAuthCalculator(consumerKey, requestToken))
          .withQueryStringParameters("track"-> "reactive")
          .get()
          .map { response => {
            Ok(response.body)
          }
          }
      }
      case None => Future.successful { InternalServerError("Twitter credential missing")}
    }



   /* credentials.map { case (consumerKey, requestToken) =>
        Future.successful {
          Ok
        }
    }.getOrElse {
        Future.successful{
         InternalServerError("Twitter credential missing")
      }
    }*/
  }

}
