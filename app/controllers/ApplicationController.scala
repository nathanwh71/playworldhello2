package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class ApplicationController @Inject()(cc:ControllerComponents) extends AbstractController(cc){

  def mvt(z:Int, x:Int, y:Int) = Action.async{

  }

}
