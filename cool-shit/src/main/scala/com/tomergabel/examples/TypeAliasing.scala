package com.tomergabel.examples

import net.liftweb.json.DefaultFormats

/**
 * Created by tomer on 7/2/12.
 */

object TypeAliasing {

    trait Module {
        type Configuration
        def configManifest: Manifest[ Configuration ]
        def initialize( config: Configuration )
    }

    case class DatabaseConfiguration( host: String, user: String, schema: String )
    class DatabaseModule extends Module {
        type Configuration = DatabaseConfiguration
        def configManifest = manifest[ Configuration ]
        def initialize( config: DatabaseConfiguration ) {
            println( "Database module starting up (host=%s, user=%s, schema=%s)".format(
                config.host, config.user, config.schema ) )
        }
    }

    case class FileConfiguration( path: String )
    class FileModule extends Module {
        type Configuration = FileConfiguration
        def configManifest = manifest[ Configuration ]
        def initialize( config: FileConfiguration ) {
            println( "File module starting up (path=" + config.path + ")" )
        }
    }

    def loadModule[ T <: Module : Manifest ]( json: String ): T = {
        val module = manifest[ T ].erasure.newInstance().asInstanceOf[ T ]
        val config = net.liftweb.json.parse( json ).extract( DefaultFormats, module.configManifest )
        module.initialize( config )
        module
    }

    def main( args: Array[ String ] ) {
        val db = loadModule[ DatabaseModule ]( """{"host":"localhost", "user":"tomer", "schema":"test"}""" )
        val file = loadModule[ FileModule ]( """{"path":"/tmp/test"}""" )
    }
}
