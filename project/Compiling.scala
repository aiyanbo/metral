import Dependencies.Versions._
import sbt.Keys._
import sbt.{ AutoPlugin, _ }

object Compiling extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] = Seq(
    crossScalaVersions := Seq(scala211, scala212, scala213))

}
