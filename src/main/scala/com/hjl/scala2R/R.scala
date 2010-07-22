package com.hjl.scala2R

import java.io.File
import org.rosuda.REngine.JRI._
import org.rosuda.REngine._
import java.awt.Color

class R( engine : REngine )  extends java.io.Closeable {
	def parseAndEval( expr : String ) = try {
		println( expr )
		engine.parseAndEval( expr ) 
	} catch { 
		case e => throw new RuntimeException( expr, e ) 
	}
    def apply( expr : String ) = parseAndEval( expr )
	def :=[ T ]( p : ( Symbol, T )) : ( Symbol, T ) = {
        p match {
            case ( n, v : Array[ Double ] ) => engine.assign( n.name, v )
            case ( n, v : Array[ String ] ) => engine.assign( n.name, v )
            case ( n, v : Iterable[ Double ] ) => engine.assign( n.name, v.toArray )
        }
        p
    }
    
    def fn( name : String, args : Arg* ) = {
    	var nameIndex = 0;
    	def nextName = { nameIndex += 1; "..t" + nameIndex }
    	def escape( s : String ) : String = {
    	    val sb = new StringBuilder
    	    for( c <- s ) c match {
    	        case '\'' => sb append "\\\'"
    	        case _ => sb append c
    	    }
    	    sb.toString
    	}
    	def val2str( x : Arg ) : String = x match {
    		case null => "NULL"
    		case Arg( None ) => null
    		case Arg( true ) => "TRUE"
    		case Arg( false ) => "FALSE"
    		case Arg( v : String ) => "'" + escape( v ) + "'"
    		case Arg( v : File ) => "'" + escape( v.getAbsolutePath ) + "'"
    		case Arg( v : Color ) => ( "'#" + "%02x" * 4 + "'" ).format( v.getRed, v.getGreen, v.getBlue, v.getAlpha )
    		case Arg( v : Float ) => v.toString
    		case Arg( v : Double ) => v.toString
    		case Arg( v : Int ) => v.toString
    		case Arg( v : Long ) => v.toString
    		case Arg( v : Array[ Double ] ) => val temp = nextName; engine.assign( temp, v ); temp
    		case Arg( v : Array[ String ] ) => val temp = nextName; engine.assign( temp, v ); temp
    		case Arg( Symbol( v )) => v
    		case Arg(( n : String, null )) => n + "=" + val2str( null )
    		case Arg(( n : String, x : Arg )) => n + "=" + val2str( x )
    	}
    	val expr = ( args map val2str ).filter( _ != null ) mkString ( name + "(", ",", ")" )
    	parseAndEval( expr )
    }
    
    case class Arg( x : Any )
    implicit def none2arg( n : Option[ Nothing ] ) = Arg( n )
    implicit def bool2arg( v : Boolean ) = Arg( v )
    implicit def float2arg( v : Float ) = Arg( v )
    implicit def double2arg( v : Double ) = Arg( v )
    implicit def int2arg( v : Int ) = Arg( v )
    implicit def long2arg( v : Long ) = Arg( v )
    implicit def string2arg( s : String ) = Arg( s )
    implicit def file2arg( f : File ) = Arg( f )
    implicit def symbol2arg( s : Symbol ) = Arg( s )
    implicit def color2arg( c : Color ) = Arg( c )
    implicit def symnull2arg( p : ( Symbol, Null )) = Arg( p )
    implicit def numseq2arg[ A, B >: A ]( x : Seq[ A ] )( implicit num : Numeric[ B ] ) = Arg( x.map( num.toDouble( _ )).toArray )
    implicit def numarr2arg[ A, B >: A ]( x : Array[ A ] )( implicit num : Numeric[ B ] ) = Arg( x.map( num.toDouble( _ )) )
    implicit def dblarr2arg( x : Array[ Double ] ) = Arg( x )
    implicit def strseq2arg( x : Seq[ String ] ) = Arg( x.toArray )
    implicit def strarr2arg( x : Array[ String ] ) = Arg( x )
    implicit def symarg2arg[ T <% Arg ]( p : ( Symbol, T )) = strarg2arg(( p._1.name, p._2 ))
    implicit def strnull2arg( p : ( String, Null )) = Arg( p )
    implicit def strarg2arg[ T <% Arg ]( p : ( String, T )) = {
    	def cast( a : Arg ) = a
    	Arg(( p._1, cast( p._2 )))
    }
    
    def verbatim( x : String ) = Symbol( x )
    def pdf( args : Arg* ) = fn( "pdf", args : _* )
    def plot( args : Arg* ) = fn( "plot", args : _* )
    def hist( args : Arg* ) = fn( "hist", args : _* )
    def barplot( args : Arg* ) = fn( "barplot", args : _* )
    def points( args : Arg* ) = fn( "points", args : _* )
    def lines( args : Arg* ) = fn( "lines", args : _* )
    def abline( args : Arg* ) = fn( "abline", args : _* )
    def hline( h : Arg, args : Arg* ) = fn( "abline", Arg(( "h", h )) +: args : _* )
    def vline( v : Arg, args : Arg* ) = fn( "abline", Arg(( "v", v )) +: args : _* )
    def par( args : Arg* ) = fn( "par", args : _* )
    def vioplot( args : Arg* ) = { library( "vioplot" ); fn( "vioplot", args : _* ) }
    def segments( args : Arg* ) = fn( "segments", args : _* )
    def text( args : Arg* ) = fn( "text", args : _* )
    def close = engine.close
    def library( name : String ) = parseAndEval( "library(" + name + ")" )
    def javaGD() = { library( "JavaGD" ); parseAndEval( "JavaGD()" ) }
    object dev { def off = parseAndEval( "dev.off()" ) }
}

object R {
    def apply( engine : REngine ) = new R( engine )
    def apply() = new R( new JRIEngine( Array( "--no-save", "--slave" ), new REngineStdOutput, false ))
}
