package com.tomergabel.examples.bullet4

import scala.NoSuchElementException

/**
 * Created by tomer on 5/20/14.
 */
object SimplifiedCollection extends App {

  // API --

  trait Iterator[ T ] {
    def hasNext: Boolean
    def next: T
  }

  trait Iterable[ T ] {
    def getIterator: Iterator[ T ]
    def first: T                      // Or throw NoSuchElementException
    def firstOption: Option[ T ]
    def last: T                       // Or throw NoSuchElementException
    def lastOption: Option[ T ]
    def size: Int
  }


  // Base implementation in a trait --

  trait BaseIterable[ T ] extends Iterable[ T ] {
    def firstOption = {
      val it = getIterator
      if ( it.hasNext )
        Some( it.next )
      else
        None
    }

    def first = firstOption.getOrElse( throw new NoSuchElementException )

    def lastOption = {
      val it = getIterator
      if ( !it.hasNext )
        None
      else {
        var value = it.next
        while ( it.hasNext ) value = it.next
        Some( value )
      }
    }

    def last = lastOption.getOrElse( throw new NoSuchElementException )

    def size = {
      val it = getIterator
      var count = 0
      while ( it.hasNext ) {
        count += 1
        it.next
      }
      count
    }
  }

  // Sample implementations --

  case class ArrayList[ T ]( array: Array[ T ] ) extends BaseIterable[ T ] {
    def getIterator = new Iterator[ T ] {
      var index = 0

      def hasNext = index < array.length

      def next =
        if ( hasNext ) {
          val value = array( index )
          index += 1
          value
        } else
          throw new NoSuchElementException
    }
  }

  case class StringIterator( s: String, split: Char ) extends BaseIterable[ String ] {
    def getIterator = new Iterator[ String ] {
      var at = 0

      def hasNext = at < s.length

      def next = {
        val nextIndex = s.indexOf( split, at )
        val token = if ( nextIndex > 0 ) s.substring( at, nextIndex ) else s.substring( at )
        at = if ( nextIndex > 0 ) nextIndex + 1 else s.length
        token
      }
    }
  }

  // Call site and validation

  val stringIter = StringIterator( "I do not like green eggs and ham", ' ' )
  assert( stringIter.first == "I" )
  assert( stringIter.last == "ham" )
  assert( stringIter.size == 8 )

  val list = ArrayList( Array( "I", "do", "not", "like", "green", "eggs", "and", "ham" ) )
  assert( list.first == "I" )
  assert( list.last == "ham" )
  assert( list.size == 8 )

  val emptyList = new ArrayList( Array.empty )
  assert( emptyList.firstOption == None )
  assert( emptyList.lastOption == None )
  assert( emptyList.size == 0 )
  try { emptyList.first; assert( false ) } catch { case _: NoSuchElementException => }
  try { emptyList.last ; assert( false ) } catch { case _: NoSuchElementException => }
}
