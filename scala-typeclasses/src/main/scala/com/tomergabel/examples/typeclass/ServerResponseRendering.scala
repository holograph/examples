package com.tomergabel.examples.typeclass

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}


object ServerResponseRendering {

  trait ResponseRenderer[ T ] {
    def render( value: T, request: HttpServletRequest, response: HttpServletResponse ): Unit
  }

  implicit object StringRenderer extends ResponseRenderer[ String ] {
    def render( value: String, request: HttpServletRequest, response: HttpServletResponse ): Unit = {
      val w = response.getWriter
      try w.write( value )
      finally w.close()
    }
  }

  implicit object IntRenderer extends ResponseRenderer[ Int ] {
    def render( value: Int, request: HttpServletRequest, response: HttpServletResponse ): Unit =
      implicitly[ ResponseRenderer[ String ] ].render( value.toString, request, response )
  }

  trait NicerHttpServlet extends HttpServlet {

    private trait Handler {
      type Response
      def result: Response
      def renderer: ResponseRenderer[ Response ]
    }

    private var handlers: Map[ String, Handler ] = Map.empty

    protected def get[ T : ResponseRenderer ]( url: String )( thunk: => T ) =
      handlers += url -> new Handler {
        type Response = T
        def result = thunk
        def renderer = implicitly[ ResponseRenderer[ T ] ]
      }

    override def doGet( req: HttpServletRequest, resp: HttpServletResponse ) = {
      handlers.get( req.getRequestURI ) match {
        case None =>
          resp.sendError( HttpServletResponse.SC_NOT_FOUND )

        case Some( handler ) =>
          try handler.renderer.render( handler.result, req, resp )
          catch {
            case e: Exception => resp.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR )
          }
      }
    }
  }

  object Showcase {
    case class DTO( message: String )

    implicit object DTORenderer extends ResponseRenderer[ DTO ] {
      def render( value: DTO, request: HttpServletRequest, response: HttpServletResponse ) =
        implicitly[ ResponseRenderer[ String ] ].render( value.message, request, response )
    }

    class MyServlet extends NicerHttpServlet {
      get( "dto" ) { DTO( "hello, world!" ) }
    }
  }

}
