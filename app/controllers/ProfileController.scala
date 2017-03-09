package controllers

import com.google.inject.Inject
import play.api.cache.CacheApi
import play.api.mvc.{Action, Controller}

/**
  * Created by knoldus on 7/3/17.
  */
class ProfileController @Inject()(cache: CacheApi) extends Controller {

  def showProfile = Action { implicit request =>

    request.session.get("connected").map { email =>

      val userDataOption = cache.get[Map[String, String]](email)

      val userData = userDataOption.fold(Map.empty[String, String])(identity)

      if (userData.nonEmpty && userData("userType") != "admin") {
        Ok(views.html.userprofile(userData))
      }
      else if (userData.nonEmpty && userData("userType") == "admin") {
        Ok(views.html.adminprofile(userData)(cache.get("listOfUsers").getOrElse(List()))(cache))
      }
      else {
        Redirect(routes.HomeController.index())
      }
    }.getOrElse {
      Redirect(routes.SignupController.showSignupForm())
    }
  }
}
