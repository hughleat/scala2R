import sbt._

class Scala2RProject( info : ProjectInfo ) extends DefaultProject( info ) {
    val rHome = System.getenv( "R_HOME" )
    if( rHome == null ) {
        log.warn( "Environment variable R_HOME is not set.  This could mean that R is not installed." )
    }
    val rJava = {
        val env = System.getenv( "R_JAVA" )
        if( env == null ) {
            log.info( "Environment variable R_JAVA is not set.  This should be the directory where the RJava jars are found." )
            if( rHome != null ) rHome
            else {
                log.error( "R_JAVA cannot default to R_HOME." )
                error( "R_JAVA" )
            }
        } else env
    }
    log.info( "JRI jars will be searched for in " + rJava )
    
    val scalatest = "org.scalatest"  % "scalatest"  % "1.2"
    val scalacheck = "org.scala-tools.testing" % "scalacheck_2.8.0" % "1.7"
	override def unmanagedClasspath = super.unmanagedClasspath +++ Path.fromFile( rJava ) ** "*.jar"
}
