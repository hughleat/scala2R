# Scala2R

## Description
Scala2R is a thin Scala wrapper around JRI.  It allows a slightly more natural use of R from Scala for some common 
functions.  For further functionality you may need to delve deeper into JRI.

The wrapper requires R and JRI to be installed and for use to have both the JRI jar files in the classpath and the
JRI native libraries in the library path.

The functionality supported at the moment is based on what I currently use for producing graphs and statistics for my
experiments.  If you need more, please ask me.

## Usage
Typical usage is:

val r = com.hjl.scala2R.R()
import r._
pdf( new File( "blah.pdf" ))
plot( Array( 1, 2, 3, 3, 4 ))

## Function arguments
Functions are called with variable arguments.  Each argument is either a value or a pair where the first element is a
symbol or string and the second is a value.  The pair format allow for named arguments to be passed to R. 

Values are one of 
* None - Becomes `null` in R
* Boolean
* Float
* Double
* Int
* Long
* String
* java.io.File
* java.awt.Color
* Sequence of Number
* Sequence of String
* Symbol - The symbol text will be used verbatim in the R code.  Typically this is for variable names, but can be used
  for more general R escaping purposes

So, if you would call in R `pdf( "hello.pdf", paper="a4", width=5 )`, then in Scala you would call:
`pdf( new File( "hello.pdf" ), 'paper->"a4", 'width->5 )`.

Similarly, if you want `plot( c( 1,2,2.5 ), c( 4,2,3 ), col="#FF0000" )`, then you would use: 
`plot( Array( 1,2,2.5 ), Array( 4,2,3 ), 'col->new Color( 255, 0, 0 ))`.
