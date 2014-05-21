package com.tomergabel.examples.bullet4

/**
 * Created by tomer on 5/20/14.
 */
trait Logging {
  private val log = org.slf4j.LoggerFactory.getLogger( this.getClass )

  protected def logInfo ( message: => String ) = if ( log.isInfoEnabled  ) log.info ( message )
  protected def logDebug( message: => String ) = if ( log.isDebugEnabled ) log.debug( message )
  protected def logWarn ( message: => String ) = if ( log.isWarnEnabled  ) log.warn ( message )
  protected def logError( message: => String ) = if ( log.isErrorEnabled ) log.error( message )
}
